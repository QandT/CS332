import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

/**
 * This class implements a receiver
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPReceiver {

	public static final int PACKETSIZE = 1450;
	public static final int HEADERSIZE = 12;
	public static final byte[] ACK = "ACK".getBytes();

	public static void main(String[] args) {

		// make sure the user specifies the correct
		// number of command line arguments
		if (args.length < 2) {
			System.err.println("Usage: java RCMPReceiver <portNum> <fileName>");
			System.exit(0);
		}

		int portNum = 22222;

		// make sure the port number specified is an integer
		try {
			portNum = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Cannot convert " + args[0] + " to int to use for port number");
			System.exit(0);
		}

		String fileName = args[1];

		File openFile = new File(fileName);
		FileOutputStream fout = null;

		DatagramSocket socket = null;

		// try to open the specified file for writing
		// and connect a UDP socket to the specified port
		try {
			fout = new FileOutputStream(openFile);
			socket = new DatagramSocket(portNum);
		} catch (SocketException e) {
			System.err.println("Error creating socket with port number " + portNum + ": " + e);
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e);
			System.exit(0);
		}

		byte[] buffer = new byte[PACKETSIZE + HEADERSIZE];
		ByteBuffer buffBoi = ByteBuffer.wrap(buffer);
		DatagramPacket receivedPacket = null, ackToSend = null;
		int portToAck = -1;
		InetAddress ipToAck = null;
		int connectionID = -1, packetNum = -1, filesize = -1, bytesRecieved = 0, payloadSize = -1;

		// loop until we get a packet smaller than the defined PACKETSIZE
		while (true) {

			try {

				// receive the packet
				receivedPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(receivedPacket);
				connectionID = buffBoi.getInt();
				filesize = buffBoi.getInt();
				packetNum = buffBoi.getInt();
				payloadSize = receivedPacket.getLength() - HEADERSIZE;

				bytesRecieved += payloadSize;

				// create an ACK packet and send it to the
				// original sender
				portToAck = receivedPacket.getPort();
				ipToAck = receivedPacket.getAddress();
				ackToSend = new DatagramPacket(ACK, ACK.length, ipToAck, portToAck);
				socket.send(ackToSend);

				fout.write(buffer, HEADERSIZE, payloadSize);
				// System.out.println("ID: " + connectionID + "\nFilesize: " + filesize +
				// "\nPacket num: " + packetNum
				// + "\nReamaining: " + buffBoi.remaining() + "\nLength: " +
				// receivedPacket.getLength() + "\n\n");
				// if it's not a full packet, it's the last one
				// so we write as much as we can and then break
				if (bytesRecieved == filesize) {
					break;
				} 
				// clear the buffer
				buffer = new byte[PACKETSIZE + HEADERSIZE];
				buffBoi = ByteBuffer.wrap(buffer);
			} catch (IOException e) {
				System.err.println("Error receiving data from socket: " + e.getMessage());
				System.exit(0);
			}

		}

		socket.close();
	}

}