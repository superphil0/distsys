package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Server.BillingServer.BillingSecure;
import Server.BillingServer.BillingServerSecure;

public interface IBillingLogin extends Remote{
	public IBillingSecure login(String username, String password) throws RemoteException;

}
