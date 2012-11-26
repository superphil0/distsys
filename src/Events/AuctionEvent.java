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
public class AuctionEvent extends Event{
    
    private long auctionID;
    
    public AuctionEvent(String type, long timestamp, long auctionID) {
        super(type, timestamp);
        this.auctionID = auctionID;
    }

    public long getAuctionID() {
        return auctionID;
    }
}
