package client;

import model.ClientRequest;
import model.ServerResponse;

import static constants.OperationName.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPClient implements Client {

    private ClientRequest request;
    private String serverAddress;
    private int serverPort;

    public TCPClient(ClientRequest request, String serverAddress, int serverPort) {
        this.request = request;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public ServerResponse execute() {

        Socket toServer;
        try {
            toServer = new Socket(serverAddress, serverPort);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create socket connection with the server", ex);
        }
        System.out.println("client: connected!");

        ServerResponse response = null;
        try {
            ObjectOutputStream out = new ObjectOutputStream(toServer.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());
            out.writeObject(request);
            out.flush();

            String operationName = request.getOperationName();
            if (!operationName.equals(EXIT)) {
                response = (ServerResponse) in.readObject();
                System.out.println(response.getResponse());
            }
            toServer.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return response;
    }

}
