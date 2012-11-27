/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagementClient;

import Common.IAnalytics;
import Common.IBillingLogin;
import Common.IBillingSecure;
import Common.IManagementClientCallback;
import Events.Event;
import PropertyReader.RegistryProperties;
import Server.AnalyticsServer.AnalyticsServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniela
 */
public class ManagementClient implements IManagementClientCallback {

    private String id = UUID.randomUUID().toString();
    private IBillingSecure billingService = null;
    private IBillingLogin billingLogin;
    private String analyticsBindingName, billingBindingName;
    private static Registry rmiRegistry;
    private BufferedReader stdIn;
    private IAnalytics analyticsService;
        private int port = RegistryProperties.getPort();
        private String host = RegistryProperties.getHost();


    public static void main(String[] args) throws RemoteException {

        RegistryProperties r = new RegistryProperties();
        //args: bindingNames 0-analytics 1-billing
        if (args.length == 2) {
            //System.out.println(args[0] + " " + args[1]);
            ManagementClient client = new ManagementClient();
            client.setAnalyticsBindingName(args[0]);
            client.setBillingBindingName(args[1]);
            client.start();
        } else {

            System.out.println("Invalid input arguments! \n"
                    + "Please provide binding name from Analytics and Billing Server");
        }

        //analyticsBindingName = args[0];
        //billingBindingName = args[1];

    }

    private void start() {
        try {
            rmiRegistry = LocateRegistry.getRegistry(host, port);
            
            System.out.println("registry located");
            
            analyticsService = (IAnalytics) rmiRegistry.lookup(analyticsBindingName);
            billingLogin = (IBillingLogin) rmiRegistry.lookup(billingBindingName);
        } catch (AccessException ex) {
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            System.out.println("Problems with ServerConnection ");
            System.out.println("analyticsService: " + analyticsBindingName + " " + analyticsService == null);
            System.out.println("billingLogin: " + billingBindingName + " " + billingLogin == null);
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("Problems with ServerConnection ");
            System.out.println("analyticsService: " + analyticsBindingName);
            System.out.println("billingLogin: " + billingBindingName);
        }
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;
        String[] input;

        try {
            //reading UserInput
            while ((fromUser = stdIn.readLine()) != null) {
                if (fromUser.startsWith("!login") || fromUser.startsWith("!steps") || fromUser.startsWith("!addStep") || fromUser.startsWith("!removeStep") || fromUser.startsWith("!bill") || fromUser.startsWith("!logout")) {
                    //talk to billing server
                    if (billingService == null) {
                        input = fromUser.split(" ");
                        if (input.length == 3) {
                            billingService = billingLogin.login(input[1], input[2]);
                        } else {
                            System.out.println("Please login! Usage: !login <username> <pw>");
                        }

                    } else {
                        if (fromUser.equals("!logout")) {
                            billingService = null;
                        } else if (fromUser.equals("!steps")) {
                            System.out.println(billingService.getPriceSteps().toString());
                        } else if (fromUser.startsWith("!addStep")) {
                            input = fromUser.split(" ");
                            if (input.length == 5) {
                                try {
                                    billingService.createPriceStep(Double.parseDouble(input[1]), Double.parseDouble(input[2]), Double.parseDouble(input[3]), Double.parseDouble(input[4]));
                                } catch (NumberFormatException ex) {
                                    System.out.println("Usage: !addStep <startPrice> <endPrice> <fixedPrice> <variablePricePercent>");
                                }
                            } else {
                                System.out.println("Usage: !addStep <startPrice> <endPrice> <fixedPrice> <variablePricePercent>");

                            }
                        } else if (fromUser.startsWith("!removeStep")) {
                            input = fromUser.split(" ");
                            if (input.length == 3) {
                                try {
                                    billingService.deletePriceStep(Double.parseDouble(input[1]), Double.parseDouble(input[2]));
                                } catch (NumberFormatException ex) {
                                    System.out.println("Usage: !removeStep <startPrice> <endPrice>");
                                }
                            } else {
                                System.out.println("Usage: !removeStep <startPrice> <endPrice>");
                            }
                        } else if (fromUser.startsWith("!bill")) {
                            input = fromUser.split(" ");
                            if (input.length == 2) {
                                try {
                                    billingService.getBill(input[1]);
                                } catch (NumberFormatException ex) {
                                    System.out.println("Usage: !bill <userName>");
                                }
                            } else {
                                System.out.println("Usage: !bill <userName>");
                            }
                        } else {
                            System.out.println("Unknown Command");
                        }

                    }
                } else if (fromUser.startsWith("!subscribe") || fromUser.startsWith("!unsubscribe") || fromUser.startsWith("!auto") || fromUser.startsWith("!hide") || fromUser.startsWith("!print")) {
                    //talk to analytics server
                    if (fromUser.startsWith("!subscribe")) {
                        input = fromUser.split(" ");
                        if (input.length == 2) {
                            analyticsService.subscribe(input[1], this);
                        } else {
                            System.out.println("Usage: !subscribe <filterRegex>");
                        }
                    } else if (fromUser.startsWith("!unsubscribe")) {
                        input = fromUser.split(" ");
                        if (input.length == 2) {
                            analyticsService.unsubscribe(input[1]);
                        } else {
                            System.out.println("Usage: !unsubscribe <id>");
                        }

                    } else if (fromUser.equals("!auto")) {
                        //TODO
                    } else if (fromUser.equals("!hide")) {
                        //TODO
                    } else if (fromUser.equals("!print")) {
                        //TODO
                    } else {
                        System.out.println("Unknown Command");
                    }

                } else {
                    System.out.println("Unknown Command");
                }
            }

        } catch (IOException e) {
            System.err.println("I/O Fehler");
        } finally {
            System.out.println("Bye.");
        }
    }

    public String getAnalyticsBindingName() {
        return analyticsBindingName;
    }

    public void setAnalyticsBindingName(String analyticsBindingName) {
        this.analyticsBindingName = analyticsBindingName;
    }

    public String getBillingBindingName() {
        return billingBindingName;
    }

    public void setBillingBindingName(String billingBindingName) {
        this.billingBindingName = billingBindingName;
    }

    public void receiveEvent(Event event) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getID() {
        return id;
    }
}
