/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Channel.Base64Channel;
import Channel.IChannel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import Protocol.CommandProtocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author daniela
 */
//Thread der die Kommunikation mit dem Client handhabt.
public class ServerThread extends Thread {

    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    private CommandProtocol cp;
    private IChannel secureChannel;
    
    public ServerThread(Socket socket) { //, String analyticsBindingName, String billingBindingName) {
        super("ServerThread");
        this.socket = socket;
        
        //this.analyticsBindingName = analyticsBindingName;
        //this.billingBindingName = billingBindingName;
        
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));

        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }

    }

    @Override
    public void run() {

        String inputLine, outputLine;

        try {

           /* if ((inputLine = in.readLine()) != null) {
                try {
                    //System.out.println("tcp port: "+socket.getPort());
                    udpPort = Integer.parseInt(inputLine);
                    dataSocket = new DatagramSocket();
                    //System.out.println("udpPort: " + udpPort);
                } catch (NumberFormatException e) {
                    out.println("Problem with udpPort");
                }
            }*/
                    cp = new CommandProtocol(this); //, analyticsBindingName, billingBindingName);


            //while Client is sending - answer
            while ((inputLine = secureChannel.receive()) != null) {    //in.readLine()) != null) {
                outputLine = cp.processInput(inputLine);
                //System.out.println(outputLine);
                //out.println(outputLine);
                secureChannel.send(outputLine);

            }

        } catch (IOException e) {
            //System.out.println(e);
            System.err.println("Problem with connection.");
        } catch (NullPointerException e) {
            //Client closed Connection
        } finally {
            close();
        }

    }

    /*public synchronized void sendNotification(String notification) {
        if (dataSocket != null) {
            buf = notification.getBytes();
            dataPacket = new DatagramPacket(buf, buf.length, socket.getInetAddress(), udpPort);
            try {
                dataSocket.send(dataPacket);
            } catch (IOException ex) {
            }
        } else {
            //System.out.println("no data socket, note " + notification);
        }
    }*/

    protected void close() {
        try {
            cp.processInput("!logout");
            out.close();
            in.close();
            socket.close();
            secureChannel.close();
            //System.out.println("Connection closed!");
        } catch (IOException e) {
            System.err.println("Problem with disconnection from " + socket.getInetAddress().toString());
        }
    }
}
