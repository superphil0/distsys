package Server.BillingServer;

import PropertyReader.RegistryProperties;
import Server.AnalyticsServer.AnalyticsServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.rmi.registry.LocateRegistry;

import sun.util.BuddhistCalendar;

public class BillingServer {

    private BillingLogin login;
    private Registry rmiRegistry;
    private Remote remoteBillingServer;
    private int port = RegistryProperties.getPort();
    private String host = RegistryProperties.getHost();

    public static void main(String[] args) {
                new RegistryProperties();

        BillingServer server = new BillingServer();
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.exit(1);
        }
        server.start(args[0]);

    }

    public void start(String bindingName) {
    	BillingServerSecure billingSecure = null;	
        try {
        	billingSecure = new BillingServerSecure();
            login = new BillingLogin(billingSecure);
            remoteBillingServer = UnicastRemoteObject.exportObject(login, 0);
            System.out.println("get registry: host " + host + " port " + port);

            rmiRegistry = LocateRegistry.getRegistry(host, port);
            rmiRegistry.rebind(bindingName, remoteBillingServer);
              
        } catch (RemoteException ex) {
                Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
			while(!in.readLine().startsWith("!exit"));
			System.out.println("Server exit");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			rmiRegistry.unbind(bindingName);
			UnicastRemoteObject.unexportObject(login, false);
			UnicastRemoteObject.unexportObject(billingSecure, false);
			
			
		}
		catch (RemoteException ex)
		{
			
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	



    }
}
