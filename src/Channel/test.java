package Channel;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeyGenerator generator = null;
		try {
			generator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// KEYSIZE is in bits 
		generator.init(256); 
		SecretKey key = generator.generateKey(); 
		HMac mac = new HMac(key);
		byte[] bytes = mac.getMac("HHHHHHHHHHHHHAAAAAAAAAAAALLooo");
		String message = new String(bytes);
		System.out.println(message + "\n " + bytes.length );

	}

}
