package model;

import java.time.Instant;

public class NodeDiscovery {

    private int port;
    private Instant lastUpdateTime;

    public NodeDiscovery(int port, Instant lastUpdateTime) {
        this.port = port;
        this.lastUpdateTime = lastUpdateTime;
    }

    public NodeDiscovery() {

    }

    public int getPort() {
        return port;
    }

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLastUpdateTime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
