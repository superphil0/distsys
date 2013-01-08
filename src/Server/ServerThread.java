/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Channel.Base64Channel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import Exceptions.AESException;
import Exceptions.KeyNotFoundException;
import Protocol.CommandProtocol;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author daniela
 */
//Thread der die Kommunikation mit dem Client handhabt.
public class ServerThread extends Thread {

    private Socket socket = null;
    public Socket getSocket() {
		return socket;
	}

	private PrintWriter out;
    private BufferedReader in;
    private CommandProtocol cp;
    private SecureChannel secureChannel;
    private String pathToClientKeyDir;
    private PrivateKey myPrivKey;
    private PublicKey clientPubKey;
    private int clientPort;
    private byte[] sentServerChallenge, receivedServerChallenge;
    private SecretKey sessionKey;
    private byte[] ivParam;
    private static SecureRandom secureRandom;
    private String loginBuffer;
    private boolean firstAESmsg = false;
    private String messageBuffer;
    private String username;

    public ServerThread(Socket socket, PrivateKey privKey, String pathToClientKeyDir) { //, String analyticsBindingName, String billingBindingName) {
        super("ServerThread");
        this.socket = socket;

        this.myPrivKey = privKey;
        this.pathToClientKeyDir = pathToClientKeyDir;
        secureRandom = new SecureRandom();


        //this.analyticsBindingName = analyticsBindingName;
        //this.billingBindingName = billingBindingName;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));
            secureChannel.setPrivKey(myPrivKey);
            secureChannel.setPathToClientKeyDir(pathToClientKeyDir);

        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }

    }

    @Override
    public void run() {

        String inputLine, outputLine;


        try {

            cp = new CommandProtocol(this); //, analyticsBindingName, billingBindingName);

            //proccessing client input
            boolean processMsg;
            while ((inputLine = secureChannel.receive()) != null) {    //in.readLine()) != null) {
                System.out.println(">received from User: " + inputLine);
                processMsg = true;
                outputLine = "couldn't process input";

                if (inputLine.startsWith("!login")) {
                    String[] input = inputLine.split(" ");
                    loginBuffer = input[0] + " " + input[1]; //!login username
                    username = input[1];
                    try {
                        //TODO set to secureChannel
                        clientPubKey = readClientsPubKey(pathToClientKeyDir, username);
                        secureChannel.setPubKey(clientPubKey);


                        try {
                            clientPort = Integer.parseInt(input[2]);
                        } catch (NumberFormatException nfe) {
                            System.err.println("PortNumber has to be a number! E.g. Server <Port>");
                        }


                        outputLine = "!ok " + input[3]; // + other stuff

                        sentServerChallenge = generateSecureRandom();
                        byte[] rndNr64 = encodeBase64(sentServerChallenge);
                        String challenge = new String(rndNr64);
                        outputLine += " " + challenge;

                        if (createSessionKey()) {
                            String secKey = new String(encodeBase64(sessionKey.getEncoded()));
                            outputLine += " " + secKey;

                            String ivParam64 = new String(encodeBase64(ivParam));
                            outputLine += " " + ivParam64;
                            secureChannel.send(outputLine);
                            System.out.println(">ServerThread sending: " + outputLine);
                            messageBuffer = outputLine;
                            processMsg = false;
                            try {
                                secureChannel.setSessionKey(sessionKey, ivParam, username);
                                firstAESmsg = true;
                            } catch (AESException ex) {
                                System.err.println(ex.getMessage());
                            }
                        }

                    } catch (KeyNotFoundException ex) {
                        System.out.println(ex.getMessage());
                        //outputLine = ex.getMessage();
                        processMsg = false;
                    }
                } else if (firstAESmsg) { //server challenge expected
                    receivedServerChallenge = decodeBase64(inputLine.getBytes());
                    if (Arrays.equals(sentServerChallenge, receivedServerChallenge)) {
                        firstAESmsg = false;
                        outputLine = cp.processInput(loginBuffer);
                    } else {
                        outputLine = "!failed to login. Sent und received Server Challenge dont conquer.";
                        secureChannel.removeSessionKey();
                        secureChannel.setPrivKey(myPrivKey);
                    }

                } else if (inputLine.startsWith("!retransmit")) {
                    outputLine = messageBuffer;
                } else {
                    outputLine = cp.processInput(inputLine);
                }
                //System.out.println(outputLine);
                //out.println(outputLine);
                if (processMsg) {
                    if (inputLine.startsWith("!logout")) {
                        secureChannel.removeSessionKey();
                        secureChannel.setPrivKey(myPrivKey);
                    }

                    secureChannel.send(outputLine);
                    messageBuffer = outputLine;
                    System.out.println(">ServerThread sending: " + outputLine);

                }

            }

        } catch (IOException e) {
            //System.out.println(e);
            System.err.println("Problem with connection.");
        } catch (NullPointerException e) {
            //Client closed Connection
        } catch (Exception e) {
            System.err.println("Sorry, an error occured..." + e.getStackTrace());
        } finally {
            close();
        }


    }
    public void sendMessage(String message)
    {
    	secureChannel.send(message);
    }
    private PublicKey readClientsPubKey(String pathToClientKeyDir, String userName) throws KeyNotFoundException {
        String pathToPublicKey = pathToClientKeyDir + "/" + userName + ".pub.pem";
        PublicKey publicKey = null;
        PEMReader in;
        try {
            in = new PEMReader(new FileReader(pathToPublicKey));
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            throw new KeyNotFoundException("Wrong path to Private Server Key");
        }

        try {
            publicKey = (PublicKey) in.readObject();
        } catch (IOException ex) {
            throw new KeyNotFoundException("Couldn't read Key");
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return publicKey;
    }

    private static byte[] generateSecureRandom() {
        // generates a 32 byte secure random number 
        final byte[] number = new byte[32];
        secureRandom.nextBytes(number);
        return number;

    }

    private boolean createSessionKey() {
        try {
            //create SecretKey
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            sessionKey = generator.generateKey();
            //create IV value
            ivParam = new byte[16];
            secureRandom.nextBytes(ivParam);
            return true;
        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ServerThread: Couldn't create Session Key!");
            return false;
        }

    }

    //Helpers
    private static byte[] encodeBase64(byte[] byteMessage) {
        byte[] base64Message = Base64.encode(byteMessage);
        return base64Message;
    }

    private static byte[] decodeBase64(byte[] base64Message) {
        byte[] byteMessage = Base64.decode(base64Message);
        return byteMessage;
    }

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
