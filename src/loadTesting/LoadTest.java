package loadTesting;

import Common.IAnalytics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import PropertyReader.LoadProperties;
import PropertyReader.RegistryProperties;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadTest {
    //ThreadPool

    private ExecutorService executer;
    private int clients;
    private int auctionsPerMin;
    private int auctionDuration;
    private int updateIntervalSec;
    private int bidsPerMin;
    private AtomicInteger counter;
    private String host = "localhost";
    private int port = 13480;
    private String analyticsBindingName;
    private Registry rmiRegistry;
    private IAnalytics analyticsService;

    public LoadTest(String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
        }
        try {
            host = args[0];//Number of concurrent bidding clients
            port = Integer.parseInt(args[1]);//Number of started auctions per client per minute
            analyticsBindingName = args[2]; //Duration of the auctions in seconds

        } catch (NumberFormatException ex) {
            System.out.println("One of the arguments was not an int");
            System.exit(1);
        }

        LoadProperties props = new LoadProperties();
        auctionDuration = props.getAuctionDuration();
        auctionsPerMin = props.getAuctionsPerMin();
        updateIntervalSec = props.getUpdateIntervalSec();
        bidsPerMin = props.getBidsPerMin();
        clients = props.getClients();
    }

    public static void main(String[] args) {
        LoadTest test = new LoadTest(args);
        test.run();
    }

    private void run() {
        
        TestManagementClient tmc = new TestManagementClient();
        new RegistryProperties();

        int rport = RegistryProperties.getPort();
        String rhost = RegistryProperties.getHost();

        try {
            rmiRegistry = LocateRegistry.getRegistry(rhost, rport);
            analyticsService = (IAnalytics) rmiRegistry.lookup(analyticsBindingName);
            analyticsService.subscribe(".*", tmc);

        } catch (NotBoundException ex) {
            Logger.getLogger(LoadTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(LoadTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(LoadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        executer = Executors.newCachedThreadPool();
        counter = new AtomicInteger();
        Socket socket = null;
        TestClient t = null;
        LinkedList<TestClient> clientList = new LinkedList<TestClient>();
        //ManagementClient manage = new ManagementClient();
        //manage.setAnalyticsBindingName(analyticsBindingName);
        long timeServerStart = System.currentTimeMillis();
        for (int i = 0;
                i < clients;
                i++) {
            try {
                socket = new Socket(host, port);
            } catch (UnknownHostException e) {
                System.out.println("could not connect to server with thread " + i);
                System.out.println("Exiting.. Please hit enter");
                break;
            } catch (IOException e) {
            	System.out.println("could not connect to server with thread " + i);
                System.out.println("Exiting.. Please hit enter");
                break;
            }
            t = new TestClient(this, socket, timeServerStart, i, new TestArgs(bidsPerMin, auctionsPerMin, auctionDuration, updateIntervalSec, clients), executer);
            clientList.add(t);
            //System.out.println("starting thread " + i);
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
        } finally {
            for (TestClient tt : clientList) {
                tt.stop();
            }
        }
    }

    public int incrementAuctionCounter() {
        return counter.incrementAndGet();
    }

    public int getAuctionCounter() {
        return counter.get();
    }
}
