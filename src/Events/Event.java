/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Events;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author daniela
 */
public abstract class Event  implements Serializable{
    private String id;
    private String type;
    private long timestamp;
    
    public Event(String type, long timestamp){
        id = UUID.randomUUID().toString();
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    
    
}
