package Server.BillingServer;

import java.util.HashMap;

public class Bills {
	private HashMap<String, Bill> userBills;
	private PriceSteps priceSteps;
	public Bills(PriceSteps steps)
	{
		userBills = new HashMap<String, Bill>();
		this.priceSteps = steps;
	}
	
	public void storeBill(String user, long auctionID, double price)
	{
		if(!userBills.containsKey(user))
		{
			this.userBills.put(user, new Bill(priceSteps));
		}
		userBills.get(user).storeAuction(new BillEntry(auctionID, price));
		
	}
	
	public Bill getBill(String user)
	{
		if(userBills.containsKey(user))
		{
			return userBills.get(user);
		}
		else
			return null;
	}
	
	

}
