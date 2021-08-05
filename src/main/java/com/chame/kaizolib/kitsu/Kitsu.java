package com.chame.kaizolib.kitsu;

import com.chame.kaizolib.common.model.Anime;
import com.chame.kaizolib.common.network.UserHttpClient;
import com.chame.kaizolib.common.util.ResponseToString;
import com.chame.kaizolib.kitsu.webscrape.ParseJsonAnime;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;


public class Kitsu {
    private static final Logger logger = LogManager.getLogger(Kitsu.class);
    private final UserHttpClient client;
    private final int timeoutInSeconds = 10;
    private final RequestConfig timeoutRequest = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(timeoutInSeconds))
            .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
            .setResponseTimeout(Timeout.ofSeconds(timeoutInSeconds))
            .build();

    public Kitsu(UserHttpClient client) {
        this.client = client;
    }

    public Kitsu() {
        client = new UserHttpClient();
    }

    public Anime getAnimeByTitle(String animeTitle) {
        animeTitle = animeTitle.replaceAll("\\[.*?\\]","")
                               .replace("_", " ")
                               .trim();

        URI queryUrl = null;
        try {
            queryUrl = new URIBuilder()
                    .setScheme("https")
                    .setHost("kitsu.io")
                    .setPath("/api/edge/anime")
                    .setParameter("page[limit]", "1")
                    .setParameter("page[offset]", "0")
                    .setParameter("filter[text]", animeTitle)
                    .build();
        } catch(URISyntaxException u) {
            logger.fatal("The URI for Kitsu API connection couldn't be created.");
            throw new RuntimeException(u);
        }

        HttpGet get = new HttpGet(queryUrl);
        get.setConfig(this.timeoutRequest);
        get.addHeader("Accept","application/vnd.api+json");
        get.addHeader("Content-Type","application/vnd.api+json");
        CloseableHttpResponse response = client.executeRequest(get);

        final String responseContent = ResponseToString.read(response);
        if (responseContent == null) {
            return new Anime(-1, animeTitle, null, null, null);
        }

        int statusCode = response.getCode();
        switch(statusCode) {
            case 304:
            case 200:
                Anime animeResult = ParseJsonAnime.parse(responseContent);
                if (animeResult == null){
                    return new Anime(-1, animeTitle, null, null, null);
                } else {
                    return animeResult;
                }
            default:
                logger.warn("Couldn't connect to Kitsu, or the request was denied when fetching Anime by title.");
                return new Anime(-1, animeTitle, null, null, null);
        }
    }

    public Anime getAnimeById(int id) {
        URI queryUrl = null;
        try {
            queryUrl = new URIBuilder()
                    .setScheme("https")
                    .setHost("kitsu.io")
                    .setPath("/api/edge/anime")
                    .setParameter("filter[id]", Integer.toString(id))
                    .build();
        } catch(URISyntaxException u) {
            logger.fatal("The URI for Kitsu API connection couldn't be created. ", u);
        }

        assert queryUrl != null;
        HttpGet get = new HttpGet(queryUrl);
        get.setConfig(this.timeoutRequest);
        get.addHeader("Accept","application/vnd.api+json");
        get.addHeader("Content-Type","application/vnd.api+json");
        CloseableHttpResponse response = client.executeRequest(get);

        final String responseContent = ResponseToString.read(response);
        if (responseContent == null){
            return new Anime(id, "Unknown", null, null, null);
        }

        int statusCode = response.getCode();
        switch(statusCode) {
            case 304:
            case 200:
                return ParseJsonAnime.parse(responseContent);
            default:
                logger.warn("Couldn't connect to Kitsu, or the request was denied when fetching Anime by id.");
                return new Anime(id, "Unknown", null, null, null);
        }
    }
}
