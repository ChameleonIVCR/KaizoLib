package com.chame.kaizolib.nibl;

import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.common.network.UserHttpClient;
import com.chame.kaizolib.common.util.ResponseToString;

import com.chame.kaizolib.nibl.webscrape.ParsePage;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Nibl {
    private static final Logger logger = LogManager.getLogger(Nibl.class);
    private final int timeoutInSeconds = 20;
    private final String header = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/92.0.4515.131 Mobile Safari/537.36";
    private final RequestConfig timeoutRequest = RequestConfig.custom()
                                                .setConnectTimeout(Timeout.ofSeconds(timeoutInSeconds))
                                                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
                                                .setResponseTimeout(Timeout.ofSeconds(timeoutInSeconds))
                                                .build();

    private final UserHttpClient client;

    public Nibl(UserHttpClient client) {
        this.client = client;
    }

    //UserHttpClient-less operation
    public Nibl() {
        client = new UserHttpClient();
    }

    public UserHttpClient getUserHttpClient() {
        return client;
    }

    public List<Result> getLatest() {
        URI queryUrl = null;
        URIBuilder builder = new URIBuilder()
                            .setScheme("https")
                            .setHost("nibl.co.uk");

        try {
            queryUrl = builder.build();
        } catch(URISyntaxException u) {
            logger.fatal("The URI for Kitsu API connection couldn't be created.");
            throw new RuntimeException(u);
        }

        HttpGet get = new HttpGet(queryUrl);
        get.setConfig(timeoutRequest);
        get.addHeader("User-Agent", header);

        CloseableHttpResponse response = client.executeRequest(get);

        final String responseContent = ResponseToString.read(response);
        if (responseContent == null){
            return null;
        }

        int statusCode = response.getCode();
        switch(statusCode) {
            case 304:
            case 200:
                return ParsePage.parse(responseContent);
            default:
                logger.warn("Couldn't connect to Nibl, or the request was denied when fetching latest animes.");
                return null;
        }
    }

    public List<Result> search(String search) {
        URI queryUrl = null;
        URIBuilder builder = new URIBuilder()
                .setScheme("https")
                .setHost("nibl.co.uk")
                .setPath("/search")
                .setParameter("query", search);

        try {
            queryUrl = builder.build();
        } catch(URISyntaxException u) {
            logger.fatal("The URI for Kitsu API connection couldn't be created.");
            throw new RuntimeException(u);
        }

        HttpGet get = new HttpGet(queryUrl);
        get.setConfig(timeoutRequest);
        get.addHeader("User-Agent", header);

        CloseableHttpResponse response = client.executeRequest(get);

        final String responseContent = ResponseToString.read(response);
        if (responseContent == null){
            return null;
        }

        int statusCode = response.getCode();
        switch(statusCode) {
            case 304:
            case 200:
                return ParsePage.parse(responseContent);
            default:
                logger.warn("Couldn't connect to Nibl, or the request was denied when fetching latest animes.");
                return null;
        }
    }
}
