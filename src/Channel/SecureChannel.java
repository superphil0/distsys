/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import Exceptions.AESException;
import Exceptions.HMacException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.util.encoders.Hex;

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
    private String username, path;
    private HMac hmac;
    private int resendCounter = 0;

    public SecureChannel(PrintWriter out, BufferedReader in) {
        super(out, in);
        initCipher();

    }

    public SecureChannel(TCPChannel channel) {
        super(channel);
        this.channel = channel;
        initCipher();

    }

    private void initCipher() {
        try {
            cEncrypt = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            cDecrypt = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("RSA: No Such Algorithm.");
        } catch (NoSuchProviderException ex) {
            System.err.println("RSA: No Such Provider.");
        } catch (NoSuchPaddingException ex) {
            System.err.println("RSA: No Such Padding.");
        }
    }

    public void send(String message) {
    	//channel.send(message);
    	
    	//System.out.println(">sending: " + message);
        //System.out.println("step 1 encrypt");

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
                    String encrypted = Base64.encode(cEncrypt.doFinal(message.getBytes()));
                    //System.out.println(">encrypted: " + encrypted);
                    channel.send(encrypted);
                    return;
                } catch (Exception ex) {
                    System.out.println("Error while trying to encrypt Message... please try again!");
                }

            } else { //nothing
                System.out.println("Please login properly, otherwise only !list is a possilbe command.");
                return;
            }

        } else {
            try {
                //AES encryption
                byte[] encryptedMessage = aesCrypter.encryptAES(message.getBytes());
                String hmac = generateMac(message);
                String msg64 = Base64.encode(encryptedMessage);
                channel.send(msg64 + " " + hmac);
                
             	//System.out.println(">AESsending: " + msg64 + " " + hmac);
                
                return;
            } catch (HMacException ex) {
                System.err.println("HMacEx: " + ex.getMessage());
            } catch (AESException ex) {
                System.err.println("AES :" + ex.getMessage());
                return;
            }
        }

    }

    public String receive() throws IOException {
    	//return channel.receive();

        String message = channel.receive(); //incoming message, already base 64 decoded
    	//System.out.println(">receiving: "+ message );
        //System.out.println("step 3 decrypting");


        if (!hasSessionKey) { //RSA priv decryption
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
                	byte [] dec = cDecrypt.doFinal(Base64.decode(message));
                    String decrypted = new String(dec);
                    
                    return decrypted;
                } catch (Exception ex) {
                    Logger.getLogger(SecureChannel.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error while trying to decrypt Message...");
                }
            }

        } else {
            try {
                //AES decryption
                String msg64 = message.split(" ")[0];
                String rhmac = message.split(" ")[1];
                String plainText = new String(aesCrypter.decryptAES(Base64.decode(msg64)));
                String ghmac = generateMac(plainText);
                //System.out.println("rhmac: " + rhmac);
                //System.out.println("ghmac: " + ghmac);
            	//System.out.println(">AESreceiving: "+ plainText );

                if (rhmac.equals(ghmac)) {
                    resendCounter = 0;
                    return plainText;
                } else if(resendCounter <1) {
                    //send("!resend");
                    send(plainText);
                    resendCounter++;
                    return "!resending";
                } else {
                    return "Receiving Message failed. Macs differ.";
                }
                //return new String(aesCrypter.decryptAES(message.getBytes()));
            } catch (HMacException ex) {
                System.err.println(ex.getMessage());
            } catch (Base64DecodingException ex) {
                System.err.println(ex.getMessage());
            } catch (AESException ex) {
                System.err.println(ex.getMessage());
            }
        }

        listCommand = false;
        // return message;
        return "Something went wrong, sorry.";

 
    }

    /*
     * generates a String version of a Hex encoded HMac
     */
    private String generateMac(String message) throws HMacException {
        byte[] mac = hmac.getMac(message);
        return new String(Hex.encode(mac));
    }

    public void listCommand() {
        if (!hasSessionKey) {
            listCommand = true;
        }

    }

    public boolean hasSessionKey() {
        return hasSessionKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPrivKey(PrivateKey myPrivKey) {
        this.myPrivKey = myPrivKey;
        //System.out.println(">SecureChannel: private key set!");
        if (myPrivKey == null) {
            System.out.println("no priv key found <.<");
        }
        try {
            cDecrypt.init(Cipher.DECRYPT_MODE, myPrivKey);
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setPubKey(PublicKey otherPubKey) {
        this.otherPubKey = otherPubKey;
       //System.out.println(">SecureChannel: public key set!");
        try {
            cEncrypt.init(Cipher.ENCRYPT_MODE, otherPubKey);
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setSessionKey(SecretKey secretKey, byte[] ivParameter) throws AESException, HMacException {
        this.secretKey = secretKey;
        this.ivParameter = ivParameter;
        hasSessionKey = true;
        aesCrypter = new AES(secretKey, ivParameter);
        //System.out.println(">SecureChannel: SessionKey set!");
        hmac = new HMac(path, username);
    }

    /**
     * logout
     */
    public void removeSessionKey() {
        logoutResponse = true;

        secretKey = null;
        ivParameter = null;
        hasSessionKey = false;
        aesCrypter = null;

        otherPubKey = null;
        myPrivKey = null;

        hmac = null;
        username = null;
        path = null;
    }
}
