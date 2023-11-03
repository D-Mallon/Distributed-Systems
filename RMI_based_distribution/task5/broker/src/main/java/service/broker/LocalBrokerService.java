package service.broker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;
import java.rmi.RemoteException;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

public class LocalBrokerService implements BrokerService {

    @Override
    public List<Quotation> getQuotations(ClientInfo info) throws RemoteException {
        List<Quotation> quotations = new LinkedList<Quotation>();

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            String[] services = registry.list();
            for (String service : services) {
                if (service.startsWith("qs-", 0)) {
                    QuotationService quotationService = (QuotationService) registry.lookup(service);
                    quotations.add(quotationService.generateQuotation(info));
                }
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
        return quotations;
    }

    // New method to register a service with the RMI Registry
    @Override
    public void registerService(String name, java.rmi.Remote service) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(name, service);
        } catch (Exception e) {
            throw new RemoteException("Trouble: ", e);
        }
    }
}
