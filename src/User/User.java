/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import Server.ServerThread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author daniela
 */
public class User {
    
    private static final int LOGGED_OUT = 0;
    private static final int LOGGED_IN = 1;
    private int state = LOGGED_OUT;
    private ServerThread serverThread;
    
    String username;
    //outstanding notifications
    List<String> notifications;
    //TODO User adress
    

    public User (String username) {
        this.username = username;
        notifications = Collections.synchronizedList(new ArrayList<String>());
    }
    
    /**
     * @param dsocket - current User-Address
     * @return true: logged in successfully.
     *         false: user is already logged in.
     */
    public synchronized void login(ServerThread serverThread) {
       // if(state == LOGGED_OUT) {
            state = LOGGED_IN;
            this.serverThread = serverThread;
            if(!notifications.isEmpty()) {
                for(int i = 0; i < notifications.size(); i++) {
                    sendToUser(notifications.get(i));
                    notifications.remove(i);
                }
                /*
                for(String notification: notifications.size()) {
                    sendToUser(notification);
                    notifications.remove(notification);
                }*/
            }
        //} else {
            //already logged in
        //}
    }
    
    public void logout() {
        state = LOGGED_OUT;
        serverThread = null;
    }
    
    public void receiveNotification(String notification) {
        if(state == LOGGED_IN && serverThread != null) {
            sendToUser(notification);
        }
        else {
            addNotification(notification);
        }
    }
    
    private void addNotification (String text) {
        notifications.add(text);
    }
    
    private void sendToUser(String notification) {
        //TODO send messages via ServerThread
        serverThread.sendNotification(notification);
    }
    
    public Boolean isLoggedIn(){
        if(state == LOGGED_IN) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getUsername() {
        return username;
    }
        
}
