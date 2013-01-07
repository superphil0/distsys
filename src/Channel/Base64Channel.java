/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author daniela
 */
public class Base64Channel extends TCPChannel {

    protected IChannel channel;

    public Base64Channel(PrintWriter out, BufferedReader in) {
        super(out, in);

    }

    public Base64Channel(TCPChannel channel) {
        super(channel);
        this.channel = channel;
    }

    /**
     * Expects as Input a String message which is gonna be base64 encoded and
     * sent via the out-channel
     *
     * @param message
     */
    public void send(String message) {
       // System.out.println("step 2 b64");

        byte[] byteMessage = string2Bytes(message);
        byte[] base64Message = encodeBase64(byteMessage);
        String msg = bytes2String(base64Message);
        channel.send(msg);

    }

    /**
     * Receives via the in-channel a base64 encoded message decodes and returns
     * it
     *
     * @return decoded received message
     * @throws IOException
     */
    public String receive() throws IOException {
        //System.out.println("step 2.1 receive b64");

        String msg = channel.receive();
        //System.out.println("step 2.2 receive b64");

        byte[] base64Message = string2Bytes(msg);
        byte[] byteMessage = decodeBase64(base64Message);
        String message = bytes2String(byteMessage);
        return message;

    }

    //Helpers
    private byte[] string2Bytes(String message) {
        return message.getBytes();
    }

    private String bytes2String(byte[] byteMessage) {
        return new String(byteMessage);
    }

    private byte[] encodeBase64(byte[] byteMessage) {
        byte[] base64Message = Base64.encode(byteMessage);
        return base64Message;
    }

    private byte[] decodeBase64(byte[] base64Message) {
        byte[] byteMessage = Base64.decode(base64Message);
        return byteMessage;
    }
    /*
     
     [...]
     // encode into Base64 format 
     byte[] encryptedMessage = ... 
     byte[] base64Message = Base64.encode(encryptedMessage);
     // decode from Base64 format 
     encryptedMessage = Base64.decode(base64Message);
     
     */
}
