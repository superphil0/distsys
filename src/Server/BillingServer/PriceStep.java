package Server.BillingServer;

public class PriceStep implements Comparable<PriceStep>{
	private double startPrice;
	private double endPrice;
	private double fixedPrice;
	private double variablePrice;
	
	
	public PriceStep(double startPrice2, double endPrice2, double fixedPrice,
			double variablePricePercent) {
		startPrice = startPrice2;
		if(endPrice2 == 0.0) endPrice = Double.POSITIVE_INFINITY;
		endPrice = endPrice2;
		
		this.fixedPrice = fixedPrice;
		this.variablePrice = fixedPrice;
	}


	public double getStartPrice() {
		return startPrice;
	}


	public double getEndPrice() {
		return endPrice;
	}


	public double getFixedPrice() {
		return fixedPrice;
	}


	public double getVariablePrice() {
		return variablePrice;
	}
	
	// not equals but overlapping and therefore redundant
	@Override
	public boolean equals(Object obj) {
		PriceStep step = (PriceStep) obj;
		if(startPrice >= step.startPrice && startPrice < step.endPrice)
			return true;
		if(endPrice <= step.endPrice && endPrice > step.startPrice)
			return true;
		return false;
		
	}
	
	boolean checkValid()
	{
		if(startPrice < 0 && endPrice < 0 && fixedPrice < 0 && variablePrice < 0)return false;
		if(endPrice == 0) return true;
		if(endPrice <= startPrice) return false;
		return true;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "StartPrice:\t" + startPrice + " endprice:\t" + endPrice + " fixedPrice:\t" + fixedPrice + " variablePrice:\t" + variablePrice;
	}


	@Override
	public int compareTo(PriceStep o) {
		if(startPrice < o.startPrice)
			return -1;
		if(startPrice > o.startPrice)
			return 1;
		return 0;
	}

	

	
	
}
