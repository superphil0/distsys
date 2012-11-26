/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Protocol.CommandProtocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    private int udpPort;
    private DatagramSocket dataSocket = null;
    private DatagramPacket dataPacket = null;
    private byte[] buf;

    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }

    }

    @Override
    public void run() {

        String inputLine, outputLine;

        try {

            if ((inputLine = in.readLine()) != null) {
                try {
                    //System.out.println("tcp port: "+socket.getPort());
                    udpPort = Integer.parseInt(inputLine);
                    dataSocket = new DatagramSocket();
                    //System.out.println("udpPort: " + udpPort);
                } catch (NumberFormatException e) {
                    out.println("Problem with udpPort");
                }
            }
                    cp = new CommandProtocol(this);


            //while Client is sending - answer
            while ((inputLine = in.readLine()) != null) {
                outputLine = cp.processInput(inputLine);
                //System.out.println(outputLine);
                out.println(outputLine);

            }

        } catch (IOException e) {
            System.out.println(e);
            System.err.println("Problem with connection.");
        } finally {
            close();
        }

    }

    public synchronized void sendNotification(String notification) {
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
    }

    protected void close() {
        try {
            cp.processInput("!logout");
            out.close();
            in.close();
            socket.close();

            //System.out.println("Connection closed!");
        } catch (IOException e) {
            System.err.println("Problem with disconnection from " + socket.getInetAddress().toString());
        }
    }
}
