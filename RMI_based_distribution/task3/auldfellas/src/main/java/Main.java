import java.rmi.registry.*;
import java.rmi.server.
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;tring[] args) {
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
    
        // Register the object with the RMI Registry 
        registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);

        //rename
        
        //Try commenting this out to see what happens
        System.out.println("STOPPING SERVER SHUTDOWN");
            while (true) {Thread.sleep(1000); } 
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }
    }   
}