/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author daniela
 */
public class TCPChannel implements IChannel {

    private PrintWriter out = null;
    private BufferedReader in = null;
    protected TCPChannel channel;

    public TCPChannel(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;

    }


    public TCPChannel(TCPChannel channel) {
        this.channel = channel;
        this.out = channel.getOutputWriter();
        this.in = channel.getInputReader();
    }

    private BufferedReader getInputReader() {
        return in;
    }

    private PrintWriter getOutputWriter() {
        return out;
    }

    public void send(String message) {
                System.out.println("step 3 send");

        out.println(message);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        in.close();
        out.close();
    }
}
