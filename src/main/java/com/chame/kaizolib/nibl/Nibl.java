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

import java.io.IOException;
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

    private NiblSuccessListener niblSuccessListener;
    private NiblFailureListener niblFailureListener;

    //Use this whenever possible.
    public Nibl(UserHttpClient client) {
        this.client = client;
    }

    //UserHttpClient-less operation
    public Nibl() {
        client = new UserHttpClient();
    }

    //Convenience class, but don't rely on this.
    public UserHttpClient getUserHttpClient() {
        return client;
    }

    public void setNiblFailureListener(NiblFailureListener niblFailureListener) {
        this.niblFailureListener = niblFailureListener;
    }

    public void setNiblSuccessListener(NiblSuccessListener niblSuccessListener) {
        this.niblSuccessListener = niblSuccessListener;
    }

    public void getLatest() {
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

        CloseableHttpResponse response = null;
        try {
            response = client.executeRequest(get);
        } catch (IOException e) {
            if (niblFailureListener !=null) niblFailureListener.onFailure(FailureCode.NoConnection);
            return;
        }

        int statusCode = response.getCode();
        final String responseContent = ResponseToString.read(response);

        //.contains will block for a big search.
        if (responseContent == null || responseContent.contains("No results")){
            if (niblSuccessListener != null) niblSuccessListener.onNoResults();
            return;
        }

        switch(statusCode) {
            case 304:
            case 200:
                if (niblSuccessListener != null) niblSuccessListener.onSuccess(ParsePage.parse(responseContent));
            default:
                logger.warn("Couldn't connect to Nibl, or the request was denied when fetching latest animes.");
                if (niblSuccessListener != null) niblSuccessListener.onNoResults();
        }
    }

    public void search(String search) {
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

        CloseableHttpResponse response = null;
        try {
            response = client.executeRequest(get);
        } catch (IOException e) {
            if (niblFailureListener !=null) niblFailureListener.onFailure(FailureCode.NoConnection);
            return;
        }

        int statusCode = response.getCode();
        final String responseContent = ResponseToString.read(response);

        if (responseContent == null || responseContent.contains("No results")){
            if (niblSuccessListener != null) niblSuccessListener.onNoResults();
            return;
        }

        switch(statusCode) {
            case 304:
            case 200:
                if (niblSuccessListener != null) niblSuccessListener.onSuccess(ParsePage.parse(responseContent));
            default:
                logger.warn("Couldn't connect to Nibl, or the request was denied when fetching latest animes.");
                if (niblSuccessListener != null) niblSuccessListener.onNoResults();
        }
    }

    public enum FailureCode{
        PeerInternalError,
        NoConnection
    }

    public interface NiblSuccessListener{
        void onSuccess(List<Result> result);

        void onNoResults();
    }

    public interface NiblFailureListener{
        void onFailure(FailureCode f);
    }
}
