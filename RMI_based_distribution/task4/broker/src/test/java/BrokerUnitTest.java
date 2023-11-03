import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

import java.lang.Exception;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.beans.Transient;
import java.rmi.NotBoundException;

import service.broker.LocalBrokerService;
import service.core.AbstractQuotationService;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
import service.core.Constants;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class BrokerUnitTest {
    private static Registry registry;

    @BeforeClass
    public static void setup() {
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099, null);
            BrokerService brokerService = (BrokerService) UnicastRemoteObject.exportObject(new LocalBrokerService(), 0, null, null, null);
            registry.rebind(Constants.BROKER_SERVICE, brokerService);
        } catch (RemoteException e) {
            try {
                System.out.println("Generating a new registry as none were located.");
                registry = LocateRegistry.createRegistry(1099, null, null);
                BrokerService brokerService = (BrokerService) UnicastRemoteObject.exportObject(new LocalBrokerService(), 0, null, null, null);
                registry.rebind(Constants.BROKER_SERVICE, brokerService);
            }
            catch (RemoteException e1) {
                System.out.println("Trouble e1: " + e1); 
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }
    }

    @Test
    public void findQuotations() throws Exception {
        LocalBrokerService brokerService = new LocalBrokerService();
        ClientInfo Joe = new ClientInfo("Joe Dolan", ClientInfo.MALE, 32, 5, 99, false, false);

        List<Quotation> quotations = brokerService.getQuotations(Joe);

        if(quotations.isEmpty()) {
            System.out.println("Quotations is empty. This is our quotation list: " + quotations);
            assertTrue("Quotations list is empty as there are no services bound", quotations.isEmpty());
        } else {
            for (Quotation quotation : quotations) {
                System.out.println(quotation);
            }
            System.out.println("Quotations are present. Here they are: " + quotations);
            assertTrue("We have quotations as we have registered services", !quotations.isEmpty());
        }
    }
}
