/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author daniela
 *
public class ClientThreadUDP extends Thread {

    private byte[] buf = new byte[256];
    private DatagramPacket packet;
    private String notification;
    private int udpPort;
    private DatagramSocket dataSocket;

    public ClientThreadUDP(int udpPort) throws IOException{
        this.udpPort = udpPort;
        dataSocket = new DatagramSocket(udpPort);
    }

    public void run() {

        //run until break
        while (true) {
            try {
                //receive packets
                packet = new DatagramPacket(buf, buf.length);
                dataSocket.receive(packet);
                notification = new String(packet.getData(), 0, packet.getLength());
                System.out.println(notification);
            } catch (IOException e) {
                 break;
            }
        }
        
        //close Socket if there is nothing more to receive
        dataSocket.close();

    }
}
*/