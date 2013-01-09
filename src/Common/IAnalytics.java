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
public interface IAnalytics extends Remote {

    public void processEvent(Event event) throws RemoteException;

    public String subscribe(String filter, IManagementClientCallback callbackObject) throws RemoteException;

    public void unsubscribe(String id) throws RemoteException;
}
