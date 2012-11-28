/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagementClient;

import Common.IManagementClientCallback;
import Events.AuctionEvent;
import Events.BidEvent;
import Events.Event;
import Events.StatisticsEvent;
import Events.UserEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author daniela
 */
public class ManagementClientCallback implements IManagementClientCallback, Serializable {

    private ManagementClient mc;
    private ArrayList received;
    private int index = 0;

    public ManagementClientCallback(ManagementClient mc) {
        this.mc = mc;
        received = new ArrayList(10);
    }

    public void receiveEvent(Event event) throws RemoteException {
        String output = event.getType() + " " + new Date(event.getTimestamp()) + " - ";

        if (event != null && !received.contains(event.getID())) {
            if (event instanceof AuctionEvent) {
                AuctionEvent auctionEvent = (AuctionEvent) event;
                output += "auction with id " + auctionEvent.getAuctionID();
                if (auctionEvent.getType().equals("AUCTION_STARTED")) {
                    output += " has been started";
                } else if (auctionEvent.getType().equals("AUCTION_ENDED")) {
                    output += " has ended";
                }

            } else if (event instanceof BidEvent) {
                BidEvent bidEvent = (BidEvent) event;
                if (bidEvent.getType().equals("BID_PLACE")) {
                    output += "user " + bidEvent.getUserName() + " placed bid " + bidEvent.getPrice() + " on auction " + bidEvent.getAuctionID();
                } else if (bidEvent.getType().equals("BID_OVERBID")) {
                    output += "user " + bidEvent.getUserName() + " placed overbid " + bidEvent.getPrice() + " on auction " + bidEvent.getAuctionID();

                } else if (bidEvent.getType().equals("BID_WON")) {
                    output += "user " + bidEvent.getUserName() + " won with " + bidEvent.getPrice() + " on auction " + bidEvent.getAuctionID();

                }

            } else if (event instanceof UserEvent) {
                UserEvent userEvent = (UserEvent) event;
                output += userEvent.getUserName();
                if (userEvent.getType().equals("USER_LOGIN")) {
                    output += " logged in";

                } else if (userEvent.getType().equals("USER_LOGOUT")) {
                    output += " logged out";

                } else if (userEvent.getType().equals("USER_DISCONNECTED")) {
                    output += " disconnected";
                }

            } else if (event instanceof StatisticsEvent) {
                StatisticsEvent statisticsEvent = (StatisticsEvent) event;

                if (statisticsEvent.getType().equals("USER_SESSIONTIME_MIN")) {
                    output += "minimum session time is " + (int) statisticsEvent.getValue() + " seconds";

                } else if (statisticsEvent.getType().equals("USER_SESSIONTIME_MAX")) {
                    output += "maximum session time is " + (int) statisticsEvent.getValue() + " seconds";

                } else if (statisticsEvent.getType().equals("USER_SESSIONTIME_AVG")) {
                    output += "average session time is " + (int) statisticsEvent.getValue() + " seconds";

                } else if (statisticsEvent.getType().equals("BID_PRICE_MAX")) {
                    output += "maximum bid price seen so far is " + statisticsEvent.getValue();

                } else if (statisticsEvent.getType().equals("BID_COUNT_PER_MINUTE")) {
                    output += "current bids per minute is " + statisticsEvent.getValue();

                } else if (statisticsEvent.getType().equals("AUCTION_TIME_AVG")) {
                    output += "average auction time is " + (int) statisticsEvent.getValue() + " seconds";

                } else if (statisticsEvent.getType().equals("AUCTION_SUCCESS_RATIO")) {
                    output += "auction success ratio is " + statisticsEvent.getValue();

                }

            }
            received.add(index, event.getID());
            index = (index+1)%10;
            mc.receiveMessage(output);
            //System.out.println(output);
        }
    }
}
