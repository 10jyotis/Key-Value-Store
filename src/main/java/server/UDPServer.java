package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer implements Server {

    //private static final KeyValueStore keyValueStore = KeyValueStore.getInstance();
    private DatagramSocket socket;
    private int portNumber;
    private boolean isRunning;

    public UDPServer(int portNumber) {
        this.portNumber = portNumber;
        this.isRunning = true;
    }

    @Override
    public void startServer() {
        openServerSocket();

        byte[] receiveBytes = new byte[2048];
        while (this.isRunning) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            try {
                this.socket.receive(receivePacket);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to receive the client packet", ex);
            }

            Thread thread = new Thread(new UDPServerProcessor(socket, receivePacket));
            thread.start();
        }
        System.out.println("Server Shutdown.");
    }

    @Override
    public void stopServer() {
        this.isRunning = false;
        this.socket.close();
    }

    private void openServerSocket() {
        try {
            this.socket = new DatagramSocket(this.portNumber);
            System.out.println("UDP server is up and running...");
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Failed to open datagram socket on port %s", this.portNumber));
        }
    }
}
