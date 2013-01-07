package Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class AESChannel extends TCPChannel implements IChannel {
	
	private static int retries = 0;
	private SecretKey key;
	private HMac macGenerator;
	private Cipher encrypt,decrypt;
	private AlgorithmParameters params;
	
	public AESChannel(PrintWriter out, BufferedReader in, SecretKey key, AlgorithmParameters params) {
		super(out, in);
		this.key = key;
		macGenerator = new HMac(key);
		this.params = params;
		init();
	}
	
	private void init()
	{
		try {
			encrypt = Cipher.getInstance("AES/CTR/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			encrypt.init(Cipher.ENCRYPT_MODE,key, params);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			decrypt = Cipher.getInstance("AES/CTR/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			decrypt.init(Cipher.DECRYPT_MODE, key, params);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public String receive() throws IOException {
		String message = super.receive();
		byte[] bytes = message.getBytes();
		byte[] receivedMac = new byte[32];
		byte[] receivedMsg = new byte[bytes.length-32];
		System.arraycopy(bytes, bytes.length-32, receivedMac, 0, 32);
		System.arraycopy(bytes, 0, receivedMsg, 0, bytes.length-32);
		byte[] generatedMac = macGenerator.getMac(new String(message));
		if(MessageDigest.isEqual(receivedMac, generatedMac))
		{
			System.out.println("Received message has wrong MAC request retransmit");
			//System.out.println(new String(receivedMsg));
			System.out.println("retrying...");
			if(retries <2)
			{
				send("!retransmit");
				retries++;
			}
		}
		else
		{
			retries = 0;
		}
		decrypt.update(receivedMsg);
		try {
			receivedMsg = decrypt.doFinal();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(receivedMsg);
	}
	@Override
	public void send(String message) {
		encrypt.update(message.getBytes());
		try {
			message = new String(encrypt.doFinal());
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] bytes = macGenerator.getMac(message);
		super.send(message+new String(bytes));
	}
	

}
