/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Events;

import Events.Event;

/**
 *
 * @author daniela
 */
public class UserEvent extends Event{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1237193793187141421L;
	private String userName;
    
    public UserEvent(String type, long timestamp, String userName) {
        super(type, timestamp);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    
    
    
}
