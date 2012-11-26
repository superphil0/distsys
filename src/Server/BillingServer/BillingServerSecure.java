package Server.BillingServer;

import java.rmi.Remote;
import java.rmi.RemoteException;


public class BillingServerSecure implements Remote{
	
	private PriceSteps priceSteps;
	public BillingServerSecure() {
		priceSteps = new PriceSteps();
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
			throw new RemoteException("Could not delete PriceStep because invalid: " + startPrice + " endprice " + endPrice);
		}
	}
	void billAuction(String user, long auctionID, double price)
	{
		
	}
	Bill getBill(String user)
	{
		return null;
		
	}

}
