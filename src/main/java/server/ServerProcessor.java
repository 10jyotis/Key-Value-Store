package server;

import model.ClientRequest;
import model.Entry;
import model.Record;
import repo.KeyValueStore;

import static constants.OperationName.*;

import java.util.Optional;

public abstract class ServerProcessor {

    protected KeyValueStore keyValueStore;

    public ServerProcessor() {
        this.keyValueStore = KeyValueStore.getInstance();
    }

    protected String process(ClientRequest request) {
        String operationName = request.getOperationName();
        String key = request.getKey();
        String val = request.getValue();

        String response;
        switch (operationName) {
            case PUT:
                response = put(key, val);
                break;
            case GET:
                response = get(key);
                break;
            case DEL:
                response = delete(key);
                break;
            case STORE:
                response = store();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operation specified: %s", operationName));
        }
        return response;
    }

    private String put(String key, String val) {
        Record unlockedRecord = new Record(val, false);
        this.keyValueStore.put(key, unlockedRecord);
        return String.format("server response:put key=%s", key);
    }

    private String get(String key) {
        Optional<Record> optionalRecord = this.keyValueStore.get(key);
        String val = optionalRecord.isPresent() ? optionalRecord.get().getVal() : "Key not found!";
        return String.format("server response:get key=%s get val=%s", key, val);
    }

    private String delete(String key) {
        this.keyValueStore.delete(key);
        return String.format("server response:delete key=%s", key);
    }

    private String store() {
        StringBuilder messageBuilder = new StringBuilder("server response:");
        this.keyValueStore.store()
                .forEach(entry -> buildResponse(entry, messageBuilder));

        return messageBuilder.toString();
    }

    private void buildResponse(Entry entry, StringBuilder messageBuilder) {
        messageBuilder.append("\n");
        messageBuilder.append(String.format("key:%s:value:%s:", entry.getKey(), entry.getRecord().getVal()));
    }
}
