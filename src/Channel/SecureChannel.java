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
        //System.out.println("step 1 sec");
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

        } else {
            try {
                //AES encryption
                String encryptedMessage = new String(aesCrypter.encryptAES(message.getBytes()));
                String hmac = generateMac(message);
                String msg64 = Base64.encode(encryptedMessage.getBytes());
                channel.send(msg64 + " " + hmac);
                return;
            } catch (HMacException ex) {
                System.err.println(ex.getMessage());
            } catch (AESException ex) {
                System.err.println(ex.getMessage());
                return;
            }
        }
        //channel.send(message);

    }

    public String receive() throws IOException {
        //System.out.println("step 3.1 receive decrypt");

        String message = channel.receive(); //incoming message, already base 64 decoded
        //System.out.println("step 3.2 receive decrypt");

        //TODO Decrypt message here
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
                    String decrypted = new String(cDecrypt.doFinal(message.getBytes()));
                    return decrypted;
                } catch (Exception ex) {
                    System.out.println("Error while trying to decrypt Message...");
                    //Logger.getLogger(SecureChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

/*
 String encryptedMessage = new String(aesCrypter.encryptAES(message.getBytes()));
                String hmac = generateMac(message);
                String msg64 = Base64.encode(encryptedMessage.getBytes());
                channel.send(msg64 + " " + hmac);*/
        } else {
            try {
                //AES decryption
                String msg64 = message.split(" ")[0];
                String rhmac = message.split(" ")[1]; 
                String plainText = new String(aesCrypter.decryptAES(Base64.decode(msg64)));
                String ghmac = generateMac(plainText);
                System.out.println("rhmac: " + rhmac);
                System.out.println("ghmac: " + ghmac);
                
                return plainText;
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
        System.out.println(">SecureChannel: private key set!");
        if (myPrivKey == null) {
            System.out.println("no priv key found <.<");
        }
        try {
            cDecrypt.init(Cipher.DECRYPT_MODE, myPrivKey);
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
            //Logger.getLogger(SecureChannel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPubKey(PublicKey otherPubKey) {
        this.otherPubKey = otherPubKey;
        System.out.println(">SecureChannel: public key set!");
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
        System.out.println(">SecureChannel: SessionKey set!");
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
