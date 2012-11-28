/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import User.UserHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This server is listening for clients
 * and starts a thread for each connected client
 * 
 * @author daniela
 */
public class AuctionServer extends Thread {

    private static int port;
    private static ServerSocket listeningSocket = null;
    private static ArrayList<ServerThread> serverList;
    private String analyticsBindingName, billingBindingName;

    public AuctionServer(int port, String analyticsBindingName, String billingBindingName) {
        this.port = port;
        serverList = new ArrayList<ServerThread>();
        this.analyticsBindingName = analyticsBindingName;
        this.billingBindingName = billingBindingName;
    }

    @Override
    public void run() {

        boolean listening = true;

        try {
            listeningSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + port + ".");
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
        }

        while (listening) {
            try {
                ServerThread clientCon = new ServerThread(listeningSocket.accept(), analyticsBindingName, billingBindingName);
                clientCon.start();
                serverList.add(clientCon);
            } catch (IOException ex) {
                close();
                break;
            }

        }

    }

    public void close() {
        //logout all users before shut-down
        UserHandler.getInstance().logoutAll();
        for(ServerThread s: serverList) {
            s.close();
        }
        try {
            listeningSocket.close();
        } catch (IOException ex) {
            System.err.println("Couldn't close Server-Socket.");
        }
    }
}
