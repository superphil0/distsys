/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import User.UserHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author daniela
 */
public class Server extends Thread {

    private static BufferedReader stdIn;
    private static UserHandler userHandler;
    private static int port;
    private static ServerSocket listeningSocket = null;
    private static ArrayList<ServerThread> serverList;

    public Server(int port) {
        this.port = port;
        serverList = new ArrayList<ServerThread>();
    }

    /*   public static void main(String[] args) throws IOException {

     //TODO starts with port only?
     int port = 0;
     if (args != null && !args[0].isEmpty()) {
     try {
     port = Integer.parseInt(args[0]);
     } catch (NumberFormatException nfe) {
     System.err.println("PortNumber has to be a number! E.g. Server <Port>");
     }
     } else {
     System.err.println("Please enter a Port Number!");
     }*/
    public void run() {

        boolean listening = true;

        try {
            listeningSocket = new ServerSocket(port);
            System.out.println("Listening to port: " + port + ".");
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
        }

        userHandler = UserHandler.getInstance();
        //stdIn = new BufferedReader(new InputStreamReader(System.in));
        //String input;

        //TODO close if enter is hit
        while (listening) {
            try {
                // && (input = stdIn.readLine()) != null && !input.isEmpty()) {
                ServerThread clientCon = new ServerThread(listeningSocket.accept());
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
        for(ServerThread s: serverList) {
            s.close();
        }
        userHandler.logoutAll();
        try {
            listeningSocket.close();
        } catch (IOException ex) {
            System.err.println("Couldn't close Server-Socket.");
        }
    }
}
