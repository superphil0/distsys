/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author daniela
 */
public interface IUnsubscribe extends Remote{
    
    public void unsubscribe(String id) throws RemoteException;
    
}
