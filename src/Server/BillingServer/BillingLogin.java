package Server.BillingServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import rmi.IBillingLogin;

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
	private BillingServerSecure billingServer;
	private MessageDigest md5;
	public BillingLogin(BillingServerSecure server)
	{
		this.billingServer = server;
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
			  String name = s.split("=")[0];
			  String password = s.split("=")[1];
			  
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
	public BillingServerSecure login(String username, String password) throws RemoteException {
		ManageUser user = new ManageUser(username.trim(), md5.digest(password.trim().getBytes()).toString()); 
		if(!validUsers.contains(user))
		{
			return null;
		}
		return billingServer;
		

	}

}
