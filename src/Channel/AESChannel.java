package Channel;

import Exceptions.AESException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESChannel extends TCPChannel {

    private static int retries = 0;
    private SecretKey secretKey;
    private HMac macGenerator;
    private Cipher encrypt, decrypt;
    private byte[] ivParam;

    public AESChannel(PrintWriter out, BufferedReader in, SecretKey secretKey, byte[] ivParam) throws AESException {
        super(out, in);
        this.secretKey = secretKey;
        macGenerator = new HMac(secretKey);
        this.ivParam = ivParam;
        init();
    }

    public AESChannel(TCPChannel channel, SecretKey secretKey, byte[] ivParam) throws AESException {
        super(channel);
        this.secretKey = secretKey;
        macGenerator = new HMac(secretKey);
        this.ivParam = ivParam;
        init();
    }

    private void init() throws AESException {
        try {
            encrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");
            decrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");

            encrypt.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivParam));
            decrypt.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivParam));
        } catch (InvalidKeyException ex) {
            throw new AESException("AES: Invalid Key.");
        } catch (InvalidAlgorithmParameterException ex) {
            throw new AESException("AES: Invalid Alogirthm Parameter.");
        } catch (NoSuchAlgorithmException ex) {
            throw new AESException("AES: No Such Algorithm.");
        } catch (NoSuchProviderException ex) {
            throw new AESException("AES: No Such Provider.");
        } catch (NoSuchPaddingException ex) {
            throw new AESException("AES: No Such Padding.");
        }

    }

   /* @Override
    public String receive() {
        try {
            //throws AESException{
            String message;
            message = super.receive();

            byte[] bytes = message.getBytes();
            byte[] receivedMac = new byte[32];
            byte[] receivedMsg = new byte[bytes.length - 32];
            System.arraycopy(bytes, bytes.length - 32, receivedMac, 0, 32);
            System.arraycopy(bytes, 0, receivedMsg, 0, bytes.length - 32);
            byte[] generatedMac = macGenerator.getMac(new String(receivedMsg));
            if (MessageDigest.isEqual(receivedMac, generatedMac)) {
                System.out.println(">Received message has wrong MAC request retransmit");
//System.out.println(new String(receivedMsg));
                System.out.println("retrying...");
                if (retries < 2) {
                    send("!retransmit");
                    retries++;
                }
            } else {
                retries = 0;
            }
            decrypt.update(receivedMsg);
            try {
                receivedMsg = decrypt.doFinal();
            } catch (IllegalBlockSizeException ex) {
                System.err.println("AES Decrypt: Bad Block Size.");
            } catch (BadPaddingException ex) {
                System.err.println("AES Decrypt: Bad Padding.");
            }
            return new String(receivedMsg);
        } catch (IOException ex) {
            System.err.println("AES IO Exception while receiving.");
            return null;
        }
    }

    @Override
    public void send(String message) {
        encrypt.update(message.getBytes());
        try {
            message = new String(encrypt.doFinal());
        } catch (IllegalBlockSizeException ex) {
            System.err.println("AES Encrypt: Bad Block Size.");
        } catch (BadPaddingException ex) {
            System.err.println("AES Encrypt: Bad Padding.");
        }

        byte[] bytes = macGenerator.getMac(message);
        super.send(message + new String(bytes));
    }*/
}
