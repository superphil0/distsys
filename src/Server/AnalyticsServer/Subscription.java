/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.AnalyticsServer;

import Events.Event;
import Common.IManagementClientCallback;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class Subscription implements Runnable {

    private String id;
    private static long counter = 0;
    private IManagementClientCallback callbackObject;
    private String filter;
    private Event event;
    private AnalyticsServer server;

    public Subscription(String filter, IManagementClientCallback callbackObject, AnalyticsServer server) {
        this.filter = filter;
        this.callbackObject = callbackObject;
        id = String.valueOf(counter++);
    }

    public String getID() {
        return id;
    }

    public synchronized void sendEvent(Event event) {
        this.event = event;
        if (checkEvent() && event != null) {
            server.addTask(this);
        }
    }

    private boolean checkEvent() {
        //TODO check regex
        return true;
    }

    public void run() {
        try {
            callbackObject.receiveEvent(event);
        } catch (RemoteException ex) {
            Logger.getLogger(Subscription.class.getName()).log(Level.SEVERE, null, ex);
        }

        event = null;
    }
}
