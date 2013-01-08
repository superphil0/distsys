package Channel;

import Exceptions.HMacException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;

public class HMac {

    private Key key;
    private Mac hMac;

    public HMac(String pathToKeys, String username) throws HMacException {
        try {
            String path = pathToKeys + "/" + username + ".key";
            key = readKey(path);
            hMac = Mac.getInstance("HmacSHA256");
            hMac.init(key);
        } catch (NoSuchAlgorithmException ex) {
            throw new HMacException("HMac: No Such Algorithm");
        } catch (InvalidKeyException ex) {
            throw new HMacException("HMac: Invalid Key");
        }

    }

    //for Testing purposes
    public HMac(Key key) {
        this.key = key;
    }

    public byte[] getMac(String message) throws HMacException {

        hMac.update(message.getBytes());
        return hMac.doFinal();
    }

    //path - username +ending included
    private Key readKey(String path) {
        try {
            byte[] keyBytes = new byte[1024];
            FileInputStream fis = new FileInputStream(path);
            fis.read(keyBytes);
            fis.close();
            byte[] input = Hex.decode(keyBytes);

            Key key = new SecretKeySpec(input, "HmacSHA256");
            return key;

        } catch (IOException ex) {
            return null;
        }

    }
}
