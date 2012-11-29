/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author daniela
 */
public class Client {

    private static Socket socket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static String host = "";
    private static int tcpPort;
    private static BufferedReader stdIn;
    private static boolean ok = false;

    public static void main(String[] args) throws IOException {
        //args should contain host, tcpPort, udpPort

    	if(args.length < 2)
    	{
    		System.out.println("Please enter hostname + hostport to connect to auction server");
    		System.exit(0);
    	}
        try {
            host = args[0];
            tcpPort = Integer.parseInt(args[1]);
            //udpPort = Integer.parseInt(args[2]);

            //TCP Connection
            socket = new Socket(host, tcpPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //UDP Connection
           // dataSocket = new DatagramSocket(udpPort);
           // address = InetAddress.getByName(host);

            System.out.println("Connected successfully to Server!");
            ok = true;

        } catch (NumberFormatException e) {                     //and udp
            System.err.println("Please enter only Numbers for TCP Port.");
            //System.exit(1);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host + ".");
            //System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host + ".");
            //System.exit(1);
        }

        if (ok) {
            /*
             * Starting TCP + UDP Threads
             */
            // Start TCP Thread
            ClientThreadTCP ctTCP = new ClientThreadTCP(socket);
            ctTCP.start();

            // Start UDP Thread
            //ClientThreadUDP ctUDP = new ClientThreadUDP(udpPort);
            //ctUDP.start();



            //User Input
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;

            try {
                
                //System.out.println(udpPort+"");
                //out.println(udpPort);

                //receiving User-Commands until input is null --> shut down
                while ((fromUser = stdIn.readLine()) != null) {// && !fromUser.isEmpty()) {
                    if (fromUser.equals("!end")) {
                        //shut down
                        break;
                    }

                    out.println(fromUser);

                }

            } catch (IOException e) {
                System.err.println("I/O Fehler");
            } finally {
                close();
                System.out.println("Bye.");
            }
        }
    }

    private static void close() {
        try {
            out.close();
            in.close();
            stdIn.close();
            socket.close();
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.err.println("Problem with disconnection.");
            //System.exit(1);
        }
    }
}
