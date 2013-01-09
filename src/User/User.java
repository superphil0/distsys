/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import Server.ServerThread;

/**
 *
 * @author daniela
 */
public class User {
    
    private static final int LOGGED_OUT = 0;
    private static final int LOGGED_IN = 1;
    private int state = LOGGED_OUT;
    private String username;
    //outstanding udp notifications
    //List<String> notifications;
    

    public User (String username) {
        this.username = username;
        //notifications = Collections.synchronizedList(new ArrayList<String>());
    }
    
    /**
     * @param serverThread - current Thread which is communication with the client
     */
    public synchronized void login(ServerThread serverThread) {
            state = LOGGED_IN;
    }
    
    public void logout() {
        state = LOGGED_OUT;
        
        //TODO EVENT!
    }
    
    /*public void receiveNotification(String notification) {
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
    } */
    
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
    
    /*private long getSessionTime() {
        return logoutTimestamp - loginTimestamp;
    }*/
        
}
