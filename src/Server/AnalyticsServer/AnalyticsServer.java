/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.AnalyticsServer;

import Common.IAnalytics;
import Events.Event;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import PropertyReader.RegistryProperties;
import Common.IManagementClientCallback;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class AnalyticsServer implements IAnalytics {

    //ThreadPool
    private ExecutorService executer;
    //Port from the RMI-Registry
    private int port = RegistryProperties.getPort();
    private String host = RegistryProperties.getHost();
    private Registry rmiRegistry;
    //Remote Object of this Server to export
    private Remote remoteAnalyticsServer;
    //Name of this Server in the rmiRegistry - from the properties file
    private String bindingName;
    //List with all subscriptions containing String ID + SubscriptionObject
    private HashMap<String, Subscription> subscriptions = new HashMap<String, Subscription>();
    private static long serverStarttime = new Date().getTime();
    private CalculateStatistics calculator;

    /**
     * starts a AnalyticsServer as Thread
     *
     * @param args should contain bindingName
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        new RegistryProperties();
        AnalyticsServer server = new AnalyticsServer();
        if (args.length == 1) {
            server.setBindingName(args[0]);
            server.start();
        } else {
            System.err.println("Invalid Arguments.");
        }
    }

    /**
     * adds a Task to the ThreadPool
     *
     * @param task
     */
    public void addTask(Runnable task) {
        System.out.println("task vorhanden " + task != null);
        executer.execute(task);
    }

    public void start() {
        calculator = new CalculateStatistics(this);
        try {
            remoteAnalyticsServer = UnicastRemoteObject.exportObject(this, 0);
            rmiRegistry = LocateRegistry.getRegistry(host, port);
            rmiRegistry.rebind(bindingName, remoteAnalyticsServer);
            System.out.println("get registry: host " + host + " port " + port);

        } catch (RemoteException ex) {
            try {
                rmiRegistry = LocateRegistry.createRegistry(port);
                rmiRegistry.rebind(bindingName, remoteAnalyticsServer);
                System.out.println("Registry created on port " + port);

            } catch (RemoteException ex1) {
                Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex1);
            }
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
        //if event !instanceof StatisticsEvent

        System.out.println("event " + event.getType());
        calculator.calculate(event);
        //send Event to all subscribers, they decide whether they need it or not
        for (Subscription subscription : subscriptions.values()) {
            subscription.sendEvent(event);
        }

    }

    public void unsubscribe(String id) throws RemoteException {
        if (subscriptions.containsKey(id)) {
            subscriptions.remove(id);
        }
        /*for (Subscription subscription : subscriptions.values()) {
         System.out.println("active subs " + subscription.getID());
         }*/
    }

    public long getStarttime() {
        return serverStarttime;
    }
}
