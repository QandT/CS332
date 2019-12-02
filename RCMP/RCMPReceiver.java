import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

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

		// open the desired file for writing

		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();
			socket.connect(new InetSocketAddress("localhost", portNum));
		} catch (SocketException e) {
			System.err.println(String.format("Error creating socket with port number %d: " + e.getMessage(), portNum));
			System.exit(0);
		}

		byte[] buffer = null;
		DatagramPacket receivedPacket = null;

		while (true) {
			try {
				buffer = new byte[1450];
				receivedPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(receivedPacket);
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