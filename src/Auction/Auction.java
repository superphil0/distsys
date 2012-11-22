/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Auction;

import User.User;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author daniela
 */
public class Auction {

    private static int counter = 0;
    private int id;
    private User owner, highestBidder;
    private String description;
    //private Date endDate;
    private Date endDate;
    private double highestBid = 0;
    private TimerTask endAuction;
    private Timer timer;
    private boolean hasEnded = false;
    private AuctionHandler auctionHandler = AuctionHandler.getInstance();

    public Auction(User owner, int duration, String description) {
        setId();
        this.owner = owner;
        this.description = description;
        //this.endDate.setTime(new Date().getTime() + duration*1000);
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(GregorianCalendar.SECOND, duration);
        endDate = cal.getTime();
        
        timer = new Timer();
        endAuction = new TimerTask() {
            public void run() {
                endAuction();
            }
        };
        timer.schedule(endAuction, endDate);
    }


    private synchronized void setId() {
        counter++;
        id = counter;
    }
    //TODO notify users + remove from list
    //TODO sagt dem Sever schick ne notification raus + löscht sich selbt vom auctionHanlder

    private void endAuction() {
        hasEnded = true;
        auctionHandler.endAuction(this);
    }

    public boolean hasEnded() {
        return hasEnded;
        //send notification
    }

    public synchronized boolean bid(User bidder, double bid) {
        if (bid > highestBid) {
            highestBid = bid;
            highestBidder = bidder;
            return true;
        } else {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public String getDescription() {
        return description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double  getHighestBid() {
        return highestBid;
    }
}
