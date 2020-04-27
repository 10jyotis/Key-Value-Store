package model;

import java.io.Serializable;

public class Record implements Serializable {

    private String val;
    private boolean isLocked;

    public Record(String val, boolean isLocked) {
        this.val = val;
        this.isLocked = isLocked;
    }

    public Record() {

    }

    public String getVal() {
        return this.val;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

}
