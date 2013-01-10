package Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.util.encoders.Base64;

import sun.security.rsa.RSASignature;
import sun.security.rsa.RSASignature.SHA512withRSA;
import sun.security.util.ObjectIdentifier;

import Channel.Base64Channel;
import Channel.SecureChannel;
import Channel.TCPChannel;

public class TimeStampResponder extends Thread{
	
	private static final String CHARSET = "UTF-8";
	private Socket socket;
	private PrivateKey key;
	private Base64Channel secureChannel;
	private BufferedReader in;
	private PrintWriter out;
	private String fromClient;
	private String signedHash;
	public TimeStampResponder(Socket accept, PrivateKey key) {
		socket = accept;
		this.key = key;
	}
	
	@Override
	public void run() {
		try {
        	out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET ), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            secureChannel = new Base64Channel(new TCPChannel(out, in));         
        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }
        
           try {
			while ((fromClient = secureChannel.receive()) != null){
				String response ="";
				if(fromClient.startsWith("!getTimestamp"))
				{
					String[] parts  = fromClient.split(" ");
					if(parts.length != 3) response = "2 arguments needed for !getTimestamp command";
					else
					{
						String msg = "!timestamp " +parts[1] + " " + parts[2] + " " + System.currentTimeMillis();
						signedHash = new String(Base64.encode(getHash(msg)));
						response+=msg + " "+ signedHash;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private byte[] getHash(String message)
	{
		Signature sig = null;
		byte[] retstring = null;
		try {
			 sig = Signature.getInstance("SHA512withRSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sig.initSign(key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sig.update(message.getBytes());
			retstring = sig.sign();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return retstring;
	}
	

}
