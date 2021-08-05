package com.chame.kaizolib.common.network;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class UserHttpClient {
    CloseableHttpClient httpClient;
    private static final Logger logger = LogManager.getLogger(UserHttpClient.class);

    public UserHttpClient(){
        httpClient = HttpClients.createDefault();
    }

    public UserHttpClient(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    public CloseableHttpResponse executeRequest(HttpUriRequest request){
        try {
            return httpClient.execute(request);
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException io) {
            logger.warn("The CloseableHttpClient couldn't close. ", io);
        }
    }

}
