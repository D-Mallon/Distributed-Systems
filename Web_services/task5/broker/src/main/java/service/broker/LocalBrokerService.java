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

import java.util.concurrent.CopyOnWriteArrayList;

@WebService(name = "QuotationService", targetNamespace = "http://core.service/", serviceName = "QuotationService")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public class LocalBrokerService implements BrokerService {
	private static CopyOnWriteArrayList<String> serviceURLs = new CopyOnWriteArrayList<>();

	public static void addServiceURL(String url) {
		serviceURLs.add(url);
	}

	@WebMethod
	public List<Quotation> getQuotations(ClientInfo info) {
		LinkedList<Quotation> quotations = new LinkedList<Quotation>();

		// Loop through the services discovered by JmDNS
		for (String quotationServiceURL : serviceURLs) {
			try {
				System.out.println("Trying: " + quotationServiceURL);

				// URL wsdlUrl = new URL(quotationServiceURL + "?wsdl");
				// trying to reoslve the wsdl url and broker not connecting by removing the
				// above
				URL wsdlUrl = new URL(quotationServiceURL);
				QName serviceName = new QName("http://core.service/", "QuotationService");
				Service service = Service.create(wsdlUrl, serviceName);
				QName portName = new QName("http://core.service/", "QuotationServicePort");
				QuotationService quotationService = service.getPort(portName, QuotationService.class);

				Quotation quotation = quotationService.generateQuotation(info);
				System.out.println("Company: " + quotation.company);
				quotations.add(quotation);

			} catch (Exception e) {
				System.out.println("Failed: " + quotationServiceURL);
				e.printStackTrace();
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
