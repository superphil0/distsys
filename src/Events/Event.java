/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Events;

import java.util.UUID;

/**
 *
 * @author daniela
 */
public abstract class Event {
    private String ID;
    private String type;
    private long timestamp;
    
    public Event(String type, long timestamp){
        ID = UUID.randomUUID().toString();
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    
    
}
