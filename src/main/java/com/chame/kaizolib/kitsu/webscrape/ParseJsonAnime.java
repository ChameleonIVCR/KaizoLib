package com.chame.kaizolib.kitsu.webscrape;

import com.chame.kaizolib.common.model.Anime;

import org.jsoup.Jsoup;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

import java.util.Iterator;

public class ParseJsonAnime {

    public static Anime parse(String response) {

        String jsonInString = Jsoup.parse(response).body().text();

        JSONObject json = (JSONObject) JSONValue.parse(jsonInString);
        JSONArray data = (JSONArray) json.get("data");
        Iterator dataIterator = data.iterator();
        if (!dataIterator.hasNext()){
            return null;
        }

        JSONObject item = (JSONObject) dataIterator.next();

        int id = (int) item.get("id");

        JSONObject attributes = (JSONObject) item.get("attributes");
        JSONObject jsonTitles = (JSONObject) attributes.get("titles");
        JSONObject jsonImages = (JSONObject) attributes.get("posterImage");
        JSONObject jsonCoverImages = (JSONObject) attributes.get("coverImage");

        String title = (String) jsonTitles.get("en_jp");
        String synopsis = (String) attributes.get("synopsis");
        String imgLink = (String) jsonImages.get("small");
        String imgCoverLink = (String) jsonCoverImages.get("tiny");

        return new Anime(id, title, synopsis, imgLink, imgCoverLink);
    }
}
