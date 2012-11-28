package Server.BillingServer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import Common.IBillingSecure;


public class BillingServerSecure implements IBillingSecure {
	/**
	 * 
	 */
	private PriceSteps priceSteps;
	private Bills bills;
	
	public BillingServerSecure()
	{
		priceSteps = new PriceSteps();
		bills = new Bills(priceSteps);
	}

	public PriceSteps getPriceSteps()
	{
		return priceSteps;
		
	}
	 
	public void createPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent) throws RemoteException
	{
		PriceStep step = new PriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);
		if(!priceSteps.addPriceStep(step))
		{
			throw new RemoteException("Could not add PriceStep because it already exists or it is invalid: " + step);
		}
	}
	
	public void deletePriceStep(double startPrice, double endPrice) throws RemoteException
{
	if(!priceSteps.deletePriceStep(new PriceStep(startPrice, endPrice, 1, 1)))
	{
		throw new RemoteException("Could not delete PriceStep because it doesnt exist: " + startPrice + " endprice " + endPrice);
	}
}


public void billAuction(String user, long auctionID, double price)
{
	bills.storeBill(user, auctionID, price);
}
public Bill getBill(String user) throws RemoteException
{
	Bill b = bills.getBill(user);
	if(b == null)
	{
		throw new RemoteException("No Entries for this User");
	}
	return b;
	
}

}
