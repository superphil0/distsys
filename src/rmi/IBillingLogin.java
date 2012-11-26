package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Server.BillingServerSecure;

public interface IBillingLogin extends Remote{
	public BillingServerSecure login(String username, String password) throws RemoteException;

}