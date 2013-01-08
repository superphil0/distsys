/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author daniela
 */
public class AESException extends Exception{

    public AESException() {
    }

    public AESException(String string) {
        super(string);
    }

    public AESException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public AESException(Throwable thrwbl) {
        super(thrwbl);
    }

    
}
