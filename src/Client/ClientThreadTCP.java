/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Channel.SecureChannel;
import Exceptions.AESException;
import Exceptions.HMacException;
import java.io.IOException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author daniela listening for Server response over TCP
 */
public class ClientThreadTCP extends Thread {
    // private Socket socket = null;
    //private BufferedReader in = null;

    private String fromServer;
    private static SecureChannel secureChannel;
    private Client clientCallback;
    private byte[] sentClientChallenge, receivedClientChallenge;
    private SecretKey sessionKey;
    private byte[] ivParam;
	private Client client;

    public ClientThreadTCP(Client client) {
        secureChannel = clientCallback.getSecureChannel();
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //While Server is still answering, print message

            //(fromServer = in.readLine()) != null) {// && socket.isConnected()){// && !fromServer.isEmpty()) {
            while ((fromServer = secureChannel.receive()) != null) {
                if (fromServer.startsWith("!ok")) {
                    //TODO compare client challenge, get server challenge
                    //System.out.println(">ClientTreadTCP: ok received");
                    //System.out.println(fromServer);
                    String[] input = fromServer.split(" ");
                    receivedClientChallenge = Client.decodeBase64(input[1].getBytes());

                    if (Arrays.equals(sentClientChallenge, receivedClientChallenge)) {
                        byte[] sKey = Client.decodeBase64(input[3].getBytes());
                        this.sessionKey = new SecretKeySpec(sKey, "AES");
                        this.ivParam = Client.decodeBase64(input[4].getBytes());
                        try {
                            secureChannel.setSessionKey(sessionKey, ivParam);
                            secureChannel.send(input[2]);
                        } catch (HMacException ex) {
                            System.err.println(ex.getMessage());
                        } catch (AESException ex) {
                            System.err.println(ex.getMessage());
                        }

                    } else {
                        System.out.println("Received Client Challenge differs from Sent one.");
                    }
                    //System.out.println(fromServer);

                } else if (fromServer.startsWith("!resending")) { //do nothing and wait for resending
                    //System.out.println("resending: " + Client.getMessageBuffer());
                    //secureChannel.send(Client.getMessageBuffer());
                    
                }  else if (fromServer.startsWith("!confirmed") || fromServer.startsWith("!rejected")) { 
                   client.setAlive(); 
                   System.out.println(fromServer);
                } else {
                    System.out.println(fromServer);
                }
            }

        } catch (IOException e) {
            //System.err.println("Couldn't get output from Server.");
        } catch (NullPointerException ex) {
            System.out.println("Lost Connection to Server... ");
        } finally {
            //closes TCP connection when there is no response
            close();
        }
    }

    public void setClientChallenge(byte[] sentClientChallenge) {
        this.sentClientChallenge = sentClientChallenge;
    }

    public void close() {
        try {
            //System.out.println("closing connection to Server.");
            //in.close();
            secureChannel.close();
            //socket.close();
        } catch (IOException e) {
            System.err.println("Problem while closing");
            //System.exit(1);
        }

    }
}
