package Server.BillingServer;

import java.io.Serializable;
import java.util.LinkedList;

public class PriceSteps implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<PriceStep> priceSteps;
	
	public PriceSteps()
	{
		this.priceSteps = new LinkedList<PriceStep>();
	}
	public LinkedList<PriceStep> getPriceSteps()
	{
		return priceSteps;
	}
	
	boolean addPriceStep(PriceStep step)
	{
		if(priceSteps.contains(step) || !step.checkValid()) return false;
		priceSteps.add(step);
		return true;
	}
	
	boolean deletePriceStep(PriceStep step)
	{
		if(!priceSteps.contains(step))
		{
			return false;
		}
		
		PriceStep step2 = priceSteps.get(priceSteps.indexOf(step));
		if(step.getEndPrice() == step2.getEndPrice())
		{
			priceSteps.remove(step2);
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		java.util.Collections.sort(priceSteps);
		String ret = "";
		for(PriceStep step : priceSteps)
		{
			ret += step.toString() + "\n";
		}
		return ret;		
	}
	
	public PriceStep getPriceStep(double price)
	{
		for(PriceStep p : priceSteps)
		{
			if(price >= p.getStartPrice() && price < p.getEndPrice())
			{
				return p;			
			}
		}
		return new PriceStep(0, 0, 0, 0);
	}
	
	
}
