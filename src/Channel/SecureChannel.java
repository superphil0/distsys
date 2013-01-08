/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import Exceptions.AESException;
import Exceptions.HMacException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author daniela
 */
public class SecureChannel extends TCPChannel {

    protected IChannel channel;
    private SecretKey secretKey;
    private byte[] ivParameter;
    private boolean hasSessionKey = false;
    private boolean listCommand = false;
    private PublicKey otherPubKey;
    private PrivateKey myPrivKey;
    private Cipher cEncrypt;
    private Cipher cDecrypt;
    private AES aesCrypter;
    private boolean logoutResponse;
    private AESChannel aesChannel;
    private String pathToClientKeyDir;
    private String username;
    private HMac hmacGenerator;
    private int retries = 0;
    private int test = 0;

    /*
     c1=Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding","BC");
     c2=Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding","BC");*/
    public SecureChannel(PrintWriter out, BufferedReader in) {
        super(out, in);
        initCipher();

    }

    //to delete?
    public SecureChannel(TCPChannel channel) {
        super(channel);
        this.channel = channel;
        initCipher();

    }

    private void initCipher() {
        try {
            cEncrypt = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            cDecrypt = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            System.out.println(">SecureChannel: Cipher algorithm found! ");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("RSA: No Such Algorithm.");
        } catch (NoSuchProviderException ex) {
            System.err.println("RSA: No Such Provider.");
        } catch (NoSuchPaddingException ex) {
            System.err.println("RSA: No Such Padding.");
        }
    }

    public void send(String message) {
        //TODO Encrypt message here
        if (!hasSessionKey) { //RSA pub encryption

            if (message.startsWith("!list")) { //no encryption
                listCommand = true;
                channel.send(message);
                return;
            } else if (listCommand || logoutResponse) {
                logoutResponse = false;
                listCommand = false;
                channel.send(message);
                return;
            } else if (message.startsWith("!login") || message.startsWith("!ok")) { //1 or 2 part of handshake - encrypt with pub.key
                //encrypt RSA
                try {
                    String encrypted = new String(cEncrypt.doFinal(message.getBytes()));
                    System.out.println(">encrypting: " + message);
                    channel.send(encrypted);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(SecureChannel.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error while trying to encrypt Message... please try again!");
                }

            } else { //nothing
                System.out.println("Pleas login properly, otherwise only !list is a possilbe command.");
                return;
            }

        } else { //AES encryption

            try {
                byte[] hmac64 = Base64.encode(hmacGenerator.getMac(message));
                String hmac = new String(hmac64);

                String encrypted = new String(aesCrypter.encryptAES(message.getBytes()));
                String toSend = encrypted + " " + hmac;
                channel.send(toSend);
                return;
            } catch (HMacException ex) {
                System.err.println(ex.getMessage());
                return;
            } catch (AESException ex) {
                System.err.println(ex.getMessage());
                return;
            }
            //aesChannel.send(message);

        }
        channel.send(message);

    }

    public String receive() throws IOException {

        //TODO Decrypt message here
        if (!hasSessionKey) { //RSA priv decryption
            String message = channel.receive(); //incoming message, already base 64 decoded

            if (message.startsWith("!list")) { //no encryption
                listCommand = true;
                return message;
            } else if (listCommand || logoutResponse) {
                logoutResponse = false;
                listCommand = false;
                return message;
            } else {
                listCommand = false;
                try {
                    //has to be decrypted with priv key (!login from client oder !ok from server)
                    String decrypted = new String(cDecrypt.doFinal(message.getBytes()));
                    return decrypted;
                } catch (Exception ex) {
                    System.out.println("Error while trying to decrypt Message...");
                    //Logger.getLogger(SecureChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        } else {//AES decryption
            System.out.println(">SecureChannel: AES decrypting");
            try {
                String[] incoming = channel.receive().split(" "); //incoming message, already base 64 decoded

                /*
                 System.arraycopy(bytes, bytes.length - 32, receivedMac, 0, 32);
                 System.arraycopy(bytes, 0, receivedMsg, 0, bytes.length - 32);
                 */

                String receivedHmac = incoming[incoming.length - 1];

                String message = "";
                for (int i = 0; i < incoming.length - 1; i++) {
                    message += incoming[i] + " ";
                }
                message = message.trim();
                //System.out.println(">receiving: " + message);
                String decrypted = new String(aesCrypter.decryptAES(message.getBytes()));

                String generatedHmac = new String(Base64.encode(hmacGenerator.getMac(decrypted)));
                System.out.println("received  " + receivedHmac + "\ngenerated " + generatedHmac);
                return decrypted;

                //if (!MessageDigest.isEqual(receivedHmac, generatedHmac)) {
               /* if (receivedHmac.equals(generatedHmac)) {
                 System.out.println(">Received valid HMAC!");
                 retries = 0;
                 return decrypted;
                 } else {
                 System.out.println(">Received message has wrong MAC.");
                 System.out.println("rec " + receivedHmac + ", gen " + generatedHmac);
                 if (retries < 2) {
                 System.out.println("request resending... nr " + retries+1);
                 send("!retransmit");
                 retries++;
                 }
                 return ("");
                 }*/

            } catch (HMacException ex) {
                System.err.println(ex.getMessage());
            } catch (AESException ex) {
                System.err.println(ex.getMessage());
            }
            //return aesChannel.receive();
        }

        listCommand = false;
        // return message;
        return "Something went wrong, sorry.";


    }

    public void listCommand() {
        if (!hasSessionKey) {
            listCommand = true;
        }

    }

    public void setPrivKey(PrivateKey myPrivKey) {
        this.myPrivKey = myPrivKey;
        if (myPrivKey == null) {
            System.out.println("no priv key found...");
        }
        try {
            cDecrypt.init(Cipher.DECRYPT_MODE, myPrivKey);
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setPubKey(PublicKey otherPubKey) {
        this.otherPubKey = otherPubKey;
        try {
            cEncrypt.init(Cipher.ENCRYPT_MODE, otherPubKey);
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setSessionKey(SecretKey secretKey, byte[] ivParameter, String username) throws AESException {
        this.secretKey = secretKey;
        this.ivParameter = ivParameter;
        hasSessionKey = true;
        aesCrypter = new AES(secretKey, ivParameter);
        try {
            hmacGenerator = new HMac(pathToClientKeyDir, username);
        } catch (HMacException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void setPathToClientKeyDir(String path) {
        this.pathToClientKeyDir = path;
    }

    /**
     * for logout
     */
    public void removeSessionKey() {
        logoutResponse = true;

        secretKey = null;
        ivParameter = null;
        hasSessionKey = false;

        aesCrypter = null;
        hmacGenerator = null;

        otherPubKey = null;
        myPrivKey = null;
    }
}
