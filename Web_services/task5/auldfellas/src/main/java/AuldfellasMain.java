import javax.xml.ws.Endpoint;
import service.auldfellas.AFQService;
import java.net.InetAddress;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class AuldfellasMain {

    private static void jmdnsAdvertise(String host) {
        try {
            // String config = "path=http://" + host + ":9001/quotations?wsdl";
            String config = "path=http://auldfellas:9001/quotations?wsdl";
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create("_quote._tcp.local.", "auldfellas-service", 9001, config);
            jmdns.registerService(serviceInfo);

            // Wait a bit
            Thread.sleep(100000);

            // Unregister all services
            jmdns.unregisterAllServices();
        } catch (Exception e) {
            System.out.println("Problem Advertising Service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        Endpoint.publish("http://0.0.0.0:9001/quotations", new AFQService());
        jmdnsAdvertise(host);
    }
}

// import javax.xml.ws.Endpoint;
// import service.auldfellas.AFQService;

// public class AuldfellasMain {
// public static void main(String[] args) {
// Endpoint.publish("http://0.0.0.0:9001/quotations", new AFQService());
// }
// }