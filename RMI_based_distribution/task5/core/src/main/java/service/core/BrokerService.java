package service.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// import service.registry.Service;

/**
 * Interface for defining the behaviours of the broker service
 * @author Rem
 *
 */
public interface BrokerService extends Remote {
	public List<Quotation> getQuotations(ClientInfo info) throws RemoteException;
	public void registerService(String name, java.rmi.Remote service) throws RemoteException;
}
