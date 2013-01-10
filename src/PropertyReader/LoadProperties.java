package PropertyReader;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daniela
 */
public class LoadProperties extends PropertyReader{
    
	private int clients;
	private int auctionsPerMin;
	private int auctionDuration;
	private int updateIntervalSec;
	private int bidsPerMin;
    private static String path = "loadtest.properties";

    //private RegistryProperties registryProperties = new RegistryProperties(); 
    
    public LoadProperties() {

        super(path);
        clients = Integer.parseInt(props.getProperty("clients"));
        auctionsPerMin = Integer.parseInt(props.getProperty("auctionsPerMin"));
        auctionDuration = Integer.parseInt(props.getProperty("auctionDuration"));
        updateIntervalSec = Integer.parseInt(props.getProperty("updateIntervalSec"));
        bidsPerMin = Integer.parseInt(props.getProperty("updateIntervalSec"));
    }

	public int getClients() {
		return clients;
	}

	public int getAuctionsPerMin() {
		return auctionsPerMin;
	}

	public int getAuctionDuration() {
		return auctionDuration;
	}

	public int getUpdateIntervalSec() {
		return updateIntervalSec;
	}

	public int getBidsPerMin() {
		return bidsPerMin;
	}

	static String getPath() {
		return path;
	}


    
}
