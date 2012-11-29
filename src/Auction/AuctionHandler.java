/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Auction;

import Common.IAnalytics;
import Events.AuctionEvent;
import Events.BidEvent;
import User.User;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class AuctionHandler {

    private static AuctionHandler instance = new AuctionHandler();
    private static HashMap<Integer, Auction> auctionList = new HashMap<Integer, Auction>();
    //endedAuctions with Notifications outstanding
    private HashMap<Integer, Auction> endedAuctions = new HashMap<Integer, Auction>();
    private static IAnalytics analyticsService;

    private AuctionHandler() {
        //auctionList = new HashMap<Integer, Auction>();
        //endedAuctions = new HashMap<Integer, Auction>();
    }

    public static AuctionHandler getInstance() {
        /*if (instance == null) {
         instance = new AuctionHandler();
         }*/
        return instance;
    }

    public static synchronized void setAS(IAnalytics as) {
        if (analyticsService == null) {
            analyticsService = as;
        }
    }

    public synchronized void endAuction(Auction a) {
        if (a.getHighestBidder() != null) {

            String bidder = a.getHighestBidder().getUsername();
            try {

                analyticsService.processEvent(new BidEvent("BID_WON", new Date().getTime(), bidder, a.getId(), a.getHighestBid()));
            } catch (RemoteException ex) {
                Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            //a.getHighestBidder().receiveNotification(end + " You won with " + a.getHighestBid());

        }
        //notify owner
        //a.getOwner().receiveNotification(end + bidder + " has won with " + a.getHighestBid());
        //    public AuctionEvent(String type, long timestamp, long auctionID) {
        try {

            analyticsService.processEvent(new AuctionEvent("AUCTION_ENDED", new Date().getTime(), a.getId()));
        } catch (RemoteException ex) {
            Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        auctionList.remove(a.getId());
    }

    public synchronized boolean bid(User bidder, int id, double amount) {
        if (auctionList.containsKey(id)) {
            boolean bidResult = getAuction(id).bid(bidder, amount);
            if (bidResult) {
                try {
                    //    public BidEvent(String type, long timestamp, String userName, long auctionID, double price) {
                    String type;
                    if (getAuction(id).getHighestBid() == 0) {
                        type = "BID_PLACED";
                    } else {
                        type = "BID_OVERBID";
                    }

                    analyticsService.processEvent(new BidEvent(type, new Date().getTime(), bidder.getUsername(), id, amount));
                } catch (RemoteException ex) {
                    Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return bidResult;
        } else {
            return false;
        }
    }

    public boolean hasAuctions() {
        return !auctionList.isEmpty();
    }

    public synchronized Auction addAuction(User owner, int duration, String description) {
        Auction auction = new Auction(owner, duration, description);
        auctionList.put(auction.getId(), auction);

        try {
            analyticsService.processEvent(new AuctionEvent("AUCTION_STARTED", new Date().getTime(), auction.getId()));
        } catch (RemoteException ex) {
            Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return auction;
    }

    public HashMap<Integer, Auction> getAllAuctions() {
        return auctionList;
    }

    public Auction getAuction(int id) {
        return auctionList.get(id);
    }
}
