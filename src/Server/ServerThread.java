/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Channel.Base64Channel;
import Channel.IChannel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import Exceptions.AESException;
import Exceptions.KeyNotFoundException;
import Exceptions.WrongPasswordException;
import Protocol.CommandProtocol;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.util.encoders.Base64;

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
    private SecureChannel secureChannel;
    private String pathToClientKeyDir;
    private PrivateKey myPrivKey;
    private PublicKey clientPubKey;
    private int clientPort;
    private byte[] sentServerChallenge, receivedServerChallenge;
    private SecretKey sessionKey;
    private byte[] ivParam;
    private static SecureRandom secureRandom;

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
                System.out.println(">received from User: "+inputLine);
                processMsg = true;
                outputLine = "couldn't process input";

                if (inputLine.startsWith("!login")) {
                    String[] input = inputLine.split(" ");
                    String username = input[1];
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
                        String challenge = bytes2String(rndNr64);
                        outputLine += " " + challenge;
                        
                        if (createSessionKey()) {
                            String secKey = bytes2String(encodeBase64(sessionKey.getEncoded()));
                            outputLine += " " + secKey;
                            
                            String ivParam64 = bytes2String(encodeBase64(ivParam));
                            outputLine += " " + ivParam64;
                            try {
                                secureChannel.setSessionKey(sessionKey, ivParam);
                            } catch (AESException ex) {
                                System.err.println(ex.getMessage());
                            }
                            System.out.println(">ServerThread sending: " + outputLine);
                        }

                    } catch (KeyNotFoundException ex) {
                        System.out.println(ex.getMessage());
                        //outputLine = ex.getMessage();
                        processMsg = false;
                    }
                } else {
                    outputLine = cp.processInput(inputLine);
                }
                //System.out.println(outputLine);
                //out.println(outputLine);
                if (processMsg) {
                    secureChannel.send(outputLine);
                }

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
    private static byte[] string2Bytes(String message) {
        return message.getBytes();
    }

    private static String bytes2String(byte[] byteMessage) {
        return new String(byteMessage);
    }

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
