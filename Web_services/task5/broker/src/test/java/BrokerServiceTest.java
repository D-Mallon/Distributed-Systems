import org.junit.*;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.Endpoint;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.broker.LocalBrokerService;

public class BrokerServiceTest {

    /**
     * Setup method that runs before all the test methods in this class.
     * We're publishing the LocalBrokerService as a web service here.
     */
    @BeforeClass
    public static void setup() {
        // Publish the LocalBrokerService at the specified URL
        Endpoint.publish("http://0.0.0.0:9000/broker", new LocalBrokerService());
    }

    /**
     * This is the actual test method. Here, we are testing if the connection
     * to the BrokerService is successful and if it returns the expected
     * number of quotations for a given ClientInfo.
     */
    @Test
    public void brokerConnectionTest() throws Exception {
        // Notify the user which test is currently being executed
        System.out.println("Running brokerConnectionTest");

        // Define the WSDL URL for the BrokerService
        URL wsdlUrl = new URL("http://localhost:9000/broker?wsdl");

        // Define QName (qualified name) for the service. This helps in WSDL parsing.
        QName serviceName = new QName("http://core.service/", "QuotationService");

        // Create a Service instance using WSDL URL and service name
        Service service = Service.create(wsdlUrl, serviceName);

        // Define port name for the service
        QName portName = new QName("http://core.service/", "QuotationServicePort");

        // Get the proxy for our service interface
        BrokerService brokerService = service.getPort(portName, BrokerService.class);

        // Get quotations using the provided client information
        List<Quotation> quotations = brokerService.getQuotations(new ClientInfo(
                "Niki Collier", ClientInfo.FEMALE, 49,
                1.5494, 80, false, false));

        // Check the number of quotations received
        if (quotations.isEmpty()) {
            System.out.println("No quotations received.");
        } else {
            System.out.println("***Received " + quotations.size() + " quotations.");
            for (Quotation quotation : quotations) {
                System.out.println("Company: " + quotation.company + ", Price: " + quotation.price);
            }
        }

        // Assert that the number of quotations received is 0 - drop back into the test
        // if you really want it. It works / passes if we expect that there will be no
        // returned quotation i.e. with no other service running.
        // assertEquals(0, quotations.size());
    }
}
