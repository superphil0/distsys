package loadTesting;

public class TestArgs {
	private int bidsPerMinute,  auctionsPerMin,  auctionDuration,  updateIntervalSec;
	private int clients;
	int getClients() {
		return clients;
	}
	public TestArgs(int bidsPerMinute, int auctionsPerMin, int auctionDuration, int updateIntervalSec, Integer clients)
	{
		this.auctionDuration = auctionDuration;
		this.auctionsPerMin = auctionsPerMin;
		this.bidsPerMinute = bidsPerMinute;
		this.updateIntervalSec = updateIntervalSec;
		this.clients = clients;
	}
	int getBidsPerMinute() {
		return bidsPerMinute;
	}

	int getAuctionsPerMin() {
		return auctionsPerMin;
	}

	int getAuctionDuration() {
		return auctionDuration;
	}

	int getUpdateIntervalSec() {
		return updateIntervalSec;
	}
	
}
