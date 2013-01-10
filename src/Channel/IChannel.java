/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import java.io.IOException;

/**
 *
 * @author daniela
 */
public interface IChannel {
    
    //printLine
    public void send (String message);
    
    //readLine
    public String receive() throws IOException;
    
    public void close() throws IOException;
        
}
