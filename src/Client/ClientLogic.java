package Client;

import User.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientLogic {

    private static final String CHARSET = "UTF-8";
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String response;

    /**
     *
     * @param bid <auctionID> <price>
     * @param client1
     * @param port1
     * @param client2
     * @param port2
     * @return
     */
    public String getSignedBid(String bid, User user1, User user2) {

        response = "!signedBid " + bid;
        String timestamp1 = getTimestamp(bid, user1);
        String timestamp2 = getTimestamp(bid, user2);

        if (timestamp1 != null && timestamp2 != null) {
            return response + " " + timestamp1 + " " + timestamp2;
        } else {
            return null;
        }



    }

    private String getTimestamp(String bid, User user) {
        String clientResponse = null;

        try {
            socket = new Socket(user.getSocket().getInetAddress(), user.getPort());

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            out.println("!getTimestamp " + bid);


            //expected !timestamp <auctionID> <price> <timestamp> <signature>
            String fromClient = in.readLine();
            String[] parts = response.split(" ");
            if (parts.length == 4) {
                clientResponse = (user.getUsername() + ":" + parts[3] + ":" + parts[4]);
            } else {
                //Invalid client response
                //return null;
            }

        } catch (IOException ex) {
            System.out.println("IO Ex: Couldn't connect to other clients.");
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
            }
        }
        return clientResponse;

    }
}
