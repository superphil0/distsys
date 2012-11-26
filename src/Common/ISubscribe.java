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
public interface ISubscribe extends Remote{
    
    public String subscribe(String filter) throws RemoteException;
    
}
