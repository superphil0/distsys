package Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

import Channel.Base64Channel;
import Channel.SecureChannel;
import Channel.TCPChannel;

public class TimeStampResponder extends Thread {

    private static final String CHARSET = "UTF-8";
    private Socket socket;
    private PrivateKey key;
    private SecureChannel secureChannel;
    private BufferedReader in;
    private PrintWriter out;

    public TimeStampResponder(Socket accept, PrivateKey key) {
        socket = accept;
        this.key = key;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            secureChannel = new SecureChannel(new Base64Channel(new TCPChannel(out, in)));
            secureChannel.setPrivKey(key);

        } catch (IOException e) {
            out.println("Problem with In or Output - Connection.");
        }

    }
}
