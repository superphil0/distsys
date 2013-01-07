/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Channel.Base64Channel;
import Channel.IChannel;
import Channel.SecureChannel;
import Channel.TCPChannel;
import Exceptions.KeyNotFoundException;
import Exceptions.WrongPasswordException;
import Protocol.CommandProtocol;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

/**
 *
 * @author daniela
 */
//Thread der die Kommunikation mit dem Client handhabt.
public class ServerThread extends Thread {

    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    private CommandProtocol cp;
    private IChannel secureChannel;
    private String pathToClientKeyDir;
    private PrivateKey myPrivKey;
    private PublicKey clientPubKey;

    public ServerThread(Socket socket, PrivateKey privKey, String pathToClientKeyDir) { //, String analyticsBindingName, String billingBindingName) {
        super("ServerThread");
        this.socket = socket;

        this.myPrivKey = privKey;
        this.pathToClientKeyDir = pathToClientKeyDir;

        //this.analyticsBindingName = analyticsBindingName;
        //this.billingBindingName = billingBindingName;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));

        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }

    }

    @Override
    public void run() {

        String inputLine, outputLine;


        try {

            cp = new CommandProtocol(this); //, analyticsBindingName, billingBindingName);

            //proccessing client input
            boolean processMsg = true;
            while ((inputLine = secureChannel.receive()) != null) {    //in.readLine()) != null) {
                //System.out.println(">received from User: "+inputLine);
                processMsg = true;
                outputLine = "couldn't process input";
                
                if(inputLine.startsWith("!login")) {
                    String username = inputLine.split(" ")[1];
                    try {
                        //TODO set to secureChannel
                        clientPubKey = readClientsPubKey(pathToClientKeyDir, username);
                        
                        
                    } catch (KeyNotFoundException ex) {
                        System.out.println(ex.getMessage());
                        outputLine = ex.getMessage();
                        processMsg = false;
                    }
                }
                
                if(processMsg) {
                    outputLine = cp.processInput(inputLine);
                }
                //System.out.println(outputLine);
                //out.println(outputLine);
                secureChannel.send(outputLine);

            }

        } catch (IOException e) {
            //System.out.println(e);
            System.err.println("Problem with connection.");
        } catch (NullPointerException e) {
            //Client closed Connection
        } finally {
            close();
        }

    }

    private PublicKey readClientsPubKey(String pathToClientKeyDir, String userName) throws KeyNotFoundException {
        String pathToPublicKey = pathToClientKeyDir + "/" + userName + ".pub.pem";
        PublicKey publicKey = null;
        PEMReader in;
        try {
            in = new PEMReader(new FileReader(pathToPublicKey));
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            throw new KeyNotFoundException("Wrong path to Private Server Key");
        }
        
        try {
            publicKey = (PublicKey) in.readObject();
        } catch (IOException ex) {
            throw new KeyNotFoundException("Couldn't read Key");
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return publicKey;
    }

    protected void close() {
        try {
            cp.processInput("!logout");
            out.close();
            in.close();
            socket.close();
            secureChannel.close();
            //System.out.println("Connection closed!");
        } catch (IOException e) {
            System.err.println("Problem with disconnection from " + socket.getInetAddress().toString());
        }
    }
}
