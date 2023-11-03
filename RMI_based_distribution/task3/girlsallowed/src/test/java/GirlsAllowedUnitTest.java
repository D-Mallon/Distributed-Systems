import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;
import service.core.QuotationService;
import service.girlsallowed.GAQService;

import org.junit.*;
import static org.junit.Assert.assertNotNull;

public class GirlsAllowedUnitTest {
    private static Registry registry;
    @BeforeClass
    public static void setup() {
        QuotationService gaqService = new GAQService();
        try {
            registry = LocateRegistry.createRegistry(1099);

            QuotationService quotationService = (QuotationService)
            UnicastRemoteObject.exportObject(gaqService, 0);

            registry.bind(Constants.GIRLS_ALLOWED_SERVICE, quotationService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService)
            registry.lookup(Constants.GIRLS_ALLOWED_SERVICE);
        assertNotNull(service);
    }

    @Test
    public void quotationTest() throws Exception {
        QuotationService service = (QuotationService)
            registry.lookup(Constants.GIRLS_ALLOWED_SERVICE);

        Quotation test_Quotation = service.generateQuotation(new ClientInfo("Jane Doe", ClientInfo.FEMALE, 35, 1.6, 55, false, false));

        assertNotNull(test_Quotation);
    }
}
