/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author daniela
 */
public class KeyNotFoundException extends Exception {

    public KeyNotFoundException() {
    }

    public KeyNotFoundException(String string) {
        super(string);
    }

    public KeyNotFoundException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public KeyNotFoundException(Throwable thrwbl) {
        super(thrwbl);
    }
    
}
