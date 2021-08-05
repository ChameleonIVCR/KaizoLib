package com.chame.kaizolib.common.model;

import java.util.List;

//TODO make this an interface
public class Episode {
    private final String title;
    private final String date;
    private final String[] lowQualityTorrent;
    private final String[] hdQualityTorrent;
    private final String[] fullHdQualityTorrent;

    public Episode(String title, String date, List<String> qualities, List<String> magnets) throws IndexOutOfBoundsException{
        this.title = title;
        this.date = date;
        try {
            this.lowQualityTorrent = new String[]{qualities.get(0), magnets.get(0)};
            this.hdQualityTorrent = new String[]{qualities.get(1), magnets.get(1)};
            this.fullHdQualityTorrent = new String[]{qualities.get(2), magnets.get(2)};
        } catch(IndexOutOfBoundsException e){
            System.out.println("One of the downloads is missing, the episode title is: " + title);
            throw new IndexOutOfBoundsException();
        }
    }

    public String getTitle(){
        return title;
    }

    public String getDate(){
        return date;
    }

    public String[] getLQDownload(){
        return lowQualityTorrent;
    }

    public String[] getHQDownload(){
        return hdQualityTorrent;
    }

    public String[] getFQDownload(){
        return fullHdQualityTorrent;
    }
}
