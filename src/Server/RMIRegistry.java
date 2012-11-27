/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import PropertyReader.RegistryProperties;
import Server.AnalyticsServer.AnalyticsServer;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class RMIRegistry {
    private static int port = RegistryProperties.getPort();
    private static Registry rmiRegistry;

    public synchronized static Registry getRmiRegistry() {
        
        //get RMI Registry, if it doesn't exist - create it
        try {
            rmiRegistry = LocateRegistry.getRegistry(port);
        } catch (RemoteException ex) {
            try {
                rmiRegistry = LocateRegistry.createRegistry(port);
            } catch (RemoteException ex1) {
                Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }   
        return rmiRegistry;
    }
    
    
}
