package nodediscovery;

import model.NodeDiscovery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;

public class DiscoveryServerThread implements Runnable {

    private static final long TEN_SECONDS = 10;

    private DiscoveryServer discoveryServer;
    private DatagramSocket toClient;
    private DatagramPacket receivePacket;
    private Supplier<Instant> timeSupplier;

    public DiscoveryServerThread(DiscoveryServer discoveryServer, DatagramSocket toClient,
            DatagramPacket receivePacket, Supplier<Instant> timeSupplier) {
        super();
        this.discoveryServer = discoveryServer;
        this.toClient = toClient;
        this.receivePacket = receivePacket;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public void run() {
        Instant timestamp = getRequestFromBytes(receivePacket.getData());
        System.out.println("Discovery timestamp: " + timestamp);

        String participantNodeAddress = receivePacket.getAddress().getHostAddress();
        int participantNodePort = receivePacket.getPort();
        Map<String, NodeDiscovery> discoveryMap = discoveryServer.nodeDiscoveryMap;
        discoveryMap.put(participantNodeAddress, new NodeDiscovery(participantNodePort, timestamp));
        for (Map.Entry<String, NodeDiscovery> entry : discoveryMap.entrySet()) {
            if (entry.getValue().getLastUpdateTime().plusSeconds(TEN_SECONDS).getEpochSecond() < timeSupplier.get()
                    .getEpochSecond()) {
                discoveryMap.remove(entry.getKey());
            }
        }
        System.out.println("Request completed.");
    }

    private Instant getRequestFromBytes(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            return (Instant) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Error while retrieving ClientRequest from receive packet", ex);
        }
    }

}
