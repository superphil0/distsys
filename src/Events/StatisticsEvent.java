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
public class StatisticsEvent extends Event{
    
    private double value;
    
    public StatisticsEvent(String type, long timestamp, double value) {
        super(type, timestamp);
        this.value = value;
    }

    public double getValue() {
        return value;
    }
    
    
}
