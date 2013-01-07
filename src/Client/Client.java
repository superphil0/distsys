/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Channel.Base64Channel;
import Channel.IChannel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    private static IChannel secureChannel;
    private static PublicKey publicKeyServer = null;
    private static PrivateKey privateKeyClient = null;
    protected static File ServerKeydirectory = null;
    protected static File ClientKeydirectory = null;

    public static void main(String[] args) throws IOException {
        //args should contain host, tcpPort, udpPort

        if (args.length < 2) {
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

            System.out.println("Active Socket Conntection to Server!");
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


        //read pub key from server and my priv key
        //try {




        //}

        if (ok) {

            /*
             * Starting TCP + UDP Threads
             */
            // Start TCP Thread

            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));

            //ClientThreadTCP ctTCP = new ClientThreadTCP(in);
            ClientThreadTCP ctTCP = new ClientThreadTCP(secureChannel);

            ctTCP.start();

            //User Input
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;

            try {
 
                //receiving User-Commands until input is null --> shut down
                while ((fromUser = stdIn.readLine().trim()) != null) {// && !fromUser.isEmpty()) {

                    if (fromUser.equals("")) {
                        System.out.println("no input - please enter a command!");
                        
                    } else if (fromUser.equals("!end")) {
                        break;//shut down
                        
                    } else if (fromUser.startsWith("!login")) {
                        //get UserName, readPriv Key, require Password
                        //base64 encode all parameters - decode am server how?!
                        //
                    } else {


                        secureChannel.send(fromUser);

                    }

                }

            } catch (IOException e) {
                System.err.println("I/O Fehler");
            } finally {
                close();
                System.out.println("Bye.");
            }
        }
    }

    // For secure connection - lab3
    public BufferedReader getInputReader() {
        return in;
    }

    public PrintWriter getOutputWriter() {
        return out;
    }

    private static void close() {
        try {
            out.close();
            in.close();
            stdIn.close();
            socket.close();
            secureChannel.close();
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.err.println("Problem with disconnection.");
            //System.exit(1);
        }
    }
}
