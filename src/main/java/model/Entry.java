package model;

import java.io.Serializable;

public class Entry implements Serializable {

    private String key;
    private Record record;

    public Entry(String key, Record record) {
        this.key = key;
        this.record = record;
    }

    public Entry() {

    }

    public String getKey() {
        return this.key;
    }

    public Record getRecord() {
        return this.record;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
