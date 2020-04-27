package server;

import model.ClientRequest;

import static constants.OperationName.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends ServerProcessor implements Server, RMICompute {

    private Registry registry;

    public RMIServer() {
        super();
    }

    @Override
    public void startServer() {
        RMICompute stub = null;
        try {
            stub = (RMICompute) UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException ex) {
            //No op
        }
        try {
            String name = "RMICompute";
            initializeRMI();
            this.registry.rebind(name, stub);
            System.out.println("RMI server is up and running...");
        } catch (Exception ex) {
            System.err.println("Encountered exception while starting the RMI server.");
        }
    }

    @Override
    public void stopServer() {
        try {
            this.registry.unbind("RMICompute");
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception ex) {
            System.err.println("Exception occurred while attempting to stop RMI server.");
        }
    }

    @Override
    public String executeTask(ClientRequest clientRequest) {
        System.out.println("Received operation name: " + clientRequest.getOperationName());

        if (clientRequest.getOperationName().equals(EXIT)) {
            stopServer();
            System.out.println("Connection closed..");
            System.exit(1);
        }
        String response = process(clientRequest);
        System.out.println("Request completed.");

        return response;
    }

    private void initializeRMI() {
        try {
            this.registry = LocateRegistry.getRegistry();
            this.registry.list();
        } catch (Exception ex) {
            System.out.println("Registry list throws exception");
            try {
                this.registry = LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                System.err.println("Error while retrieving RMI registry. ");
            }
        }
    }
}
