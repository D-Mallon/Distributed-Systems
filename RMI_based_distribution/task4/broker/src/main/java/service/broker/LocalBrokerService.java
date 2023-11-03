package service.broker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import service.core.AbstractQuotationService;
import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;
import service.core.Constants;


public class LocalBrokerService implements BrokerService {
    public List<Quotation> getQuotations(ClientInfo info) throws RemoteException {
        List<Quotation> quotations = new LinkedList<Quotation>();

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099, null);

            String[] services = registry.list();

            for (String service: services) {
                if (service.startsWith("qs-", 0)){
                    QuotationService quotationService = (QuotationService) registry.lookup(service);
                    quotations.add(quotationService.generateQuotation(info));
                }
            }
        } catch (Exception e) {
            System.out.println("Trouble: " + e); 
        }

        return quotations;
    }
}