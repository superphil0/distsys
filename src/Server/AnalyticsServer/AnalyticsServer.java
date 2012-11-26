/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.AnalyticsServer;

import Events.Event;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import PropertyReader.RegistryProperties;
import Common.IManagementClientCallback;
import Common.IProcessEvent;
import Common.ISubscribe;
import Common.IUnsubscribe;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class AnalyticsServer implements ISubscribe, IProcessEvent, IUnsubscribe {

    private ExecutorService execute;
    private int port = RegistryProperties.getPort();
    private Registry rmiRegistry;
    private Remote remoteAnalyticsServer;
    private String bindingName;
    private HashMap<String, Subscription> subscriptions = new HashMap<String, Subscription>();

    public static void main(String[] args) throws RemoteException {
        AnalyticsServer server = new AnalyticsServer();
        if (args.length == 1) {
            server.start();
            server.setBindingName(args[0]);
        } else {
            System.err.println("Invalid Arguments.");
        }

    }

    public void addTask(Runnable task) {
        execute.execute(task);
    }

    public void start() {
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
            remoteAnalyticsServer = UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            rmiRegistry.rebind(bindingName, remoteAnalyticsServer);
        } catch (RemoteException ex) {
            Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String subscribe(String filter, IManagementClientCallback callbackObject) throws RemoteException {

        Subscription newSubscription = new Subscription(filter, callbackObject, this);
        String id = newSubscription.getID();
        subscriptions.put(id, newSubscription);
        return id;

    }

    public void processEvent(Event event) throws RemoteException {
        //TODO calculate statistics


        for (Subscription sub : subscriptions.values()) {
            sub.sendEvent(event);
        }


    }

    public void unsubscribe(String id) throws RemoteException {
        if (subscriptions.containsKey(id)) {
            subscriptions.remove(id);
        }
    }
}
