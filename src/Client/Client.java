/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Channel.Base64Channel;
import Channel.IChannel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import Exceptions.KeyNotFoundException;
import Exceptions.WrongPasswordException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author daniela
 */
public class Client {

    private static Socket socket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static String host = "";
    private static int tcpPort, clientPort;
    private static BufferedReader stdIn;
    private static boolean ok = false;
    private static SecureChannel secureChannel;
    private static PublicKey serverPubKey = null;
    private static PrivateKey myPrivKey = null;
    private static String pathToServerKey, pathToClientKeyDir;
    private static byte[] myChallenge, serverChallenge;
    private static String username;
    private static String messageBuffer;
    private final static String CHARSET  ="UTF-8";

    public Client()
    {
    	object = new Object();
    }
    public static void main(String[] args) throws IOException {
        //args should contain host, tcpPort, udpPort

        if (args.length != 5) {
            System.out.println("Invalid input arguments!"
                    + "Please enter hostname, hostport, clientport, pathToServerKey, clientKeyDir to start this client.");
            System.exit(0);
        } else {
            try {
                host = args[0];
                tcpPort = Integer.parseInt(args[1]);
                clientPort = Integer.parseInt(args[2]);
                pathToServerKey = args[3];
                pathToClientKeyDir = args[4];

                //TCP Connection
                socket = new Socket(host, tcpPort);
               
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET ), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));

                System.out.println("Active Socket Connection to Server!");
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
        }


        if (ok) {
            try {
                serverPubKey = readPublicServerKey(pathToServerKey);
                if (serverPubKey == null) {
                    System.out.println("Client: server pub key = null, there should be a key not found ex!");
                }
            } catch (KeyNotFoundException ex) {
                System.out.println(ex.getMessage());
                close();
            }
            /*
             * Starting TCP + UDP Threads
             */
            // Start TCP Thread

            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));
            secureChannel.setPubKey(serverPubKey);
            //ClientThreadTCP ctTCP = new ClientThreadTCP(in);
        } else {
            close();
        }
        Client client = new Client();
        client.startCommunication();

    }

    private static PublicKey readPublicServerKey(String pathToServerKey) throws KeyNotFoundException {
        PublicKey publicKey = null;
        PEMReader in;
        try {
            in = new PEMReader(new FileReader(pathToServerKey));
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            throw new KeyNotFoundException("Wrong path to Public Server Key");
        }

        try {
            publicKey = (PublicKey) in.readObject();
        } catch (IOException ex) {
            throw new KeyNotFoundException("Couldn't read Key");
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return publicKey;
    }

    private static PrivateKey getPrivateKey(String username) throws KeyNotFoundException, WrongPasswordException {

        String pathToPrivKey = pathToClientKeyDir + "/" + username + ".pem";
        PrivateKey privateKey = null;
        PEMReader pemIn;
        try {
            pemIn = new PEMReader(new FileReader(pathToPrivKey), new PasswordFinder() {
                public char[] getPassword() {
                    // reads the password from standard input for decrypting the private key
                    System.out.println("Enter pass phrase:");
                    try {
                    	char[] pw = stdIn.readLine().toCharArray();
                        return pw;
                    } catch (IOException ex) {
                        //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                }
            });
            if (pemIn == null) {
                throw new KeyNotFoundException("Couldn't read Key");
            }
        } catch (FileNotFoundException ex) {
            throw new KeyNotFoundException("No private key found. Login not possible.");
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
        KeyPair keyPair;
        try {
            keyPair = (KeyPair) pemIn.readObject();
        } catch (IOException ex) {
            throw new WrongPasswordException("Wrong Password, please try to login again!");
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        privateKey = keyPair.getPrivate();
        return privateKey;

    }

	private boolean alive = true;
	private Object object;
    public void startCommunication()
    {
    	ClientThreadTCP ctTCP = new ClientThreadTCP(this);

        ctTCP.start();

        //User Input
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;

        try {

            //receiving User-Commands until input is null --> shut down
            
            boolean sendMsg = true;
            while ((fromUser = stdIn.readLine().trim()) != null) {// && !fromUser.isEmpty()) {
            	if(!alive) continue;
                sendMsg = true;
                if (fromUser.equals("")) {
                    System.out.println("no input - please enter a command!");
                    sendMsg = false;

                } else if (fromUser.equals("!end")) {
                    break;//shut down

                } else {
                    if (fromUser.startsWith("!login")) {
                        //read Serv pub Key
                        //get UserName, readPriv Key, require Password
                        //create client challenge
                        //base64 encode all parameters - decode am server how?!
                        //
                        if (fromUser.split(" ").length == 2 && !secureChannel.hasSessionKey()) {
                            username = fromUser.split(" ")[1];
                            try {
                                myPrivKey = getPrivateKey(username);
                                if (myPrivKey != null) {
                                    secureChannel.setPrivKey(myPrivKey);

                                    fromUser += " " + clientPort;

                                    myChallenge = generateSecureRandom();
                                    ctTCP.setClientChallenge(myChallenge);
                                    byte[] rndNr64 = encodeBase64(myChallenge);
                                    String challenge = bytes2String(rndNr64);
                                    fromUser += " " + challenge;
                                    secureChannel.setUsername(username);
                                    secureChannel.setPath(pathToClientKeyDir);
                                    sendMsg = true;
                                }
                            } catch (KeyNotFoundException ex) {
                                System.out.println(ex.getMessage());
                                sendMsg = false;
                            } catch (WrongPasswordException ex) {
                                System.out.println(ex.getMessage());
                                sendMsg = false;
                            }
                        } else {
                            sendMsg = false;
                            if (secureChannel.hasSessionKey()) {
                                System.out.println("User " + username + " already logged in.");
                            } else {
                                System.out.println("Usage: !login <username>");
                            }
                        }

                    }

                    if (sendMsg) {
                        //System.out.println(">sending: " + fromUser);
                        secureChannel.send(fromUser);
                        messageBuffer = fromUser;
                        
                        if (fromUser.startsWith("!logout")) {
                            secureChannel.removeSessionKey();
                            username = null;
                        }
                        else if(fromUser.startsWith("!confirm"))
                        {    
                        	synchronized(object)
                        	{
                        	 alive = false;     
                        	}
                        }
                    }
                }

            }

        } catch (IOException e) {
            System.err.println("I/O Fehler");
        } finally {
            close();
        }
    }
    
    public void setAlive()
    {
    	synchronized(object)
    	{
    		this.alive = true;
    	}
    }
    public static SecureChannel getSecureChannel() {
        return secureChannel;
    }

    
    //Helpers
    public static byte[] string2Bytes(String message) {
        return message.getBytes();
    }

    public static String bytes2String(byte[] byteMessage) {
        return new String(byteMessage);
    }

    public static byte[] encodeBase64(byte[] byteMessage) {
        byte[] base64Message = Base64.encode(byteMessage);
        return base64Message;
    }

    public static byte[] decodeBase64(byte[] base64Message) {
        byte[] byteMessage = Base64.decode(base64Message);
        return byteMessage;
    }

    private static byte[] generateSecureRandom() {
        // generates a 32 byte secure random number 
        SecureRandom secureRandom = new SecureRandom();
        final byte[] number = new byte[32];
        secureRandom.nextBytes(number);
        return number;

    }
    
    public static String getMessageBuffer() {
        return messageBuffer;
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
            if (secureChannel != null) {
                secureChannel.close();
            }
            System.out.println("All Connections have been closed!");
            System.out.println("Bye.");
        } catch (IOException e) {
            System.err.println("Problem with closing connections.");
            //System.exit(1);
        } catch (NullPointerException e) {
            System.err.println("No Connection to Server.");
        }
    }
}
