package service.broker;

import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

@WebService(name = "QuotationService", targetNamespace = "http://core.service/", serviceName = "QuotationService")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public class LocalBrokerService implements BrokerService {

	@WebMethod
	public List<Quotation> getQuotations(ClientInfo info) {
		LinkedList<Quotation> quotations = new LinkedList<Quotation>();

		String[] quotationServiceURLs = {
				"http://0.0.0.0:9001/quotations",
				"http://0.0.0.0:9002/quotations",
				"http://0.0.0.0:9003/quotations"
		};

		for (String quotationServiceURL : quotationServiceURLs) {
			try {
				System.out.println("Trying: " + quotationServiceURL);

				URL wsdlUrl = new URL(quotationServiceURL + "?wsdl");
				QName serviceName = new QName("http://core.service/", "QuotationService");
				Service service = Service.create(wsdlUrl, serviceName);
				QName portName = new QName("http://core.service/", "QuotationServicePort");
				QuotationService quotationService = service.getPort(portName, QuotationService.class);

				Quotation quotation = quotationService.generateQuotation(info);
				System.out.println("Company: " + quotation.company);
				quotations.add(quotation);

			} catch (Exception e) {
				System.out.println("Failed: " + quotationServiceURL);
			}
		}

		if (quotations.isEmpty()) {
			System.out.println("***No quotation was returned.");
		} else {
			System.out.println("***Received " + quotations.size() + " quotations.");
		}

		return quotations;
	}
}

// public class LocalBrokerService implements BrokerService {
// private List<String> quotationServiceUrls = Arrays.asList(
// "http://0.0.0.0:9001/quotations",
// "http://0.0.0.0:9002/quotations",
// "http://0.0.0.0:9003/quotations");

// // 3b
// @WebMethod
// public List<Quotation> getQuotations(ClientInfo info) {
// List<Quotation> quotations = new LinkedList<Quotation>();

// if (quotations.isEmpty()) {
// System.out.println("***No quotation was returned.");
// } else {
// System.out.println("***Received " + quotations.size() + " quotations.");
// }

// for (String serviceUrl : quotationServiceUrls) {
// QuotationService service = connectToService(serviceUrl);
// if (service != null) {
// quotations.add(service.generateQuotation(info));
// }
// }

// return quotations;
// }

// // Placeholder for connectToService
// // Implement logic to connect to the service using the provided URL and
// return a
// // QuotationService instance
// private QuotationService connectToService(String serviceUrl) {
// return null;
// }
// }
