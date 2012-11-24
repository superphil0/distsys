/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author daniela
 */
public class ServerStartUp {

    private static Server server;

    public static void main(String[] args) throws IOException {

        //TODO starts with port only?
        int port = 0;
        if(args.length != 1)
        {
        	System.out.println("Please enter the port as argument on which you want the server to run");
        	System.exit(0);
        }
        if (args != null && !args[0].isEmpty()) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println("PortNumber has to be a number! E.g. Server <Port>");
            }
        } else {
            System.err.println("Please enter a Port Number!");
        }
        System.out.println("Server started, hit enter to shut down.");

        if (port > 0) {
            server = new Server(port);
            server.start();
        }

        Scanner scanner = new Scanner(System.in);

        if (scanner.hasNextLine()) {
            server.close();
        }
        System.out.println("Bye.");

    }
}
