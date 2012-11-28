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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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
public class ManagementClient implements IManagementClientCallback, Serializable {

    private String id = UUID.randomUUID().toString();
    private IBillingSecure billingService = null;
    private IBillingLogin billingLogin;
    private String analyticsBindingName, billingBindingName;
    private static Registry rmiRegistry;
    private BufferedReader stdIn;
    private IAnalytics analyticsService;
    private int port = RegistryProperties.getPort();
    private String host = RegistryProperties.getHost();
    private String storedMessages = "";
    private boolean printEvents = true;

    public static void main(String[] args) throws RemoteException {

        new RegistryProperties();
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


            //TEST
            System.out.println(analyticsService.subscribe("blubb", (IManagementClientCallback) this));
            /*System.out.println(analyticsService.subscribe("blubb", this));
             System.out.println(analyticsService.subscribe("blubb", this));
             System.out.println(analyticsService.subscribe("blubb", this));
            
             analyticsService.unsubscribe("1");
             analyticsService.unsubscribe("2");
            
             */


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
        // test code
        IBillingSecure secure = null;
        try {
			 secure = billingLogin.login("john", "dslab2012");
			 secure.createPriceStep(10, 20, 10, 3);
			 System.out.println(secure.getPriceSteps());
			 secure.deletePriceStep(10, 20);
			 System.out.println(secure.getPriceSteps());
			 secure.createPriceStep(10, 40, 10, 3);
			 secure.createPriceStep(40, 0, 20, 5);
			 System.out.println(secure.getPriceSteps());
			 secure.billAuction("Philipp", 2, 20);
			 secure.billAuction("Philipp", 3, 2000);
			 secure.billAuction("Philipp", 4, 40);
			 secure.billAuction("Philipp", 5, 30);
			 secure.billAuction("Paiö", 6, 30);
			 System.out.println(secure.getBill("Philipp"));
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;

        try {
            //reading UserInput

            while ((fromUser = stdIn.readLine()) != null && !fromUser.equals("!end")) {
                    processInput(fromUser);
            }


        } catch (IOException e) {
            System.err.println("I/O Fehler");
        } finally {
            System.out.println("Bye.");
        }
    }

    private void processInput(String fromUser) {
        String[] input;
        
        if (fromUser.startsWith("!login") || fromUser.startsWith("!steps") || fromUser.startsWith("!addStep") || fromUser.startsWith("!removeStep") || fromUser.startsWith("!bill") || fromUser.startsWith("!logout")) {
            //talk to billing server
            if (billingService == null) {
                input = fromUser.split(" ");
                if (input.length == 3) {
                    try {
                        billingService = billingLogin.login(input[1], input[2]);
                    } catch (RemoteException ex) {
                        Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("Please login! Usage: !login <username> <pw>");
                }

            } else {
                if (fromUser.equals("!logout")) {
                    billingService = null;
                } else if (fromUser.equals("!steps")) {
                    try {
						System.out.println(billingService.getPriceSteps().toString());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else if (fromUser.startsWith("!addStep")) {
                    input = fromUser.split(" ");
                    if (input.length == 5) {
                        try {
                            try {
                                billingService.createPriceStep(Double.parseDouble(input[1]), Double.parseDouble(input[2]), Double.parseDouble(input[3]), Double.parseDouble(input[4]));
                            } catch (RemoteException ex) {
                                Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
                            try {
                                billingService.deletePriceStep(Double.parseDouble(input[1]), Double.parseDouble(input[2]));
                            } catch (RemoteException ex) {
                                Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
                            try {
                                System.out.append(billingService.getBill(input[1]).toString());
                            } catch (RemoteException ex) {
                                Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
        } else if (fromUser.startsWith("!subscribe")) {
            input = fromUser.split(" ");
            if (input.length == 2) {
                try {
                    String id = analyticsService.subscribe(input[1], (IManagementClientCallback) this);
                    System.out.println("Created subscription with ID " + id + " for events using '" + input[1] + "'");

                } catch (RemoteException ex) {
                    Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Usage: !subscribe <filterRegex>");
            }
        } else if (fromUser.startsWith("!unsubscribe")) {
            input = fromUser.split(" ");
            if (input.length == 2) {
                try {
                    analyticsService.unsubscribe(input[1]);
                    System.out.println("subscription " + input[1] + " terminated");
                } catch (RemoteException ex) {
                    Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        if (printEvents) {
            System.out.println(event);
        } else {
            storedMessages += event;
        }
    }

    public String getID() {
        return id;
    }
}
