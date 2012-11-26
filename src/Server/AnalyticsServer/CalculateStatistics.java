/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.AnalyticsServer;

import Events.AuctionEvent;
import Events.BidEvent;
import Events.Event;
import Events.StatisticsEvent;
import Events.UserEvent;

/**
 *
 * @author daniela
 */
public class CalculateStatistics implements Runnable {
    
    private AnalyticsServer server;
    private Event event;
    
    public CalculateStatistics(AnalyticsServer server) {
        this.server = server;
    }
    
    public void calculate(Event event) {
        this.event = event;
        server.addTask(this);
        
    }

    public void run() {
       
        if(event != null) {
            if(event instanceof AuctionEvent){
                
            } else if(event instanceof BidEvent) {
                
            } else if(event instanceof UserEvent) {
                server.processEvent(new StatisticsEvent("BID_PRICE_MAX", new Date().getTime(), 4000));
            } else {
                //e.g. StatisticsEvent
            }
        
        
    }
    
    
    
}
