import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import service.broker.LocalBrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

public class BrokerMain {

    public static void main(String[] args) {
        Endpoint.publish("http://0.0.0.0:9000/broker", new LocalBrokerService());

        try {
            // Create a JmDNS instance with the local host's InetAddress
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_quote._tcp.local.", new JmdnsClient());

            // Wait indefinitely. You can adjust this as per your requirement.
            Thread.currentThread().join();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class JmdnsClient implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
            // Possible work to review later - should I remove the service URL from
            // LocalBrokerService?
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
            String url = event.getInfo().getURL();
            LocalBrokerService.addServiceURL(url);
            // Option to test the service right away
            // invokeQuotationService(url);
        }

        // Test the discovered service (kept
        // it for future reference)
        // private void invokeQuotationService(String url) {
        // try {
        // URL wsdlUrl = new URL(url + "?wsdl");

        // QName serviceName = new QName("http://core.service/", "QuotationService");
        // Service service = Service.create(wsdlUrl, serviceName);

        // QName portName = new QName("http://core.service/", "QuotationServicePort");
        // QuotationService quotationService = service.getPort(portName,
        // QuotationService.class);

        // // Using a sample client for testing purposes
        // ClientInfo testClient = new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49,
        // 1.5494, 80, false, false);

        // Quotation quotation = quotationService.generateQuotation(testClient);

        // // Print the details of the received quotation
        // System.out.println("Company: " + quotation.company);
        // System.out.println("Reference: " + quotation.reference);
        // System.out.println("Price: " + quotation.price);

        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
    }
}

// try {
// JmDNS jmdns = JmDNS.create();
// jmdns.addServiceListener("_quote._tcp.local.", new ServiceListener() {
// @Override
// public void serviceAdded(ServiceEvent event) {
// System.out.println("Service added: " + event.getInfo());
// }

// @Override
// public void serviceRemoved(ServiceEvent event) {
// System.out.println("Service removed: " + event.getInfo());
// }

// @Override
// public void serviceResolved(ServiceEvent event) {
// System.out.println("Service resolved: " + event.getInfo());
// LocalBrokerService.addServiceURL(event.getInfo().getURL());
// }
// });
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }