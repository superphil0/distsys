package loadTesting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

import Client.ClientThreadTCP;

public class TestClient implements Runnable{
	private BufferedReader in = null;
	private boolean running = true;
	private Socket socket;
	private int id;
	private TestArgs args;
	private PrintWriter out;
	private long time;
	private ExecutorService executer;
	private Thread t;
	public TestClient(Socket socket, long timeStarted, int id, TestArgs args, ExecutorService executer)
	{
		this.id = id;
		this.time = timeStarted;
		this.socket = socket;
		this.args = args;
		this.executer = executer;

	}
	@Override
	public void run() {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		
			//t = new OutputThread(
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = null;
		t = new ClientThreadTCP(socket);
		t.start();
		runCommand("!login Thread"+id);
		t = new AuctionMaker(id, args.getAuctionsPerMin(), this, args.getAuctionDuration());
		t.start();
		t = new LoopedExecuter(id, args.getBidsPerMinute(), this) {
			
			@Override
			protected String getCommand(long timeSinceCreation) {
				int timeInSeconds= (int ) (timeSinceCreation / 1000);
				double auctionDistance =  60/args.getAuctionsPerMin(); 
				int activeAuctions = (int) Math.ceil(timeInSeconds / auctionDistance)
				- (int ) Math.ceil((timeInSeconds - args.getAuctionDuration())/auctionDistance);
				System.out.println(activeAuctions + " active auctions now");
				// alle gestarteten - allen beendeten auktionen  = alle aktiven
				//String command = "!bid " + aucId + " " + timeSinceCreation;
				return "!list";
			}
		};
		t.start();
		
		
		
		
	}
	
	public void runCommand(String command)
	{
		System.out.println(command);
		synchronized (executer) {
			t = new OutputThread(command, out);
			t.start();
		}
		
	}
	
	public void stop()
	{
		this.running = false;
	}
	public boolean isRunning()
	{
		return running;
	}
	public long getCreationTime()
	{
		return time;
	}
	public TestArgs getTestArgs() {
		
		return args;
	}
	

}
