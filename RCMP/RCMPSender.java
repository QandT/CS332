import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
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

        File openFile = new File(fileName);
        FileInputStream fin = null;

        DatagramSocket socket = null;

        try {
            fin = new FileInputStream(openFile);
            socket = new DatagramSocket();
            // socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), portNum));
        } catch (SocketException e) {
            System.err.println("Error creating socket with port number " + portNum + ": " + e);
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.err.println("File not found" + e);
            System.exit(0);
            // } catch (UnknownHostException e) {
            // System.err.println("Host not found" + e);
            // System.exit(0);
        }

        byte[] buffer = null;
        DatagramPacket receivedPacket = null;
        int eof = 1;
        int i = 0;

        while (true) {
            try {
                Thread.sleep(1);
                i++;
                buffer = new byte[1450];
                eof = fin.read(buffer);
            } catch (IOException e) {
                System.err.println("Error receiving data from file: " + e);
                System.exit(0);
            } catch (InterruptedException e) {
                System.err.println("Error STOPPING: " + e);
            }

            try {
                receivedPacket = new DatagramPacket(buffer, eof, InetAddress.getLocalHost(), portNum);
                socket.send(receivedPacket);
                System.out.println(i + " " + eof);
            } catch (PortUnreachableException e) {
                System.err.println("Error receiving port: " + e);
                e.printStackTrace();
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Error receiving data from socket: " + e);
                System.exit(0);
            }

            if (eof < 1450) {
                break;
            }

        }
        socket.close();
    }
}