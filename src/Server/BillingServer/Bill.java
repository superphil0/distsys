package Server.BillingServer;

import java.io.Serializable;
import java.util.LinkedList;

public class Bill implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<BillEntry> auctions;
	private PriceSteps steps;
	public Bill(PriceSteps priceSteps)
	{
		auctions = new  LinkedList<BillEntry>();
		steps = priceSteps;
	}
	
	public void storeAuction(BillEntry entry)
	{
		
		synchronized (auctions) {
			auctions.add(entry);
		}
		
	}
	
	@Override
	public String toString() 
	{
		if(auctions == null) return "";
		String ret = "auction_ID\tstrike_price\tfee_fixed\tfee_variable\n";
		synchronized(auctions)
		{
			for(BillEntry b : auctions )
			{
				PriceStep step1 = steps.getPriceStep(b.getPrice());
				ret += b.getAuctionID() +"\t" + b.getPrice() +"\t" + step1.getFixedPrice() + "\t" + step1.getVariableAmount(b.getPrice())+"\n";
			}
		}
		return ret;
	}

}
