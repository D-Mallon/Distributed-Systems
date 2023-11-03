package service.core;
// import service.registry.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Interface to define the behaviour of a quotation service.
 * 
 * @author Rem
 *
 */

 @WebService

public interface QuotationService {
	@WebMethod
	public Quotation generateQuotation(ClientInfo info);
}
