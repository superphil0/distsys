package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Server.BillingServer.Bill;
import Server.BillingServer.PriceStep;
import Server.BillingServer.PriceSteps;

public interface IBillingSecure extends Remote{
	PriceSteps getPriceSteps();

	 
	void createPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent) throws RemoteException;

	
	void deletePriceStep(double startPrice, double endPrice) throws RemoteException;

	
	void billAuction(String user, long auctionID, double price);

	Bill getBill(String user) throws RemoteException;

}
