package Auction;

import java.util.EventObject;

public class GroupBidFinished extends EventObject {

	/**
	 * 
	 */
	private Bid bid;
	public Bid getBid()
	{
		return bid;
	}
	private static final long serialVersionUID = 1L;

	public GroupBidFinished(Object arg0, Bid bid) {
		super(arg0);
		this.bid = bid;
	}

}
