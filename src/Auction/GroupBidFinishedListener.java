package Auction;

import java.util.EventObject;

public interface GroupBidFinishedListener {
	
        public void handleGroupBidFinished(EventObject e, boolean result);
}


