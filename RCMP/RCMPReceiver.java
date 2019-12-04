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
	public static final int HEADERSIZE = 13;
	public static final int ACKSIZE = 8;

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

		// create a File object and a stream for writing to it
		String fileName = args[1];
		File openFile = new File(fileName);
		FileOutputStream fout = null;

		DatagramSocket socket = null;

		// try to open the specified file for writing
		// and connect a UDP socket to the specified port
		try {
			fout = new FileOutputStream(openFile);
			socket = new DatagramSocket(portNum);
			// TODO: remove if we don't use
			// socket.setReceiveBufferSize(212992); // maybe do this if we need to
		} catch (SocketException e) {
			System.err.println("Error creating socket with port number " + portNum + ": " + e);
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e);
			System.exit(0);
		}

		// set up variables used for packet receiving
		byte[] buffer = new byte[PACKETSIZE + HEADERSIZE], ackBuffer = new byte[ACKSIZE];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		ByteBuffer ackByteBuffer = ByteBuffer.wrap(ackBuffer);
		DatagramPacket receivedPacket = null, ackToSend = null;
		int portToAck = -1;
		InetAddress ipToAck = null;
		int connectionID = -1, packetNum = -1, filesize = -1, bytesRecieved = 0, payloadSize = -1;
		byte toAck;

		// loop until we have received the complete file
		while (true) {

			try {

				// receive the packet
				receivedPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(receivedPacket);

				// extract the header information and payload size info
				connectionID = byteBuffer.getInt();
				filesize = byteBuffer.getInt();
				packetNum = byteBuffer.getInt();
				toAck = byteBuffer.get();		

				payloadSize = receivedPacket.getLength() - HEADERSIZE;
				bytesRecieved += payloadSize;

				// create an ACK packet and send it to the original sender
				// if the last packet was marked to be acked
				if (toAck == (byte)1) {
					portToAck = receivedPacket.getPort();
					ipToAck = receivedPacket.getAddress();
					ackByteBuffer.putInt(connectionID);
					ackByteBuffer.putInt(packetNum);
					ackToSend = new DatagramPacket(ackBuffer, ACKSIZE, ipToAck, portToAck);
					socket.send(ackToSend);
				}				

				// write the datagram payload to the file
				fout.write(buffer, HEADERSIZE, payloadSize);

				// break if we have received the whole file
				if (bytesRecieved == filesize) {
					break;
				} 

				// clear the buffers and re-wrap the bytebuffers
				buffer = new byte[PACKETSIZE + HEADERSIZE];
				byteBuffer = ByteBuffer.wrap(buffer);

				ackBuffer = new byte[ACKSIZE];
				ackByteBuffer = ByteBuffer.wrap(ackBuffer);

			} catch (IOException e) {
				System.err.println("Error receiving data from socket: " + e.getMessage());
				System.exit(0);
			}

		}

		socket.close();
	}

}