package parser;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import static constants.OperationName.*;

public class RequestValidator {

    private static final Set<String> SUPPORTED_OPERATIONS = ImmutableSet.of(PUT, GET, DEL, STORE, EXIT);

    public static void validateRequest(String[] args) {

        if (args.length < 4) {
            // throw generic message
            throw new IllegalArgumentException("Invalid request parameters. Valid usages are:\n");
        } else if (!SUPPORTED_OPERATIONS.contains(args[3])) {
            // throw generic message
            throw new IllegalArgumentException("Operation name is missing or not supported. Valid usages are:\n");
        }

    }

    public static void validateRequest(String[] array, int size) {
        if (array.length < size) {
            // throw generic message
            throw new IllegalArgumentException("One or more parameters missing for the specified operation. Valid " +
                    "usages are:\n");
        }
    }
}
