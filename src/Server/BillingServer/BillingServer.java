package Server.BillingServer;

import PropertyReader.RegistryProperties;
import Server.AnalyticsServer.AnalyticsServer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import Server.RMIRegistry;
import java.rmi.registry.LocateRegistry;

public class BillingServer {

    private BillingLogin login;
    private Registry rmiRegistry;
    private Remote remoteBillingServer;
    private int port = RegistryProperties.getPort();

    public static void main(String[] args) {
        BillingServer server = new BillingServer();
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.exit(1);
        }
        server.start(args[0]);

    }

    public void start(String bindingName) {
        login = new BillingLogin(new BillingServerSecure());
        try {
            rmiRegistry = RMIRegistry.getRmiRegistry();
            remoteBillingServer = UnicastRemoteObject.exportObject(login, 0);
        } catch (RemoteException e) {
            Logger.getLogger(BillingServer.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            rmiRegistry.rebind(bindingName, remoteBillingServer);
            System.out.println("Server bound to rmiRegistry by name: " + bindingName);
        } catch (RemoteException ex) {
            Logger.getLogger(BillingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
