/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import Events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author daniela
 */
public interface IManagementClientCallback extends Remote{
    
    public void receiveEvent(Event event) throws RemoteException;
    
    public String getID();
    
}
