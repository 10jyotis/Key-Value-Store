package model;

import java.io.Serializable;
import java.util.List;

public class ServerResponse implements Serializable {

    private String response;
    private List<Entry> entries;

    public ServerResponse(String response, List<Entry> entries) {
        this.response = response;
        this.entries = entries;
    }

    public ServerResponse() {

    }

    public String getResponse() {
        return this.response;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
