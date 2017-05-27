package com.lexdraven.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.logging.Level;

public class URLChecker {
    private int statusCode;
    private RequestConfig config;
    private CloseableHttpClient client;
    private HttpResponse response;
    private HttpHead request;

    public URLChecker() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(Level.OFF);
        config= RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(8000).setConnectionRequestTimeout(3000).setRedirectsEnabled(true).build();
    }

    public boolean checkLinks(String URL){
        try {
            client= HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionReuseStrategy(new NoConnectionReuseStrategy()).disableAutomaticRetries().build();
            request= new HttpHead(URL);
            request.addHeader("User-Agent","Mozilla/5.0");
            response = client.execute(request);
            client.close();
        } catch (Exception e) {
            System.err.println(URL + " error " + e.getLocalizedMessage());
            return false;
        }
        statusCode = response.getStatusLine().getStatusCode();
        return  statusCode==200;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isFile (String url){
        if ((url.lastIndexOf(".")==url.length()-4) & (!url.endsWith("/"))) {
            return true;
        }
        if ((url.endsWith(".docx")) | (url.endsWith(".jpeg"))) {
            return true;
        }
        return false;
    }
}
