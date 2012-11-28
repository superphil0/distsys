package Server.BillingServer;

import PropertyReader.RegistryProperties;
import Server.AnalyticsServer.AnalyticsServer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.rmi.registry.LocateRegistry;

public class BillingServer {

    private BillingLogin login;
    private Registry rmiRegistry;
    private Remote remoteBillingServer;
    private int port = RegistryProperties.getPort();
    private String host = RegistryProperties.getHost();

    public static void main(String[] args) {

        RegistryProperties r = new RegistryProperties();


        BillingServer server = new BillingServer();
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.exit(1);
        }
        server.start(args[0]);

    }

    public void start(String bindingName) {

        try {
        	
            login = new BillingLogin(new BillingServerSecure());
            remoteBillingServer = UnicastRemoteObject.exportObject(login, 0);
            rmiRegistry = LocateRegistry.getRegistry(host, port);
            rmiRegistry.rebind(bindingName, remoteBillingServer);
            System.out.println("get registry: host " + host + " port " + port);


        } catch (RemoteException ex) {
            try {
                Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
                rmiRegistry = LocateRegistry.createRegistry(port);
                rmiRegistry.rebind(bindingName, remoteBillingServer);
                System.out.println("Registry created on port " + port);

            } catch (RemoteException ex1) {
                Logger.getLogger(BillingServer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }



    }
}
