import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;
import service.core.QuotationService;
import service.dodgygeezers.DGQService;

import org.junit.*;
import static org.junit.Assert.assertNotNull;

public class DodgygeezersUnitTest {
    private static Registry registry;
    @BeforeClass
    public static void setup() {
        QuotationService dgqService = new DGQService();
        try {
            registry = LocateRegistry.createRegistry(1099);

            QuotationService quotationService = (QuotationService)
            UnicastRemoteObject.exportObject(dgqService, 0);

            registry.bind(Constants.DODGY_GEEZERS_SERVICE, quotationService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService)
            registry.lookup(Constants.DODGY_GEEZERS_SERVICE);
        assertNotNull(service);
    }

    @Test
    public void quotationTest() throws Exception {
        QuotationService service = (QuotationService)
            registry.lookup(Constants.DODGY_GEEZERS_SERVICE);

        Quotation test_Quotation = service.generateQuotation(new ClientInfo("Joe Doe", ClientInfo.MALE, 65, 1.8, 100, true, true));

        assertNotNull(test_Quotation);
    }
}
