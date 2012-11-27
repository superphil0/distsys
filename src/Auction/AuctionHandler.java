/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Auction;

import User.User;
import java.util.HashMap;

/**
 *
 * @author daniela
 */
public class AuctionHandler {

    private static AuctionHandler instance = new AuctionHandler();
    private static HashMap<Integer, Auction> auctionList = new HashMap<Integer, Auction>();
    //endedAuctions with Notifications outstanding
    private HashMap<Integer, Auction> endedAuctions = new HashMap<Integer, Auction>();

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

    public synchronized void endAuction(Auction a) {
        String bidder = "Nobody";
        String end = "The auction " + a.getId() + " '" + a.getDescription() + "' has ended. ";

        if (a.getHighestBidder() != null) {
            bidder = a.getHighestBidder().getUsername();
            //a.getHighestBidder().receiveNotification(end + " You won with " + a.getHighestBid());
        }
        //notify owner
        //a.getOwner().receiveNotification(end + bidder + " has won with " + a.getHighestBid());

        auctionList.remove(a.getId());
    }

    public synchronized boolean bid(User bidder, int id, double amount) {
        if (auctionList.containsKey(id)) {
            return getAuction(id).bid(bidder, amount);
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
        return auction;
    }

    public HashMap<Integer, Auction> getAllAuctions() {
        return auctionList;
    }

    public Auction getAuction(int id) {
        return auctionList.get(id);
    }
}
