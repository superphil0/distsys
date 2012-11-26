/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author daniela
 */
public interface IProcessEvent extends Remote{
    
    public void processEvent() throws RemoteException;
    
}
