/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Auction.AuctionHandler;
import Common.IAnalytics;
import PropertyReader.RegistryProperties;
import User.UserHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This server is listening for clients and starts a thread for each connected
 * client
 *
 * @author daniela
 */
public class AuctionServer extends Thread {

    private static BufferedReader stdIn;
    private static int port;
    private static ServerSocket listeningSocket = null;
    private static ArrayList<ServerThread> serverList;
    private String analyticsBindingName, billingBindingName;
    private static Registry rmiRegistry;
    private IAnalytics analyticsService;

    public AuctionServer(int port, String analyticsBindingName, String billingBindingName) {

        AuctionServer.port = port;
        serverList = new ArrayList<ServerThread>();
        this.analyticsBindingName = analyticsBindingName;
        this.billingBindingName = billingBindingName;
        RegistryProperties r = new RegistryProperties();

        try {
            rmiRegistry = LocateRegistry.getRegistry(RegistryProperties.getHost(), RegistryProperties.getPort());
            analyticsService = (IAnalytics) rmiRegistry.lookup(analyticsBindingName);
            AuctionHandler.getInstance();
			AuctionHandler.setAS(analyticsService);
            UserHandler.getInstance();
			UserHandler.setAS(analyticsService);

        } catch (NotBoundException ex) {
            System.out.println("Couldn't connect to analytics server.");
            //Logger.getLogger(AuctionServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            //Logger.getLogger(AuctionServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            //Logger.getLogger(AuctionServer.class.getName()).log(Level.SEVERE, null, ex);
        }


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
                ServerThread clientCon = new ServerThread(listeningSocket.accept());//, analyticsBindingName, billingBindingName);
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
        for (ServerThread s : serverList) {
            s.close();
        }
        try {
            listeningSocket.close();
        } catch (IOException ex) {
            System.err.println("Couldn't close Server-Socket.");
        }
    }
}
