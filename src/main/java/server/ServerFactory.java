package server;

public class ServerFactory {

    private static final String TCP_SERVER = "ts";
    private static final String UDP_SERVER = "us";
    private static final String RMI_SERVER = "rmis";

    public Server getServer(String serverType, int portNumber, String nodeDirectoryAddress) {

        if (serverType.equals(TCP_SERVER)) {
            return new TCPServer(portNumber, nodeDirectoryAddress);
        } else if (serverType.equals(UDP_SERVER)) {
            return new UDPServer(portNumber);
        } else if (serverType.equals(RMI_SERVER)) {
            return new RMIServer();
        } else {
            throw new IllegalArgumentException(String.format("Unknown server type provided: '%s'", serverType));
        }
    }
}
