package Server.BillingServer;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Common.IBillingSecure;

public class BillingSecure extends UnicastRemoteObject implements IBillingSecure{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private IBillingSecure billingServerSecure;
	public BillingSecure(IBillingSecure billing) throws RemoteException
	{
		super();
		this.billingServerSecure = billing;
	}
	@Override
	public PriceSteps getPriceSteps() throws RemoteException{
		return billingServerSecure.getPriceSteps();
	}
	@Override
	public void createPriceStep(double startPrice, double endPrice,
			double fixedPrice, double variablePricePercent)
			throws RemoteException {
		billingServerSecure.createPriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);
		
	}
	@Override
	public void deletePriceStep(double startPrice, double endPrice)
			throws RemoteException {
		billingServerSecure.deletePriceStep(startPrice, endPrice);
		
	}
	@Override
	public void billAuction(String user, long auctionID, double price) throws RemoteException{
		billingServerSecure.billAuction(user, auctionID, price);
		
	}
	@Override
	public Bill getBill(String user) throws RemoteException {
		return billingServerSecure.getBill(user);
	}

}
