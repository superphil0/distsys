package Server.BillingServer;

public class BillEntry {
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
