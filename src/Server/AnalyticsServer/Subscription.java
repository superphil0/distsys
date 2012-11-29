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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author daniela
 */
public class Subscription {//implements Runnable {

    private String id;
    private static long counter = 1;
    private IManagementClientCallback callbackObject;
    private Event event;
    private AnalyticsServer server;
    private String filter;

    public Subscription(String filter, IManagementClientCallback callbackObject, AnalyticsServer server) {
        this.callbackObject = callbackObject;
        id = String.valueOf(counter++);
        this.filter = filter;
    }

    public String getID() {
        return id;
    }

    public synchronized void sendEvent(Event event) {
        this.event = event;
        /*if (checkEvent() && event != null) {
         server.addTask(this);
         }*/
        if (checkFilter()){//checkEvent(event)) {
            send();
        }
    }

    private boolean checkFilter() {

        //check regex
        
        //* for subscribe all
        if(filter.equals("*")) {
            return true;
        }

        String type = event.getType();

        Matcher matcher = null;
        Pattern pattern = null;

        try {
            pattern = Pattern.compile(filter);
            matcher = pattern.matcher(type);
        } catch (PatternSyntaxException ex) {
            return false;
        }

        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public void send() {
        try {
            callbackObject.receiveEvent(event);
        } catch (RemoteException ex) {
            Logger.getLogger(Subscription.class.getName()).log(Level.SEVERE, null, ex);
        }

        event = null;
    }
}
