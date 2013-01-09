package Auction;

import java.util.LinkedList;

import User.User;

public class Bid {
	User getUser() {
		return user;
	}

	Auction getAuction() {
		return auction;
	}

	double getAmount() {
		return amount;
	}

	private User user;
	private Auction auction;
	private double amount;
	private int confirms;
	private GroupBidSource source;
	private LinkedList<User> confirmants;
	
	public Bid(User user, Auction auction, double amount2) {
		this.user = user;
		this.auction = auction;
		this.amount = amount2;
		source = new GroupBidSource();
		confirmants = new LinkedList<User>();
	}
	public boolean confirm(User user, double amount, int auctionID)
	{
		if(confirmants.contains(user)) return false;
		
		confirms++;
		source.addEventListener(user);
		if(isConfirmed())
		{	
			source.fireEvent(auction.bid(user, amount), this);
		}
		return true;
	}
	public boolean isConfirmed()
	{
		return confirms >=2;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Bid)
		{
			Bid bi = (Bid) obj;
			if(bi.auction.getId() == this.auction.getId() 
					&& this.user.getUsername() == bi.user.getUsername())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(int id, String username, double amount)
	{
		if(id == this.auction.getId() 
				&& this.user.getUsername().equals(username)
				&& this.amount == amount)
		{
			return true;
		}
		return false;
	}
	
	
}
