import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import service.core.QuotationService;
import service.core.AbstractQuotationService;
import service.core.BrokerService;
import service.core.Constants;
import service.broker.LocalBrokerService;

public class Main {
    public static void main(String[] args) {
        BrokerService localbrokerservice = new LocalBrokerService();

        try {
            
            Registry registry = null;
            if (args.length == 0) {
                registry = LocateRegistry.createRegistry(1099, null, null);
            } else {
                registry = LocateRegistry.getRegistry(args[0], 1099, null);
            }

            BrokerService brokerService = (BrokerService)
                UnicastRemoteObject.exportObject(localbrokerservice, 0, null, null, null);

            registry.bind(Constants.BROKER_SERVICE, brokerService);

            List<String> previousServices = new ArrayList<>();

            //Try commenting this out to see what happens
            System.out.println("STOPPING SERVER SHUTDOWN");
                while (true) {

                    List<String> currentServices = Arrays.asList(registry.list());
                
                    if (!currentServices.equals(previousServices)) {
                        System.out.println("Broker - Main: Services bound to the registry:");
                        for (String name : currentServices) {
                            System.out.println(name);
                        }
                        previousServices = currentServices;
                    }
                    
                        
                Thread.sleep(1000); } 
        
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }

    }
}
