package Auction;

import User.User;

public class Bid {
	User getUser() {
		return user;
	}

	Auction getAuction() {
		return auction;
	}

	int getAmount() {
		return amount;
	}

	private User user;
	private Auction auction;
	private int amount;
	private int confirms;
	private GroupBidSource source;
	
	public Bid(User user, Auction auction, int amount) {
		this.user = user;
		this.auction = auction;
		this.amount = amount;
		source = new GroupBidSource();
	}
	public void confirm(User user)
	{
		confirms++;
		source.addEventListener(user);
		if(isConfirmed())
		{
			source.fireEvent();
		}
	}
	public boolean isConfirmed()
	{
		return confirms >=2;
	}
	
	
}
