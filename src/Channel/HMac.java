package Channel;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
public class HMac{
	private SecretKey key;
	public HMac(SecretKey key)
	{
		this.key = key;
	}
	
	public byte[] getMac(String message) {
		Mac mac = null;
		try {
			mac = Mac.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mac.init(key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mac.update(message.getBytes());
		return mac.doFinal();
	}
	

}
