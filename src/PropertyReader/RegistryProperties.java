package PropertyReader;


import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daniela
 */
public class RegistryProperties extends PropertyReader{
    
    private static String host;
    private static int port;
    private static String path = "registry.properties";
<<<<<<< HEAD
    private RegistryProperties registryProperties = new RegistryProperties(); 
    
    private RegistryProperties() {
=======
    //private RegistryProperties registryProperties = new RegistryProperties(); 
    
    public RegistryProperties() {
>>>>>>> Dani
        super(path);
        host = props.getProperty("registry.host");
        port = Integer.parseInt(props.getProperty("registry.port"));
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }
    
}
