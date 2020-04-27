package genericnode;

import client.Client;
import client.ClientFactory;
import constants.MessageConstants;
import model.ClientRequest;
import parser.RequestParser;
import server.Server;
import server.ServerFactory;

public class GenericNode {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(MessageConstants.HELP_MESSAGE);
            return;
        }

        if (args[0].equals("tc")) {
            ClientRequest request = RequestParser.parseInput(args);
            initiateClient(request, args[1], Integer.parseInt(args[2]));
        } else if (args[0].equals("ts")) {
            initiateServer(args);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported client/server type specified: ' %s'", args[0]));
        }
    }

    private static void initiateServer(String[] args) {
        ServerFactory serverFactory = new ServerFactory();
        String serverPort = args[1];
        String nodeDirectoryAddress = args.length == 3 ? args[2] : null;

        Server server = serverFactory.getServer("ts", Integer.parseInt(serverPort), nodeDirectoryAddress);
        server.startServer();
    }

    private static void initiateClient(ClientRequest request, String serverAddress, int serverPort) {
        ClientFactory clientFactory = new ClientFactory();
        Client client = clientFactory.getClient("tc", serverAddress, serverPort, request);
        client.execute();
    }

}
