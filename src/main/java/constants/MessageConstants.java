package constants;

public class MessageConstants {
    public static final String CLIENT_USAGE = "uc/tc <address> <port> put <key> <msg> UDP/TCP CLIENT: Put an object " +
            "into store.\n"
            + "uc/tc <address> <port> get <key> UDP/TCP CLIENT: Get an object from store by key.\n"
            + "uc/tc <address> <port> del <key> UDP/TCP CLIENT: Delete an object from store by key.\n"
            + "uc/tc <address> <port> store UDP/TCP CLIENT: Display object store.\n"
            + "uc/tc <address> <port> exit UDP/TCP CLIENT: Shutdown server.\n"
            + "rmic <address> put <key> <msg>  RMI CLIENT: Put an object into store.\n"
            + "rmic <address> get <key>  RMI CLIENT: Get an object from store by key.\n"
            + "rmic <address> del <key>  RMI CLIENT: Delete an object from store by key.\n"
            + "rmic <address> store  RMI CLIENT: Display object store.\n"
            + "rmic <address> exit  RMI CLIENT: Shutdown server\n";

    public static final String SERVER_USAGE = "us/ts <port> UDP/TCP/: run server on <port>.\n"
            + "tus <tcpport> <udpport> TCP-and-UDP SERVER: run servers on <tcpport> and <udpport> sharing same key-value store.\n"
            + "rmis  RMI Server";

    public static final String HELP_MESSAGE = "\nUsage:" +
            "\nClient:\n" + CLIENT_USAGE +
            "\nServer:\n" + SERVER_USAGE;
}
