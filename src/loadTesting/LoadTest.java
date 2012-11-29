package loadTesting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTest {
	 //ThreadPool
    private ExecutorService executer;
	private int clients;
	private int auctionsPerMin;
	private int auctionDuration;
	private int updateIntervalSec;
	private int bidsPerMin;
    private AtomicInteger counter;
	public LoadTest(String[] args) {
		if(args.length != 5 ) 
		{
			System.out.println("Wrong number of arguments");
		}
		try
		{
			clients = Integer.parseInt(args[0]);//Number of concurrent bidding clients
			 auctionsPerMin = Integer.parseInt(args[1]);//Number of started auctions per client per minute
			 auctionDuration = Integer.parseInt(args[2]); //Duration of the auctions in seconds
			 updateIntervalSec = Integer.parseInt(args[3]); //Number of seconds that have to 
									//pass before the clients repeatedly update the current list of active auctions
			 bidsPerMin = Integer.parseInt(args[4]); // Number of bids placed on (random) auctions per client per minute
		}
		catch (NumberFormatException ex)
		{
			System.out.println("One of the arguments was not an int");
			System.exit(1);
		}
	}

	public static  void main(String[] args)
	{		
		LoadTest test = new LoadTest(args);
		test.run();
	}
	
	private void run()
	{
		executer = Executors.newCachedThreadPool();
		counter = new AtomicInteger();
		Socket socket = null;
		String host = "localhost";
		int port = 13480;
		TestClient t = null;
		LinkedList<TestClient> clientList = new LinkedList<TestClient>();
		long timeServerStart = System.currentTimeMillis();
		for(int i = 0; i < clients; i++)
		{
			try {
				socket  = new Socket(host, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t = new TestClient(this, socket, timeServerStart, i, new TestArgs(bidsPerMin, auctionsPerMin, auctionDuration, updateIntervalSec,clients),executer);
			clientList.add(t);
			synchronized (executer) {
				executer.execute(t);
			}
			
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			for(TestClient tt : clientList)
			{
				tt.stop();
			}
		}
		
	}
	public int getAuctionCounter()
	{
		return counter.incrementAndGet();
	}

}
