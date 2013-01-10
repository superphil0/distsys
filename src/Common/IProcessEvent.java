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
public interface IProcessEvent extends Remote{
    
    public void processEvent(Event event) throws RemoteException;
    
}
