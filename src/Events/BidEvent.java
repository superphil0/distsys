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
public class BidEvent extends Event {

    private String userName;
    private long auctionID;
    private double price;

    public BidEvent(String type, long timestamp, String userName, long auctionID, double price) {
        super(type, timestamp);
        this.userName = userName;
        this.auctionID = auctionID;
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }

    public long getAuctionID() {
        return auctionID;
    }

    public double getPrice() {
        return price;
    }
    
}
