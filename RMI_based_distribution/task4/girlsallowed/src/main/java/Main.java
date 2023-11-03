import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

import service.core.QuotationService;
import service.girlsallowed.GAQService;
import service.core.Constants;

public class Main {
    public static void main (String[] args) {
    QuotationService GAQService = new GAQService(); 

    try {
        // Connect to the RMI Registry - creating the registry will be the // responsibility of the broker.
        Registry registry = null;
        if (args.length == 0) {
            registry = LocateRegistry.createRegistry(1099, null, null);
        } else {
            registry = LocateRegistry.getRegistry(args[0], 1099, null);
        }
    
        // Create the Remote Object
        QuotationService quotationService = (QuotationService)
            UnicastRemoteObject.exportObject(GAQService,0);
    
        // Register the object with the RMI Registry 
        registry.bind(Constants.GIRLS_ALLOWED_SERVICE, quotationService);

        //rename
        
        //Try commenting this out to see what happens
        System.out.println("STOPPING SERVER SHUTDOWN");
            while (true) {Thread.sleep(1000); } 
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }
    }   
}