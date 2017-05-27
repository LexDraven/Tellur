package com.lexdraven.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class HttpRequestsSender {
    private HttpClient client;
    private int timeOut = 60;
    private final String USER_AGENT = "Mozilla/5.0";
    private Map <String, String> headers;
    private LinkedList<String> content;
    private RequestConfig config;

    public HttpRequestsSender() {
        init();
    }

    public HttpRequestsSender(int timeOut) {
        this.timeOut = timeOut;
        init();
    }

    private void init(){
        config= RequestConfig.custom().setSocketTimeout(timeOut*1000).build();
        content = new LinkedList<>();
        headers = new HashMap<>();
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        init();
    }

    public void addHeader(String key, String value){
        headers.put(key,value);
    }

    public void clearHeaders(){
        headers.clear();
    }

    public void printContent(){
        content.forEach(System.out::println);
    }

    public List<String> getContent() {
        return content;
    }

    private void getHeaders(HttpRequestBase base){
        client= HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        base.addHeader("User-Agent",USER_AGENT);
        if (headers.size()>0){
            for (Map.Entry<String,String>pair:headers.entrySet()){
                base.addHeader(pair.getKey(),pair.getValue());
            }
        }
    }

    public int sendGet(String url, String body){
        return simpleRequestByType("get",url,body);
    }

    public int sendDelete(String url, String body){
        return simpleRequestByType("delete",url,body);
    }

    public int sendPost(String url, String jsonBody){
        //postRequest.addHeader("content-type", "application/x-www-form-urlencoded");
        return sendPutOrPost("post",url,jsonBody);
    }

    public int sendPut(String url, String jsonBody){
        return sendPutOrPost("put",url,jsonBody);
    }

    private int simpleRequestByType(String type, String url, String body){
        HttpRequestBase base;
        if (type.equals("get")) {
            base = new HttpGet(url+body);
        }
        else  {
            base = new HttpDelete(url+body);
        }
        getHeaders(base);
        return getRequestResult(base);
    }

    private int sendPutOrPost(String type, String url, String jsonBody){
        HttpEntityEnclosingRequestBase base;
        if (type.equals("post")) {
            base = new HttpPost(url);
        }
        else {
            base = new HttpPut(url);
        }
        getHeaders(base);
        try {
            StringEntity entity = new StringEntity(jsonBody);
            base.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
        return getRequestResult(base);
    }

    private int getRequestResult(HttpRequestBase base){
        try {
            HttpResponse response = client.execute(base);
            Scanner scanner = new Scanner(new InputStreamReader(response.getEntity().getContent()));
            content.clear();
            while (scanner.hasNext()){
                content.add(scanner.nextLine());
            }
            int result = response.getStatusLine().getStatusCode();
            if (result!=200) {
                content.add(Integer.toString(result));
            }
            return result;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

}
