/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.RemoteException;

/**
 *
 * @author daniela
 */
public class AnalyticsServer implements ISubscribe, IProcessEvent, IUnsubscribe {
    
    

    public String subscribe(String filter) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void processEvent() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unsubscribe(String sID) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
