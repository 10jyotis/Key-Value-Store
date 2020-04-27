package nodediscovery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.util.concurrent.Uninterruptibles;

public class DiscoveryClient implements Runnable {

    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final int DISCOVERY_SERVER_PORT = 4410;

    private Supplier<Instant> timeSupplier;
    private int discoveryClientPort;

    public DiscoveryClient(Supplier<Instant> timeSupplier, int discoveryClientPort) {
        this.timeSupplier = timeSupplier;
        this.discoveryClientPort = discoveryClientPort;
    }

    @Override
    public void run() {

        DatagramSocket toServer;
        try {
            toServer = new DatagramSocket(discoveryClientPort);
            toServer.setBroadcast(true);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create broadcast datagram socket", ex);
        }
        System.out.println("client: connected!");

        while (true) {
            try {
                byte[] sendBytes = convertToByteArray(timeSupplier.get());
                InetAddress inetAddress = InetAddress.getByName(BROADCAST_ADDRESS);
                DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, inetAddress,
                        DISCOVERY_SERVER_PORT);
                toServer.send(sendPacket);
                Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private byte[] convertToByteArray(Instant timestamp) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(timestamp);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while converting Instant timestamp to byteArray", ex);
        }
    }

}
