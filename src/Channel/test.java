/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Channel;

import Exceptions.HMacException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class test {

    private static String path, username;
    private static HMac hmac;

    public static void main(String[] args) {
        try {
            path = "keys/";
            username = "alice";
            hmac = new HMac(path, username);


            String test = "blubber blubber";
            byte[] bmac = hmac.getMac(test);
            System.out.println(new String(bmac));
            
            String test64 = Base64.encode(bmac);
            System.out.println(test64);
            try {
                byte[] bmac2 = Base64.decode(test64);
                            System.out.println(new String(bmac2));

            } catch (Base64DecodingException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
            

        } catch (HMacException ex) {
            System.out.println(ex.getMessage());
        }





    }
}
