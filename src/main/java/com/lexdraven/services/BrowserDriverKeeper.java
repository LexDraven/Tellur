package com.lexdraven.services;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.HashMap;
import java.util.Map;

public class BrowserDriverKeeper {
    private static  HashMap <String, WebDriver> driverPool;

    private WebDriver getDriverByType(String browserType) {
        WebDriver webDriver = null;
        if (browserType.equals("ie")) {
            System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"/vendors/IEDriverServer.exe");
            webDriver = new InternetExplorerDriver();
        }
        if (browserType.equals("chrome")) {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/vendors/chromedriver.exe");
            webDriver=new ChromeDriver();
        }
        if (browserType.equals("html")) {
            webDriver = new HtmlUnitDriver(false);
        }
        if ((browserType.equals("firefox")) || (webDriver == null)) {
            webDriver = new FirefoxDriver();
        }
        return webDriver;
    }

    public BrowserDriverKeeper() {
        driverPool = new HashMap<>();
    }

    public synchronized WebDriver getDriver (String key, String type) {
        WebDriver webDriver = null;
        if (driverPool.containsKey(key)) {
            webDriver = driverPool.get(key);
        }
        else {
            webDriver =getDriverByType(type);
            driverPool.put(key,webDriver);
        }
        return webDriver;
    }

    public void closeDriver(WebDriver newDriver) {
        if (newDriver!=null) {
            if (driverPool.containsValue(newDriver)) {
                driverPool.remove(newDriver);
                newDriver.quit();
            }
        }
    }

    public void closeAll() {
        if (!driverPool.isEmpty()) {
            for (Map.Entry<String, WebDriver> pair: driverPool.entrySet()){
                pair.getValue().quit();
            }
            driverPool.clear();
        }
    }

    public  WebDriver reloadBrowser(String key){
        WebDriver webDriver2=null;
        WebDriver webDriver;
        if (driverPool.containsKey(key)){
            if (driverPool.get(key) instanceof ChromeDriver) {
                webDriver2 = getDriverByType("chrome");
            }
            if (driverPool.get(key) instanceof InternetExplorerDriver) {
                webDriver2 = getDriverByType("ie");
            }
            if (driverPool.get(key) instanceof HtmlUnitDriver) {
                webDriver2 = getDriverByType("html");
            }
            if (webDriver2==null) {
                webDriver2 = getDriverByType("firefox");
            }
            webDriver = driverPool.get(key);
            webDriver.close();
            driverPool.remove(key);
            driverPool.put(key,webDriver2);
        }
        else {
            driverPool.put(key,webDriver2);
        }
        return webDriver2;
    }


}
