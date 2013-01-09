/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import Auction.AuctionHandler;
import Common.IAnalytics;
import Events.UserEvent;
import Server.ServerThread;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton - contains a list of all users
 *
 * @author daniela
 */
public class UserHandler {

    private static UserHandler instance = new UserHandler();
    private static HashMap<String, User> allUsers = new HashMap<String, User>();
    private static IAnalytics analyticsService;

    private UserHandler() {
    }

    public static UserHandler getInstance() {
        return instance;
    }
    public synchronized int getNumberofUsers()
    {
    	return allUsers.size();
    }
    public User addUser(String name) {
        allUsers.put(name, new User(name));
        return getUser(name);
    }

    public User getUser(String name) {
        return allUsers.get(name);
    }

    public void logoutAll() {
        for (User u : allUsers.values()) {
            u.logout();

            try {
                analyticsService.processEvent(new UserEvent("USER_DISCONNECTED", new Date().getTime(), u.getUsername()));
            } catch (RemoteException ex) {
                Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
            }
        }
    }

    public void logout(User user) {
        user.logout();
        try {
            analyticsService.processEvent(new UserEvent("USER_LOGOUT", new Date().getTime(), user.getUsername()));
        } catch (RemoteException ex) {
            Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
            //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
        }
        allUsers.remove(user);
    }
    public String getUserList()
    {
    	String ret = "";
    	for(User u : allUsers.values())
    	{
    		ret+= u+"\n";
    	}
    	return ret;
    }
    public static synchronized void setAS(IAnalytics as) {
        if (analyticsService == null) {
            analyticsService = as;
        }
    }

    //TODO geht DS!!!
    /**
     * @param username to log in
     * @return true for successful login, false for already logged in
     */
    /*
     try {
     analyticsService.processEvent(new UserEvent("USER_LOGOUT", new Date().getTime(), currentUser.getUsername()));
     } catch (RemoteException ex) {
     Logger.getLogger(CommandProtocol.class.getName()).log(Level.SEVERE, null, ex);
     }
     */
    public Boolean login(String username, ServerThread serverThread) {
        //checks if the User already exists
        if (serverThread == null) {
            return false;
        }
        if (allUsers.containsKey(username)) {
            //check if already logged in
            if (getUser(username).isLoggedIn()) {
                return false;
            } else {
                getUser(username).login(serverThread);

                try {
                    analyticsService.processEvent(new UserEvent("USER_LOGIN", new Date().getTime(), username));
                } catch (RemoteException ex) {
                    Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException npe) {
                    //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
                }


                return true;
            }
            //new User
        } else {
            allUsers.put(username, new User(username));
            getUser(username).login(serverThread);

            try {
                analyticsService.processEvent(new UserEvent("USER_LOGIN", new Date().getTime(), username));
            } catch (RemoteException ex) {
                Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
            }
            return true;
        }
    }
}
