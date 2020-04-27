package parser;

import model.ClientRequest;

import static constants.OperationName.*;

public class RequestParser {

    public static ClientRequest parseInput(String[] args) {
//        RequestValidator.validateRequest(args);
        String opName = getOperationName(args);

        switch (opName) {
            case PUT:
                return buildClientRequest(opName, getKey(args), getValue(args));
            case GET:
            case DEL:
                return buildClientRequest(opName, getKey(args), null);
            default:
                return buildClientRequest(opName, null, null);
        }
    }

    private static String getOperationName(String[] args) {
        return isRMIClientCall(args) ? args[2] : args[3];
    }

    private static String getKey(String[] args) {
        return isRMIClientCall(args) ? args[3] : args[4];
    }

    private static String getValue(String[] args) {
        return isRMIClientCall(args) ? args[4] : args[5];
    }

    private static ClientRequest buildClientRequest(String operationName, String key, String val) {
        return new ClientRequest(operationName, key, val);
    }

    private static boolean isRMIClientCall(String[] args) {
        return args[0].equals("rmic");
    }
}
