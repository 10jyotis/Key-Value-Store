package client;

import model.ClientRequest;

public class ClientFactory {

    private static final String TCP_CLIENT = "tc";
    private static final String UDP_CLIENT = "uc";
    private static final String RMI_CLIENT = "rmic";

    public Client getClient(String clientType, String address, int portNumber, ClientRequest request) {

        if (clientType.equals(TCP_CLIENT)) {
            return new TCPClient(request, address, portNumber);
        } else if (clientType.equals(UDP_CLIENT)) {
            return new UDPClient(request, address, portNumber);
        } else if (clientType.equals(RMI_CLIENT)) {
            return new RMIClient(request, address);
        } else {
            throw new IllegalArgumentException(String.format("Unknown client type provided: '%s'", clientType));
        }
    }
}
