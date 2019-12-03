import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.PortUnreachableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class implements a sender
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPSender {

    public static final int PACKETSIZE = 1450;
    public static final byte[] ACK = "ACK".getBytes();

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

        String fileName = args[2];

        File openFile = new File(fileName);
        FileInputStream fin = null;

        DatagramSocket socket = null;

        // try to open the specified file for reading
        // and create a UDP socket for sending packets
        try {
            fin = new FileInputStream(openFile);
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Error creating socket with port number " + portNum + ": " + e);
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(0);
        }

        byte[] buffer = null, ackBuffer = null;
        DatagramPacket packetToSend = null, ackToReceive = null;
        int eof = 1;

        // loop until we send a packet smaller than the defined PACKETSIZE
        while (true) {

            try {
                buffer = new byte[PACKETSIZE];
                ackBuffer = new byte[ACK.length];
                // make sure to figure out how much data we read in
                eof = fin.read(buffer);
            } catch (IOException e) {
                System.err.println("Error receiving data from file: " + e);
                System.exit(0);

            }

            try {
                // create the packet with the data and send it
                packetToSend = new DatagramPacket(buffer, eof, InetAddress.getByName(hostName), portNum);
                socket.send(packetToSend);

                // receive an ACK packet from the receiver
                ackToReceive = new DatagramPacket(ackBuffer, ACK.length);
                socket.receive(ackToReceive);

                // make sure the ACK packet has 'ACK' in it
                if (new String(ackToReceive.getData()).compareTo("ACK") != 0) {
                    System.err.println("ACK not received");
                    System.exit(0);
                } else {
                    System.out.println("ACK");
                }
            } catch (PortUnreachableException e) {
                System.err.println("Error reaching port: " + e);
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Error receiving data from socket: " + e);
                System.exit(0);
            }

            // if we've sent a packet that isn't 'full', break
            if (eof < PACKETSIZE) {
                break;
            }

        }

        socket.close();
    }
}