import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.xml.ws.Endpoint;
import service.dodgygeezers.DGQService;

public class DodgygeezersMain {

    private static void jmdnsAdvertise(String host) {
        try {
            // String config = "path=http://" + host + ":9002/quotations?wsdl";
            String config = "path=http://dodgygeezers:9002/quotations?wsdl";
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create("_quote._tcp.local.", "dodgygeezers-service", 9002, config);
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
        Endpoint.publish("http://0.0.0.0:9002/quotations", new DGQService());
        jmdnsAdvertise(host);
    }
}
