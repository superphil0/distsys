/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author daniela
 */
public class WrongPasswordException extends Exception {

    public WrongPasswordException() {
    }

    public WrongPasswordException(String string) {
        super(string);
    }

    public WrongPasswordException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public WrongPasswordException(Throwable thrwbl) {
        super(thrwbl);
    }

    
}
