package loadTesting;

public class AuctionMaker extends LoopedExecuter {

	private int auctionDuration;
	
	public AuctionMaker(int id, int timesPerMin, TestClient callback, int auctionDuration) {
		super(id, timesPerMin, callback);
		this.auctionDuration = auctionDuration;
	}

	@Override
	protected String getCommand(long timeSinceCreation) {
		String command = "!create " + auctionDuration + " Thread" + id +"" + 
		counter;
		callback.getSyncThread().incrementAuctionCounter();
		counter++;
		return command;
	}

}
