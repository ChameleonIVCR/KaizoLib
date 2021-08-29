package com.chame.kaizolib.common.model;

public class Result {
    private final String bot;
    private final String pack;
    private final String size;
    private final String filename;
    private final String quality;
    private final String format;
    private final String command;
    private final String rawFilename;

    public Result (String bot, String pack, String size, String quality, String format, String filename, String rawFilename) {
        this.bot = bot;
        this.pack = pack;
        this.size = size;
        this.quality = quality;
        this.format = format;
        this.filename = filename;
        this.rawFilename = rawFilename;
        this.command = bot + " :xdcc send #" + pack;
    }


    public String getBot() {
        return bot;
    }

    public String getPack() {
        return pack;
    }

    public String getSize() {
        return size;
    }

    public String getFilename() {
        return filename;
    }

    public String getQuality() {
        return quality;
    }

    public String getCommand() {
        return command;
    }

    public String getFormat() {
        return format;
    }

    public String getRawFilename() {
        return rawFilename;
    }
}
