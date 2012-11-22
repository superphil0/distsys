/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import Server.ServerThread;
import java.util.HashMap;

/**
 * Singleton - contains a list of all users
 *
 * @author daniela
 */
public class UserHandler {

    private static UserHandler instance = new UserHandler();
    private static HashMap<String, User> allUsers = new HashMap<String, User>();

    private UserHandler() {
        //allUsers = new HashMap<String, User>();
        //initializeList();
    }

    public static UserHandler getInstance() {
        /*if (instance == null) {
            instance = new UserHandler();
        }*/
        return instance;
    }

    private void initializeList() {
        //users
        allUsers.put("alice", new User("alice"));
        allUsers.put("bob", new User("bob"));
        allUsers.put("carl", new User("carl"));
        allUsers.put("dave", new User("dave"));

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
        }
    }

    //TODO geht DS!!!
    /**
     * @param username to log in
     * @return true for successful login false for already logged in
     */
    public Boolean login(String username, ServerThread serverThread) {
        //checks if the User already exists
        if(serverThread == null) {
            return false;
        }
        if (allUsers.containsKey(username)) {
            //check if already logged in
            if (getUser(username).isLoggedIn()) {
                return false;
            } else {
                getUser(username).login(serverThread);
                return true;
            }
            //new User
        } else {
            allUsers.put(username, new User(username));
            getUser(username).login(serverThread);
            return true;
        }
    }
}
