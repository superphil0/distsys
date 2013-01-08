package Auction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupBidSource {

		  private List<GroupBidFinishedListener> listeners = new ArrayList<GroupBidFinishedListener>();
		  public synchronized void addEventListener(GroupBidFinishedListener listener)	{
		    listeners.add(listener);
		  }
		  public synchronized void removeEventListener(GroupBidFinishedListener listener)	{
		    listeners.remove(listener);
		  }

		  // call this method whenever you want to notify
		  //the event listeners of the particular event
		  public synchronized void fireEvent()	{
		    GroupBidFinished event = new GroupBidFinished(this);
		    Iterator<GroupBidFinishedListener> i = listeners.iterator();
		    while(i.hasNext())	{
		      ((GroupBidFinishedListener) i.next()).handleMyEventClassEvent(event);
		    }
		  }
}
