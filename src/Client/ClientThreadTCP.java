/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Channel.IChannel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author daniela
 * listening for Server response over TCP
 */
public class ClientThreadTCP extends Thread{
   // private Socket socket = null;
    //private BufferedReader in = null;
    private String fromServer;
        private static IChannel secureChannel;

    
    /*public ClientThreadTCP (BufferedReader in) {
        this.in = in;
    }*/
        
    public ClientThreadTCP (IChannel secureChannel) {
        this.secureChannel = secureChannel;
    }
    
    @Override
    public void run() {
        try  {
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            //While Server is still answering, print message
            
               //(fromServer = in.readLine()) != null) {// && socket.isConnected()){// && !fromServer.isEmpty()) {
            while ((fromServer = secureChannel.receive()) != null) {
                System.out.println(fromServer);
            }
            
        } catch (IOException e) {
            //System.err.println("Couldn't get output from Server.");
        } catch (NullPointerException ex) {
            System.out.println("Lost Connection to Server... ");
        }finally {
            //closes TCP connection when there is no response
            close();
        }
    }
    
    public void close() {
        try {
            //System.out.println("closing connection to Server.");
            //in.close();
            secureChannel.close();
            //socket.close();
        } catch(IOException e) {
            System.err.println("Problem while closing");
            //System.exit(1);
        }
        
    }
    
}
