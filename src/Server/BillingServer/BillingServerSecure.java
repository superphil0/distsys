package Server.BillingServer;

import java.rmi.Remote;


public class BillingServerSecure implements Remote{
	PriceSteps getPriceSteps()
	{
		return null;
		
	}
	void createPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent)
	{
		
	}
	void deletePriceStep(double startPrice, double endPrice)
	{
		
	}
	void billAuction(String user, long auctionID, double price)
	{
		
	}
	Bill getBill(String user)
	{
		return null;
		
	}

}
