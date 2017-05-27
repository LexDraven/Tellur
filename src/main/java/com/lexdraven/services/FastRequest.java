package com.lexdraven.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class FastRequest {
    private int timeOut = 7000;
    private RequestConfig config;
    private HttpClient client;

    public FastRequest() {
        config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setSocketTimeout(timeOut).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(Level.OFF);
    }

    private HttpResponse getRequest(String URL) throws Exception {
        HttpGet request = new HttpGet(URL);
        return client.execute(request);
    }

    public int getRequestStatusCode(String URL) throws Exception {
        return getRequest(URL).getStatusLine().getStatusCode();
    }

    public boolean isSuccess(String URL) throws Exception {
        return getRequestStatusCode(URL) == 200;
    }

    public void writeCookiesToFile(WebDriver webDriver) {
        File file = new File("browser.dat");
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Cookie cookie : webDriver.manage().getCookies()) {
                writer.write((cookie.getName() + ";" + cookie.getValue() + ";" + cookie.getDomain() + ";" + cookie.getPath() + ";" + cookie.getExpiry() + ";" + cookie.isSecure()));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Ошибка при записи куки - " + e.getLocalizedMessage());
        }
    }

    public Cookie readCookiesFromFile() {
        Cookie cookie = new Cookie("", "");
        try {
            File file = new File("browser.dat");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer str = new StringTokenizer(line, ";");
                while (str.hasMoreTokens()) {
                    String name = str.nextToken();
                    String value = str.nextToken();
                    String domain = str.nextToken();
                    String path = str.nextToken();
                    Date expiry = null;
                    String date = str.nextToken();
                    if (!(date).equals("null")) {
                        expiry = new Date(System.currentTimeMillis() * 2);
                    }
                    boolean isSecure = Boolean.parseBoolean(str.nextToken());
                    cookie = new Cookie(name, value, domain, path, expiry, isSecure);
                }
            }
        } catch (Exception ex) {
            System.out.println("Ошибка при чтении куки - " + ex.getLocalizedMessage());
        }
        return cookie;
    }

    private LinkedList<String> getElementsByTag(String tag, String htmlCode) {
        LinkedList<String> linkedList = new LinkedList<>();
        String element = getElement(tag, htmlCode);
        while (!element.equals("!!!")) {
            linkedList.add(element);
            htmlCode = htmlCode.substring(htmlCode.indexOf(element) + element.length());
            element = getElement(tag, htmlCode);
        }
        return linkedList;
    }

    private String getElement(String tag, String htmlCode) {
        String tagStart = "<" + tag;
        String tagEnd = "</" + tag + ">";
        if (!htmlCode.contains(tagStart)) {
            return "!!!";
        }
        return htmlCode.substring(htmlCode.indexOf(tagStart), htmlCode.indexOf(tagEnd) + tag.length() + 3);
    }

    private String getAttribute(String attr, String element) {
        if (!element.contains(attr)) {
            return "";
        }
        String[] words = element.split("\"");
        element = "";
        for (int i = 0; i < words.length; i++) {
            if ((words[i].contains(attr)) && (i + 1 < words.length)) {
                element = words[i + 1];
                break;
            }
        }
        return element;
    }

    public HashSet<String> getLinksFromUrl(String url,String baseUrl, String tag, String attr) {
        HttpRequestsSender sender = new HttpRequestsSender();
        HashSet<String> result = new HashSet<>();
        sender.sendGet(url, "");
        LinkedList<String> list = getElementsByTag(tag, sender.getContent().toString());
        String [] tokens = url.split("/");
        String finalWord = tokens[tokens.length-1];
        for (String link : list) {
            link = getAttribute(attr, link);
            if (link.length()>3) {
                if (link.startsWith("/")) {
                    if (link.startsWith("/"+finalWord)){
                        link=link.substring(finalWord.length()+1);
                        link=url+link;
                    }
                    else {
                        link = link.startsWith("//")? "http:"+link : baseUrl + link;
                    }
                }
                if (!link.startsWith("#")) {
                    result.add(link);
                }
            }
        }
        return result;
    }


}
