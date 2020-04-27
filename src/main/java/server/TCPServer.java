package server;

import client.Client;
import client.TCPClient;
import constants.OperationName;
import model.ClientRequest;
import model.NodeDiscovery;
import nodediscovery.DiscoveryClient;
import nodediscovery.DiscoveryServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServer implements Server {

    private static final int NODE_DIRECTORY_PORT_NUMBER = 4410;

    protected Map<String, NodeDiscovery> nodeDiscoveryMap = new ConcurrentHashMap<>();

    private String nodeDirectoryAddress;
    private ServerSocket serverSocket;
    private int portNumber;
    private boolean isRunning;
    private boolean isCentralizedMembershipServer;

    public TCPServer(int portNumber, String nodeDirectoryAddress) {
        this.portNumber = portNumber;
        this.nodeDirectoryAddress = nodeDirectoryAddress;
        this.isCentralizedMembershipServer = portNumber == NODE_DIRECTORY_PORT_NUMBER;
        this.isRunning = true;
    }

    @Override
    public void startServer() {
        registerMembership();
        openServerSocket();
        while (this.isRunning) {
            Socket toClient;
            try {
                toClient = this.serverSocket.accept();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to connect to client", ex);
            }

            Thread thread = new Thread(
                    new TCPServerProcessor(toClient, this, nodeDirectoryAddress, isCentralizedMembershipServer));
            thread.start();
        }
        System.out.println("Server Shutdown.");
    }

    @Override
    public void stopServer() {
        this.isRunning = false;
        try {
            deregisterMembership();
            this.serverSocket.close();
            System.exit(1);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to terminate the server.", ex);
        }
    }

    private void registerMembership() {
        if (!isCentralizedMembershipServer) {
            if (nodeDirectoryAddress != null) {
                System.out.println("Registering IP/Port at node-directory");
                String hostAddress = getHostAddress();
                ClientRequest registerRequest = buildClientRequest(OperationName.PUT, hostAddress,
                        String.valueOf(portNumber));
                Client tcpClient = new TCPClient(registerRequest, nodeDirectoryAddress, NODE_DIRECTORY_PORT_NUMBER);
                tcpClient.execute();
            } else {
                // UDP Discovery Protocol
                Thread broadcastThread = new Thread(new DiscoveryClient(Instant::now, portNumber));
                broadcastThread.start();
                Thread accumulatorThread = new Thread(new DiscoveryServer(nodeDiscoveryMap));
                accumulatorThread.start();
            }
        }
    }

    private void deregisterMembership() {
        if (nodeDirectoryAddress != null) {
            String hostAddress = getHostAddress();
            ClientRequest deregisterRequest = buildClientRequest(OperationName.DEL, hostAddress,
                    String.valueOf(portNumber));
            Client tcpClient = new TCPClient(deregisterRequest, nodeDirectoryAddress, NODE_DIRECTORY_PORT_NUMBER);
            tcpClient.execute();
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
            System.out.println("TCP server is up and running...");
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Failed to open port %s", this.portNumber));
        }
    }

    private String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Error while retrieving the host address. ", ex);
        }
    }

    private ClientRequest buildClientRequest(String operationName, String key, String val) {
        return new ClientRequest(operationName, key, val);
    }

}
