package server;

import client.Client;
import client.TCPClient;
import constants.OperationName;
import model.*;

import static constants.OperationName.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;

public class TCPServerProcessor extends ServerProcessor implements Runnable {

    private static final int NODE_DIRECTORY_PORT_NUMBER = 4410;

    private Socket toClient;
    private TCPServer tcpServer;
    private String nodeDirectoryAddress;
    private boolean isCentralizedMembershipServer;

    public TCPServerProcessor(Socket toClient, TCPServer tcpServer, String nodeDirectoryAddress,
            boolean isCentralizedMembershipServer) {
        super();
        this.toClient = toClient;
        this.tcpServer = tcpServer;
        this.nodeDirectoryAddress = nodeDirectoryAddress;
        this.isCentralizedMembershipServer = isCentralizedMembershipServer;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(toClient.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(toClient.getInputStream());

            ClientRequest clientRequest = (ClientRequest) inputStream.readObject();
            System.out.println("Received operation name: " + clientRequest.getOperationName());

            if (clientRequest.getOperationName().equals(EXIT)) {
                System.out.println("Connection closed..");
                toClient.close();
                tcpServer.stopServer();
                return;
            }
            String response;
            List<Entry> entries = Lists.newArrayList();
            String operationName = clientRequest.getOperationName();
            if (INTERNAL_OPERATIONS.contains(operationName)) {
                response = invokeParticipant(clientRequest);
            } else if (isWriteOperation(operationName) && !isCentralizedMembershipServer) {
                if (nodeDirectoryAddress != null) {
                    response = invokeLeader(clientRequest, invokeCentralizedMembershipServer());
                } else {
                    response = invokeLeader(clientRequest, convertDiscoveryMapToEntryList(tcpServer.nodeDiscoveryMap));
                }
            } else {
                response = process(clientRequest);
            }
            if (operationName.equals(STORE)) {
                entries.addAll(this.keyValueStore.store());
            }
            ServerResponse serverResponse = new ServerResponse(response, entries);
            outputStream.writeObject(serverResponse);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            System.out.println("Request completed.");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private List<Entry> convertDiscoveryMapToEntryList(Map<String, NodeDiscovery> nodeDiscoveryMap) {
        List<Entry> entries = Lists.newArrayList();
        for (Map.Entry<String, NodeDiscovery> entry : nodeDiscoveryMap.entrySet()) {
            entries.add(new Entry(entry.getKey(), new Record(String.valueOf(entry.getValue().getPort()), false)));
        }
        return entries;
    }

    private List<Entry> invokeCentralizedMembershipServer() {
        // Fetch latest list of participants
        ClientRequest storeRequest = new ClientRequest(STORE, null, null);
        Client tcpClient = new TCPClient(storeRequest, nodeDirectoryAddress, NODE_DIRECTORY_PORT_NUMBER);
        return tcpClient.execute().getEntries();
    }

    private String invokeLeader(ClientRequest request, List<Entry> entries) {
        TCPClient tcpClient;
        // phase-1
        ClientRequest initialRequest;
        ServerResponse initialResponse;
        List<Entry> abortNodes = Lists.newArrayList();
        String initialOperation = request.getOperationName().equals(PUT) ? D_PUT1 : D_DEL1;
        for (Entry entry : entries) {
            initialRequest = new ClientRequest(initialOperation, request.getKey(), request.getValue());
            tcpClient = new TCPClient(initialRequest, entry.getKey(), Integer.parseInt(entry.getRecord().getVal()));
            initialResponse = tcpClient.execute();
            if (initialResponse.getResponse().equals("ABORT")) {
                abortNodes.add(entry);
            }
        }

        // phase-1 trigger abort
        if (!abortNodes.isEmpty()) {
            List<Entry> nonAbortedNodes = Lists.newArrayList(entries);
            nonAbortedNodes.removeAll(abortNodes);
            ClientRequest abortRequest;
            String abortOperationName = request.getOperationName().equals(PUT) ? D_PUT_ABORT : D_DEL_ABORT;
            for (Entry entry : nonAbortedNodes) {
                abortRequest = new ClientRequest(abortOperationName, request.getKey(), request.getValue());
                tcpClient = new TCPClient(abortRequest, entry.getKey(), Integer.parseInt(entry.getRecord().getVal()));
                tcpClient.execute();
            }
            return "ABORTED";
        }

        //Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        // phase-2
        ClientRequest commitRequest;
        ServerResponse commitResponse = null;
        String commitOperationName = request.getOperationName().equals(PUT) ? D_PUT2 : D_DEL2;
        for (Entry entry : entries) {
            commitRequest = new ClientRequest(commitOperationName, request.getKey(), request.getValue());
            tcpClient = new TCPClient(commitRequest, entry.getKey(), Integer.parseInt(entry.getRecord().getVal()));
            commitResponse = tcpClient.execute();
        }
        return commitResponse != null ? commitResponse.getResponse() : null;
    }

    private String invokeParticipant(ClientRequest request) {
        String operationName = request.getOperationName();
        if (PHASE_ONE_OPERATIONS.contains(operationName)) {
            return triggerPhaseOne(request);
        } else if (PHASE_TWO_OPERATIONS.contains(operationName)) {
            return triggerPhaseTwo(request);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown internal operation name specified: '%s'", request.getOperationName()));
        }
    }

    private String triggerPhaseOne(ClientRequest request) {
        Optional<Record> optionalRecord = this.keyValueStore.get(request.getKey());
        String val = optionalRecord.map(Record::getVal).orElse(null);

        if (isAbortOperation(request.getOperationName())) {
            Record unlockedRecord = new Record(val, false);
            this.keyValueStore.put(request.getKey(), unlockedRecord);
            return "ABORTED";
        }
        boolean isLocked = optionalRecord.map(Record::isLocked).orElse(false);
        if (isLocked) {
            // abort
            return "ABORT";
        }
        Record lockedRecord = new Record(val, true);
        this.keyValueStore.put(request.getKey(), lockedRecord);
        return "READY";
    }

    private String triggerPhaseTwo(ClientRequest request) {
        if (request.getOperationName().equals(OperationName.D_DEL2)) {
            ClientRequest deleteRequest = new ClientRequest(OperationName.DEL, request.getKey(), null);
            return process(deleteRequest);
        }
        ClientRequest putRequest = new ClientRequest(OperationName.PUT, request.getKey(), request.getValue());
        return process(putRequest);
    }

    private boolean isAbortOperation(String operationName) {
        return operationName.equals(D_PUT_ABORT) || operationName.equals(D_DEL_ABORT);
    }

    private boolean isWriteOperation(String operationName) {
        return operationName.equals(PUT) || operationName.equals(DEL);
    }

}
