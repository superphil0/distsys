package loadTesting;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class TestClient implements Runnable{
	//private BufferedReader in = null;
	private boolean running = true;
	private Socket socket;
	private int id;
	private TestArgs args;
	private PrintWriter out;
	private long time;
	private ExecutorService executer;
	private Thread t;
	private LoadTest test;
	public TestClient(LoadTest test, Socket socket, long timeStarted, int id, TestArgs args, ExecutorService executer)
	{
		this.test = test;
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
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		
			//t = new OutputThread(
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = null;
//		t = new ClientThreadTCP(socket);
//		t.start();
		runCommand("!login Thread"+id);
		t = new AuctionMaker(id, args.getAuctionsPerMin(), this, args.getAuctionDuration());
		t.start();
		t = new LoopedExecuter(id, args.getBidsPerMinute(), this) {
			
			@Override
			protected String getCommand(long timeSinceCreation) {
				int timeInSeconds= (int ) (timeSinceCreation / 1000);
				double auctionDistance =  60/args.getAuctionsPerMin(); 
				int endedAuctions = (int)((timeInSeconds - args.getAuctionDuration()) / auctionDistance +1) * args.getClients();
				int activeAuctions=0;
				int globalId = callback.getSyncThread().getAuctionCounter();
				if(endedAuctions < 0 ) 
				{
					endedAuctions = 0;
				}

				activeAuctions = globalId - endedAuctions;
		
				//System.out.println(activeAuctions + " active auctions now glbId = "+globalId+ " ended: " +endedAuctions);
				int rand = random.nextInt(activeAuctions+1) ;

				// alle gestarteten - allen beendeten auktionen  = alle aktiven
				String command = "!bid " + (endedAuctions +rand) + " " + timeSinceCreation;
				return command;
			}
		};
		t.start();
		
		t = new LoopedExecuter(id, (int) 60/args.getUpdateIntervalSec(), this) {
			
			@Override
			protected String getCommand(long timeSinceCreation) {
				
				return "!list";
			}
		};
		t.start();
		
		
		
		
	}
	
	public void runCommand(String command)
	{
		//System.out.println(command);
		synchronized (executer) {
			t = new OutputThread(command, out);
			executer.execute(t);
		}
		
	}
	public LoadTest getSyncThread()
	{
		return test;
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
