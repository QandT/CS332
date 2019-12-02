import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramChannel;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * This class implements a receiver
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPReceiver {

	public static void main(String[] args) {

		if (args.length < 2) {
			System.err.println("Usage: java RCMPReceiver <portNum> <fileName>");
			System.exit(0);
		}

		int portNum = 22222;

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

		try {
			fout = new FileOutputStream(openFile);
			socket = new DatagramSocket();
			// socket.connect(new InetSocketAddress("localhost", portNum));
			socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), portNum));
		} catch (SocketException e) {
			System.err.println("Error creating socket with port number " + portNum + ": " + e);
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println("File not found" + e);
			System.exit(0);
		} catch (UnknownHostException e) {
			System.err.println("Host not found" + e);
			System.exit(0);
		}

		byte[] buffer = null;
		DatagramPacket receivedPacket = null;

		while (true) {
			try {
				buffer = new byte[1450];
				receivedPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(receivedPacket);
				fout.write(buffer);
				System.out.println(receivedPacket.getData());
				if (receivedPacket.getLength() < 1450) {
					break;
				}
			} catch (IOException e) {
				System.err.println("Error receiving data from socket: " + e.getMessage());
				System.exit(0);
			}

		}
	}

}