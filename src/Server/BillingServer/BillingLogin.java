package Server.BillingServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import Common.IBillingLogin;
import Common.IBillingSecure;


class ManageUser
{
	String name;
	String password;
	public ManageUser(String user, String password)
	{
		this.name = user;
		this.password = password;
	}
	@Override
	public boolean equals(Object obj) {
		ManageUser user = (ManageUser) obj;
		if(user.name.equals(this.name) && user.password.equals(this.password))
			return true;
		return false;
	}
}

public class BillingLogin implements IBillingLogin {
	private LinkedList<ManageUser> validUsers = new LinkedList<ManageUser>();
	private IBillingSecure billingServer;
	private MessageDigest md5;
	public BillingLogin(BillingServerSecure billingServerSecure)
	{
		this.billingServer = (IBillingSecure) billingServerSecure;
		try {
			md5 = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			System.out.println("MD5 is not recognized as algorithm");
		}
		BufferedReader in = null;
		try {
			System.out.println(System.getProperty("user.dir"));
			in = new BufferedReader(new FileReader("user.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			while (in.ready()) {
			  String s = in.readLine();
			  if(s.startsWith("#")) continue;
			  String name = s.split("=")[0].trim();
			  String password = s.split("=")[1].trim();
			  
			  validUsers.add(new ManageUser(name, password));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IBillingSecure login(String username, String password) throws RemoteException {
		String hashedPassword =null;
		try {
			byte[] digest = md5.digest(password.trim().getBytes("UTF-8"));
			BigInteger bigInt = new BigInteger(1,digest);
			hashedPassword = bigInt.toString(16);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		ManageUser user = new ManageUser(username.trim(), hashedPassword);
		if(!validUsers.contains(user))
		{
			return null;
		}
		//IBillingSecure billing = (IBillingSecure) UnicastRemoteObject.exportObject(billingServer,0);
		return billingServer;
		

	}

}
