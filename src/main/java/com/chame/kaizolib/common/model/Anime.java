package com.chame.kaizolib.common.model;

import java.util.List;

//TODO make this an interface
public class Anime {
    private final int id;
    private final String title;
    private final String synopsis;
    private final String imgLink;
    private final String coverImgLink;
    private List<Episode> episodes;

    public Anime(int id, String title, String synopsis, String imgLink, String coverImgLink){
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.imgLink = imgLink;
        this.coverImgLink = coverImgLink;
    }

    public void setEpisodes(List<Episode> episodes){
        this.episodes = episodes;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public String getImgLink(){
        return imgLink;
    }

    public String getCoverImgLink(){
        return coverImgLink;
    }

    public List<Episode> getEpisodes(){
        return episodes;
    }

    public Episode getEpisode(int number){
        try {
            return episodes.get(number + 1);
        } catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
}
