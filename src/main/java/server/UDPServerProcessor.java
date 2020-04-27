package server;

import model.ClientRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static constants.OperationName.EXIT;

public class UDPServerProcessor extends ServerProcessor implements Runnable {

    private DatagramSocket toClient;
    private DatagramPacket receivePacket;

    public UDPServerProcessor(DatagramSocket toClient, DatagramPacket receivePacket) {
        super();
        this.toClient = toClient;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        ClientRequest clientRequest = getRequestFromBytes(receivePacket.getData());
        System.out.println("Received operation name: " + clientRequest.getOperationName());

        if (clientRequest.getOperationName().equals(EXIT)) {
            toClient.close();
            System.out.println("Connection closed..");
            return;
        }

        String response = process(clientRequest);
        byte[] sendBytes = response.getBytes();
        InetAddress inetAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, inetAddress, port);

        try {
            toClient.send(sendPacket);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send response to the Client.", ex);
        }
        System.out.println("Request completed.");
    }

    private ClientRequest getRequestFromBytes(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            return (ClientRequest) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Error while retrieving ClientRequest from receive packet", ex);
        }
    }
}
