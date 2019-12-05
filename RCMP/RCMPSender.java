import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.PortUnreachableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.nio.ByteBuffer;

/**
 * This class implements a sender that sends a file over UDP
 * using the RCMP protocol as defined by Professor Norman
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPSender {

    public static final int PACKETSIZE = 1450;
    public static final int HEADERSIZE = 13;
    public static final int ACKSIZE = 8;

    public static void main(String[] args) {

        // make sure the user specifies the correct
        // number of command line arguments
        if (args.length < 3) {
            System.err.println("Usage: java RCMPSender <hostName> <portNum> <fileName>");
            System.exit(0);
        }

        String hostName = args[0];

        int portNum = 22222;

        // make sure the port number specified is an integer
        try {
            portNum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Cannot convert " + args[1] + " to int to use for port number");
            System.exit(0);
        }

        // create a File object, determine its total size (in bytes), and open a stream
        // for reading
        String fileName = args[2];
        File openFile = new File(fileName);
        int fileSize = (int) openFile.length();
        RandomAccessFile fin = null;
        DatagramSocket socket = null;

        // try to open the specified file for reading
        // and create a UDP socket for sending packets
        try {
            fin = new RandomAccessFile(openFile, "rw");
            socket = new DatagramSocket();
            // set the socket to throw an exception when it doesn't
            // receive a packet when it expects to
            socket.setSoTimeout(250);
        } catch (SocketException e) {
            System.err.println("Error creating socket with port number " + portNum + ": " + e);
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(0);
        }

        // set up variables used for packet creation and sending
        byte[] buffer = null, ackBuffer = null;
        DatagramPacket packetToSend = null, ackToReceive = null;
        ByteBuffer byteBuffer = null, ackByteBuffer = null;
        Random theMostRandom = new Random();
        int eof = 1, connectionID = theMostRandom.nextInt(), packetNum = 0, receivedID = -1, receivedPacketNum = -1,
                gapCounter = 0, nonAckedPackets = 0, lastAckedPacket = -1, filePosition = -1;
        byte packetShouldBeAcked = (byte) 1;

        // loop until we send a packet smaller than the defined PACKETSIZE
        while (true) {

            try {

                // loop to 'waste' time so the messages don't get sent too fast for the
                // receiver to handle them
                // TODO: find a better way to do this or remove it
                // int j = 0;
                // for (int i = 0; i < 300000; i++) {
                // j += i;
                // }

                // set up the buffer used for datagram sending and fill its header
                buffer = new byte[PACKETSIZE + HEADERSIZE];
                byteBuffer = ByteBuffer.wrap(buffer);
                byteBuffer.putInt(connectionID);
                byteBuffer.putInt(fileSize);
                byteBuffer.putInt(packetNum);

                // set up buffer and bytebuffer used for receiving acks
                ackBuffer = new byte[ACKSIZE];
                ackByteBuffer = ByteBuffer.wrap(ackBuffer);

                // make sure to figure out how much data we read in
                eof = fin.read(buffer, HEADERSIZE, PACKETSIZE);

                // make sure the packet is acked when it's the last one
                if (eof < PACKETSIZE)
                    packetShouldBeAcked = (byte) 1;
                byteBuffer.put(packetShouldBeAcked);

            } catch (IOException e) {
                System.err.println("Error receiving data from file: " + e);
                System.exit(0);
            }

            try {
                // create the packet with the data and send it
                packetToSend = new DatagramPacket(buffer, eof + HEADERSIZE, InetAddress.getByName(hostName), portNum);
                socket.send(packetToSend);

                // receive an ACK packet from the receiver if we marked the packet to be acked
                if (packetShouldBeAcked == (byte) 1) {
                    ackToReceive = new DatagramPacket(ackBuffer, ACKSIZE);
                    socket.receive(ackToReceive);
                    receivedID = ackByteBuffer.getInt();
                    receivedPacketNum = ackByteBuffer.getInt();

                    // when we ack a packet, increase the gap counter, reset the number of
                    // packets that have not been acked, and mark the next packets to not be acked
                    gapCounter++;
                    nonAckedPackets = 0;
                    packetShouldBeAcked = (byte) 0;
                    lastAckedPacket = packetNum;

                    // make sure the ACK packet has the correct connection id
                    if (receivedID != connectionID) {
                        System.err.println("ACK not received");
                        System.exit(0);
                    } else {
                        System.out.println("Received ID: " + receivedID + ", received packetNum: " + receivedPacketNum);
                    }

                    // if we don't receive an ACK packet, increment the counter of non-acked packet
                } else {
                    nonAckedPackets++;
                }

                // mark the next one to be acked only if we have reached the gap counter - 1
                if (nonAckedPackets == (gapCounter - 1))
                    packetShouldBeAcked = (byte) 1;

            } catch (PortUnreachableException e) {
                System.err.println("Error reaching port: " + e);
                System.exit(0);

                // if the socket has timed out, reset our gap counter info
                // and where in the file we are reading data from
            } catch (SocketTimeoutException e) {
                // do stuff to reset and handle the timeout
                gapCounter = 0;
                nonAckedPackets = 0;
                packetShouldBeAcked = (byte) 1;
                packetNum = lastAckedPacket;
                filePosition = (packetNum + 1) * PACKETSIZE;
                try {
                    fin.seek(filePosition);
                } catch (IOException e2) {
                    System.err.println("Error seeking to position " + filePosition + ": " + e2);
                    System.exit(0);
                }
            } catch (IOException e) {
                System.err.println("Error receiving data from socket: " + e);
                System.exit(0);
            }

            // increment the packet number counter
            packetNum++;

            // if we've sent a packet that isn't 'full', break
            if (eof < PACKETSIZE) {
                break;
            }

        }

        socket.close();
    }
}