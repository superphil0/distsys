package Server.BillingServer;

import java.util.LinkedList;

public class PriceSteps {
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
	
	
}
