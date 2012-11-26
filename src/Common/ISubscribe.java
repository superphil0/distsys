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
public interface ISubscribe extends Remote{
    
    public String subscribe(String filter, IManagementClientCallback callbackObject) throws RemoteException;
    
}
