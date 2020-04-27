package nodediscovery;

import model.NodeDiscovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.Map;

public class DiscoveryServer implements Runnable {

    private static final int DISCOVERY_SERVER_PORT = 4410;

    protected Map<String, NodeDiscovery> nodeDiscoveryMap;
    private DatagramSocket socket;
    private boolean isRunning;

    public DiscoveryServer(Map<String, NodeDiscovery> nodeDiscoveryMap) {
        this.nodeDiscoveryMap = nodeDiscoveryMap;
        this.isRunning = true;
    }

    @Override
    public void run() {
        startDiscovery();
    }

    private void startDiscovery() {
        openServerSocket();

        byte[] receiveBytes = new byte[2048];
        while (this.isRunning) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            try {
                this.socket.receive(receivePacket);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to receive the client packet", ex);
            }

            Thread thread = new Thread(new DiscoveryServerThread(this, socket, receivePacket, Instant::now));
            thread.start();
        }
        System.out.println("Server Shutdown.");
    }

    private void stopDiscovery() {
        this.isRunning = false;
        this.socket.close();
    }

    private void openServerSocket() {
        try {
            this.socket = new DatagramSocket(DISCOVERY_SERVER_PORT);
            System.out.println("UDP server is up and running...");
        } catch (IOException ex) {
            throw new RuntimeException(
                    String.format("Failed to open datagram socket on port %s", DISCOVERY_SERVER_PORT));
        }
    }

}
