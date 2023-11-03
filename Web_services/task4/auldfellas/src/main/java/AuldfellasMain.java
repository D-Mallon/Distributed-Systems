import javax.xml.ws.Endpoint;
import service.auldfellas.AFQService;

public class AuldfellasMain {
    public static void main(String[] args) {
        Endpoint.publish("http://0.0.0.0:9001/quotations", new AFQService());
    }
}