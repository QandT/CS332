import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.io.IOException;

/**
 * This class implements a sender
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPSender {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Usage: java RCMPSender <hostName> <portNum> <fileName>");
            System.exit(0);
        }

        String hostName = args[0];

        int portNum = 22222;

        try {
            portNum = Integer.parseInt(args[1]); 
        } catch (NumberFormatException e) {
            System.err.println("Cannot convert " + args[1] + " to int to use for port number");
            System.exit(0);
        }

        String fileName = args[2];

        // open the desired file for writing

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            socket.connect(new InetSocketAddress(hostName, portNum));
        } catch (SocketException e) {
            System.err.println(String.format("Error creating socket with port number %d: " + e.getMessage(), portNum));
            System.exit(0);
        }

        byte[] buffer = null;
        DatagramPacket receivedPacket = null;

        while (true) {
            try {
                buffer = new byte[1450];
                String test = "teststringtosendtootherthing";
                buffer = test.getBytes();
                receivedPacket = new DatagramPacket(buffer, buffer.length);
                socket.send(receivedPacket);
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