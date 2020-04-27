package server;

import model.ClientRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMICompute extends Remote {

    String executeTask(ClientRequest clientRequest) throws RemoteException;

}
