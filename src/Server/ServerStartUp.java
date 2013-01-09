/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Exceptions.KeyNotFoundException;
import Exceptions.WrongPasswordException;
import PropertyReader.RegistryProperties;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Starts the AuctionServer and Shuts it down, if user hits enter
 *
 * @author daniela
 */
public class ServerStartUp {

    private static AuctionServer auctionServer;
    private static String analyticsBindingName, billingBindingName;
    private static String pathToPrivKey, pathToClientKeyDir;
    private static PrivateKey myPrivKey;

    public static void main(String[] args) throws IOException {
        new RegistryProperties();
        RegistryProperties.getPort();
        RegistryProperties.getHost();

        int port = 0;
        if (args.length != 5) {
            System.out.println("Please enter the port, analyticsBindingName and"
                    + "billingBindingName  as argument on which you want the server to run");
            System.out.println(args.length);
            System.exit(0);
        }
        if (args != null && !args[0].isEmpty()) {
            try {
                port = Integer.parseInt(args[0]);
                analyticsBindingName = args[1];
                billingBindingName = args[2];
                pathToPrivKey = args[3];
                pathToClientKeyDir = args[4];
            } catch (NumberFormatException nfe) {
                System.err.println("PortNumber has to be a number! E.g. Server <Port>");
            }
        } else {
            System.err.println("Please enter a Port Number!");
        }

        for (int i = 0; i < 3; i++) {
            try {
                myPrivKey = getPrivateKey(pathToPrivKey);
                if (myPrivKey != null) {
                    System.out.println("Success!");
                    break;
                }
            } catch (KeyNotFoundException ex) {
                System.out.println(ex.getMessage());
                //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrongPasswordException ex) {
                System.out.println(ex.getMessage());

                //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (i == 2) {
                System.out.println("Wrong Key, Access denied.");
                return;
            }
        }

        if (port > 0) {
            auctionServer = new AuctionServer(port, analyticsBindingName, billingBindingName, myPrivKey, pathToClientKeyDir);
            auctionServer.start();
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("AuctionServer started, hit enter to shut down.");


        if (scanner.hasNextLine()) {
            auctionServer.close();
        }
        System.out.println("Bye.");

    }

    private static PrivateKey getPrivateKey(String pathToPrivKey) throws KeyNotFoundException, WrongPasswordException {
        PrivateKey privateKey = null;
        PEMReader in;
        try {
            in = new PEMReader(new FileReader(pathToPrivKey), new PasswordFinder() {
                public char[] getPassword() {
                    // reads the password from standard input for decrypting the private key
                    System.out.println("Enter pass phrase:");
                    try {
                    	char[] pw = new BufferedReader(new InputStreamReader(System.in)).readLine().toCharArray();
                    	//System.out.println(">input pw: " + pw.toString());
                        return pw;
                    } catch (IOException ex) {
                        Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                }
            });
            if (in == null) {
                throw new KeyNotFoundException("Couldn't read Key");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);

            throw new KeyNotFoundException("Wrong path to Private Server Key");
        }
        KeyPair keyPair;
        try {
            keyPair = (KeyPair) in.readObject();
        } catch (IOException ex) {
            ///Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);

            throw new WrongPasswordException("Wrong Password, please try again!");
        } finally {
        	
        }
        privateKey = keyPair.getPrivate();
        return privateKey;

    }
}
