import org.junit.*;
import static org.junit.Assert.assertNotNull;

import service.core.Quotation;
import service.core.QuotationService;
import service.core.ClientInfo;
import service.auldfellas.AFQService;

import javax.xml.ws.Endpoint;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class QuotationServiceUnitTest {

    /**
     * Setup method that runs before all the test methods in this class.
     * We're publishing the AFQService as a web service here.
     */
    @BeforeClass
    public static void setup() {
        // Publish the AFQService at the specified URL
        Endpoint.publish("http://0.0.0.0:9001/quotation", new AFQService());
    }

    /**
     * This is the actual test method. Here, we are testing if the connection
     * to the QuotationService is successful and if we get a valid quotation
     * response for a given ClientInfo.
     */
    @Test
    public void connectionTest() throws Exception {
        // Notify the user which test is currently being executed
        System.out.println("Running AF_connectionTest");

        // Define the WSDL URL for the QuotationService
        URL wsdlUrl = new URL("http://localhost:9001/quotation?wsdl");

        // Define QName (qualified name) for the service. This helps in WSDL parsing.
        QName serviceName = new QName("http://core.service/", "QuotationService");

        // Create a Service instance using WSDL URL and service name
        Service service = Service.create(wsdlUrl, serviceName);

        // Define port name for the service
        QName portName = new QName("http://core.service/", "QuotationServicePort");

        // Get the proxy for our service interface
        QuotationService quotationService = service.getPort(portName, QuotationService.class);

        // Generate a quotation using the provided client information
        Quotation quotation = quotationService
                .generateQuotation(new ClientInfo(
                        "Niki Collier", ClientInfo.FEMALE, 49,
                        1.5494, 80, false, false));

        // Print the company name and reference number received in the quotation
        System.out.println("Company: " + quotation.company);
        System.out.println("Reference: " + quotation.reference);

        // Assert that the quotation received is not null
        assertNotNull(quotation);
    }
}
