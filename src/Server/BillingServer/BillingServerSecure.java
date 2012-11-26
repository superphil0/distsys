package Server.BillingServer;

import java.rmi.Remote;
import java.rmi.RemoteException;


public class BillingServerSecure implements Remote{
	
	private PriceSteps priceSteps;
	private Bills bills;
	public BillingServerSecure() {
		priceSteps = new PriceSteps();
		bills = new Bills(priceSteps);
	}
	PriceSteps getPriceSteps()
	{
		return priceSteps;
		
	}
	 
	void createPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent) throws RemoteException
	{
		PriceStep step = new PriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);
		if(!priceSteps.addPriceStep(step))
		{
			throw new RemoteException("Could not add PriceStep because it already exists or it is invalid: " + step);
		}
	}
	
	void deletePriceStep(double startPrice, double endPrice) throws RemoteException
	{
		if(!priceSteps.deletePriceStep(new PriceStep(startPrice, endPrice, 1, 1)))
		{
			throw new RemoteException("Could not delete PriceStep because it doesnt exist: " + startPrice + " endprice " + endPrice);
		}
	}
	
	
	void billAuction(String user, long auctionID, double price)
	{
		bills.storeBill(user, auctionID, price);
	}
	Bill getBill(String user) throws RemoteException
	{
		Bill b = bills.getBill(user);
		if(b == null)
		{
			throw new RemoteException("No Entries for this User");
		}
		return b;
		
	}

}
