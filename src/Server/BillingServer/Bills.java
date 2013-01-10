package Server.BillingServer;

import java.io.Serializable;
import java.util.HashMap;

public class Bills implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3465364019720640045L;
	private HashMap<String, Bill> userBills;
	private PriceSteps priceSteps;
	public Bills(PriceSteps steps)
	{
		userBills = new HashMap<String, Bill>();
		this.priceSteps = steps;
	}
	
	public synchronized void storeBill(String user, long auctionID, double price)
	{
		if(!userBills.containsKey(user))
		{
			this.userBills.put(user, new Bill(priceSteps));
		}
		userBills.get(user).storeAuction(new BillEntry(auctionID, price));
		
	}
	
	public synchronized Bill getBill(String user)
	{
		if(userBills.containsKey(user))
		{
			return userBills.get(user);
		}
		else
			return null;
	}
	
	

}
