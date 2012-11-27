/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagementClient;

import Common.IManagementClientCallback;
import Events.Event;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 *
 * @author daniela
 */
public class ManagementClient implements IManagementClientCallback {
    private String id = UUID.randomUUID().toString();

    public void receiveEvent(Event event) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getID() {
        return id;
    }
    
}
