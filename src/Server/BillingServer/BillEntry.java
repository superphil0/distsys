package Server.BillingServer;

import java.io.Serializable;

public class BillEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2193852192125265558L;
	private long auctionID;
	private double price;
	public BillEntry(long auctionID2, double price)
	{
		this.auctionID = auctionID2;
		this.price = price;
	}
	long getAuctionID() {
		return auctionID;
	}
	double getPrice() {
		return price;
	}
	
	

}
