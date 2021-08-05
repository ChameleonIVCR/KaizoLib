package com.chame.kaizolib.irc.model;

public class DCC {
    private final String filename;
    private final String ip;
    private final int port;
    private final long sizeBits;

    public DCC(String filename, String ip, int port, long sizeBits) {
        this.filename = filename;
        this.ip = ip;
        this.port = port;
        this.sizeBits = sizeBits;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public long getSizeBits() {
        return sizeBits;
    }

    public String getFilename() {
        return filename;
    }
}
