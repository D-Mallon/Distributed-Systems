import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry; 
import java.rmi.server.UnicastRemoteObject;

import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;
import service.core.QuotationService; 
import service.auldfellas.AFQService;

import org.junit.*;
import static org.junit.Assert.assertNotNull;

public class AuldfellasUnitTest { 
    private static Registry registry;
    @BeforeClass
    public static void setup() {
        QuotationService afqService = new AFQService(); 
        try {
            registry = LocateRegistry.createRegistry(1099);

            QuotationService quotationService = (QuotationService) 
            UnicastRemoteObject.exportObject(afqService,0);

            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);
         } catch (Exception e) {
            System.out.println("Trouble: " + e); }
        }

@Test
public void connectionTest() throws Exception {
QuotationService service = (QuotationService) 
    registry.lookup(Constants.AULD_FELLAS_SERVICE);
assertNotNull(service); 
}

//Adding in unit test for quotation
@Test
public void quotationTest() throws Exception {
    QuotationService service = (QuotationService)
    registry.lookup(Constants.AULD_FELLAS_SERVICE);

    Quotation test_Quotation = service.generateQuotation(new ClientInfo("John Doe", ClientInfo.MALE, 65, 1.8, 100, true, true));

    // Asserting that the quotation object is not null
    assertNotNull(test_Quotation);
}


}
