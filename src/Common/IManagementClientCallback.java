/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author daniela
 */
public interface IReceiveEvent extends Remote{
    
    public void receiveEvent(Event event) throws RemoteException;
    
}
