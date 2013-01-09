/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Auction;

import Common.IAnalytics;
import Common.IBillingSecure;
import Events.AuctionEvent;
import Events.BidEvent;
import User.User;
import User.UserHandler;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class AuctionHandler {

    private static AuctionHandler instance = new AuctionHandler();
    private static HashMap<Integer, Auction> auctionList = new HashMap<Integer, Auction>();
    private LinkedList<User> biddingPriority;
    private Bid reservedSpotUser = null;
    //endedAuctions with Notifications outstanding

    private static IAnalytics analyticsService;
	private static IBillingSecure billingSecure;
    private LinkedList<Bid> groupBids = new LinkedList<Bid>();

    private AuctionHandler() {
        //auctionList = new HashMap<Integer, Auction>();
        //endedAuctions = new HashMap<Integer, Auction>();
    	biddingPriority = new LinkedList<User>();
    	
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
    public boolean confirm(double amount, int id, String username,User user)
    {
    	for(Bid b : groupBids)
    	{
    		if(b.equals(id, username, amount))
    		{
    			return b.confirm(user, amount, id);
    		}
    	}
    	return false;
    }
    public synchronized boolean addGroupBid(Bid bid)
    {
    	// this is also the entry point to guarentee fairness
    	// Preventing deadlocks, because always at least one user is free
    	// if we only allow users -1 bids at the same time
    	
    	// in order to prevent starvation we have a queue that keeps track of the 
    	// users who have failed to create a group bid lately
    	// there is one spot out of the number of users reserved for this queue
    	
    	if(groupBids.size() -2 < UserHandler.getInstance().getNumberofUsers())
    	{
    		this.groupBids.add(bid);
    		return true;
    	}
    	else if(biddingPriority.isEmpty() && reservedSpotUser == null)
    	{
    		this.groupBids.add(bid);
    		reservedSpotUser = bid;
    	}
    	else if (biddingPriority.peekFirst() == bid.getUser())
    	{
    		if(reservedSpotUser == null) 
    		{
    			reservedSpotUser = bid;
    			groupBids.add(bid);
    			biddingPriority.removeFirst();
    		}
    	}
    	else
    	{
    		if(!biddingPriority.contains(bid.getUser()))
    			biddingPriority.add(bid.getUser());
    	}
    	return false;
    }
    
    public synchronized void removeGroupBid(Bid bid)
    {
    	this.groupBids.remove(bid);
    	if(bid == reservedSpotUser) reservedSpotUser = null;
    }
    public synchronized void endAuction(Auction a) {
        if (a.getHighestBidder() != null) {

            String bidder = a.getHighestBidder().getUsername();
            try {
            	billingSecure.billAuction(bidder, a.getId(), a.getHighestBid());
                analyticsService.processEvent(new BidEvent("BID_WON", new Date().getTime(), bidder, a.getId(), a.getHighestBid()));
            } catch (RemoteException ex) {
                Logger.getLogger(AuctionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
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
        }catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
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
                }catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
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
        }catch (NullPointerException npe) {
                //Logger.getLogger("No Connection to Analytics Server").log(Level.SEVERE, null, npe);
            }

        return auction;
    }

    public HashMap<Integer, Auction> getAllAuctions() {
        return auctionList;
    }

    public Auction getAuction(int id) {
        return auctionList.get(id);
    }

	public static void setBilling(IBillingSecure billingSecuree) {
		billingSecure = billingSecuree;
		
	}
}
