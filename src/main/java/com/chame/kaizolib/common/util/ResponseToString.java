package com.chame.kaizolib.common.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseToString {
    private static final Logger logger = LogManager.getLogger(ResponseToString.class);

    public static String read(CloseableHttpResponse response){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            response.close();
            return out.toString();
        } catch (IOException e) {
            logger.warn("The CloseableHttpResponse went through to the host and back,"
                        +"but it couldn't be read correctly, or didn't close. ", e);
            return null;
        }

    }
}
