package model;

import java.io.Serializable;
import java.time.Instant;

public class ClientRequest implements Serializable {
    private String operationName;
    private String key;
    private String value;
    private Instant instant;

    public ClientRequest(String operationName, String key, String value) {
        // Add preconditions
        this.operationName = operationName;
        this.key = key;
        this.value = value;
        this.instant = Instant.now();
    }

    public ClientRequest() {

    }

    public String getOperationName() {
        return this.operationName;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
