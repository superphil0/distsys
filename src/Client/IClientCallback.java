/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author daniela
 */
public interface IClientCallback {
    
    public byte[] getClientChallenge();
    
    public void resendLastCommand();
    
}
