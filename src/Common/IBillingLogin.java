package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBillingLogin extends Remote{
	public IBillingSecure login(String username, String password) throws RemoteException;

}
