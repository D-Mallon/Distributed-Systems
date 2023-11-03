import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import service.core.BrokerService;
import service.core.QuotationService;
import service.auldfellas.AFQService;
import service.core.Constants;

public class AuldfellasMain {
    public static void main(String[] args) {
        QuotationService afqService = new AFQService(); 

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
                UnicastRemoteObject.exportObject(afqService,0);
        
            // Locate the broker service in the RMI Registry
            BrokerService remoteBroker = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);
            
            // Use the broker service to register this quotation service
            remoteBroker.registerService(Constants.AULD_FELLAS_SERVICE, quotationService);
        
            // Keep the service running
            System.out.println("STOPPING SERVER SHUTDOWN");
            while (true) {Thread.sleep(1000); } 
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }
    }   
}
