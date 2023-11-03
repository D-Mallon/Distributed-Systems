import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import service.core.BrokerService;
import service.core.Constants;
import service.broker.LocalBrokerService;

public class BrokerMain {
    public static void main(String[] args) {
       
        BrokerService localBrokerService = new LocalBrokerService();

        try {
            Registry registry;
            if (args.length == 0) {
                registry = LocateRegistry.createRegistry(1099);
            } else {
                registry = LocateRegistry.getRegistry(args[0], 1099);
            }

            BrokerService brokerService = (BrokerService)
                    UnicastRemoteObject.exportObject(localBrokerService, 0);

            registry.bind(Constants.BROKER_SERVICE, brokerService);

            List<String> previousServices = new ArrayList<>();

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
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }
}
