package Server.BillingServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BillingServer {
	private BillingLogin login;
	private Registry rmiRegistry;
	private Remote remoteAnalyticsServer;
	public static void main(String[] args)
	{
		BillingServer  server = new BillingServer();
		if(args.length!=1) 
		{
			System.out.println("Wrong number of arguments!");
			System.exit(1);			
		}
		server.start(args[0]);
		
	}
	
	public void start(String bindingName)
	{
		login = new BillingLogin(new BillingServerSecure());
		try {
			remoteAnalyticsServer = UnicastRemoteObject.exportObject(login, 0);
		} catch (RemoteException e) {
			Logger.getLogger(BillingServer.class.getName()).log(Level.SEVERE, null, e);
		}
	       try {
	            rmiRegistry.rebind(bindingName, remoteAnalyticsServer);
	        } catch (RemoteException ex) {
	            Logger.getLogger(BillingServer.class.getName()).log(Level.SEVERE, null, ex);
	        }
	}

}
