package client;

import model.ClientRequest;
import model.ServerResponse;
import server.RMICompute;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.google.common.collect.Lists;

public class RMIClient implements Client {

    private ClientRequest request;
    private String serverAddress;

    public RMIClient(ClientRequest request, String serverAddress) {
        this.request = request;
        this.serverAddress = serverAddress;
    }

    @Override
    public ServerResponse execute() {

        String response;
        try {
            String name = "RMICompute";
            Registry registry = LocateRegistry.getRegistry(serverAddress);
            RMICompute stub = (RMICompute) registry.lookup(name);
            response = stub.executeTask(request);
        } catch (Exception ex) {
            response = "Closing client...";
            System.out.println(response);
        }
        return new ServerResponse(response, Lists.newArrayList());
    }
}
