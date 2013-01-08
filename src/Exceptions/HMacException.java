/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author daniela
 */
public class HMacException extends Exception{

    public HMacException() {
    }

    public HMacException(String string) {
        super(string);
    }

    public HMacException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public HMacException(Throwable thrwbl) {
        super(thrwbl);
    }
    
}
