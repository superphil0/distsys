/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import Exceptions.AESException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author daniela
 */
public class AES {

    private Cipher cEncrypt;
    private Cipher cDecrypt;
    private HMac macGenerator;

    public AES(SecretKey secretKey, byte[] ivParam) throws AESException {
        try {
            cEncrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");
            cDecrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");

            cEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivParam));
            cDecrypt.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivParam));
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
    
    
    public byte[] encryptAES(byte[] message) throws AESException{
        try {
            return cEncrypt.doFinal(message);
        } catch (IllegalBlockSizeException ex) {
            throw new AESException("AES Encrypt: Bad Block Size.");
        } catch (BadPaddingException ex) {
            throw new AESException("AES Encrypt: Bad Padding.");
        }
        
    }
    
    
    public byte[] decryptAES(byte[] message) throws AESException{
        try {
            return cDecrypt.doFinal(message);
        } catch (IllegalBlockSizeException ex) {
            throw new AESException("AES Decrypt: Bad Block Size.");
        } catch (BadPaddingException ex) {
            throw new AESException("AES Decrypt: Bad Padding.");
        }
        
        
    }
    
    
    
}
