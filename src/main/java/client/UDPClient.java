package client;

import model.ClientRequest;
import model.ServerResponse;

import static constants.OperationName.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.common.collect.Lists;

public class UDPClient implements Client {

    private ClientRequest request;
    private String serverAddress;
    private int serverPort;

    public UDPClient(ClientRequest request, String serverAddress, int serverPort) {
        this.request = request;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public ServerResponse execute() {

        DatagramSocket toServer;
        try {
            toServer = new DatagramSocket();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create client datagram socket", ex);
        }
        System.out.println("client: connected!");

        String serverResponse = null;
        try {
            byte[] sendBytes = convertToByteArray(request);
            InetAddress inetAddress = InetAddress.getByName(serverAddress);
            DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, inetAddress, serverPort);
            toServer.send(sendPacket);

            String operationName = request.getOperationName();
            if (!operationName.equals(EXIT)) {
                byte[] receiveBytes = new byte[65000];
                DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                toServer.receive(receivePacket);
                serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(serverResponse);
            }
            toServer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ServerResponse(serverResponse, Lists.newArrayList());
    }

    private byte[] convertToByteArray(ClientRequest request) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(request);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while converting client request to byteArray", ex);
        }
    }

}
