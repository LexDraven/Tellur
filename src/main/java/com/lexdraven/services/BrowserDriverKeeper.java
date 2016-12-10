package com.lexdraven.services;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BrowserDriverKeeper {
    private static  HashMap <String, WebDriver> driverPool;
    private final int timeToWait = 3;

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
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"/vendors/geckodriver.exe");
            webDriver = new FirefoxDriver();
        }
        return webDriver;
    }

    public BrowserDriverKeeper() {
        driverPool = new HashMap<>();
    }

    public int getSize(){
        return driverPool.size();
    }

    public WebDriver getDriver(String key, String type) {
        WebDriver webDriver = null;
        if (driverPool.containsKey(key)) {
            return driverPool.get(key);
        }
        webDriver = getDriverByType(type);
        webDriver.manage().timeouts().implicitlyWait(timeToWait, TimeUnit.SECONDS);
        webDriver.manage().window().maximize();
        synchronized (driverPool) {
            driverPool.put(key, webDriver);
        }
        return webDriver;
    }

    public void closeDriver(WebDriver newDriver) {
        if (newDriver != null) {
            if (driverPool.containsValue(newDriver)) {
                synchronized (driverPool) {
                    driverPool.remove(newDriver);
                }
                newDriver.quit();
            }
        }
    }

    public void closeAll() {
        if (!driverPool.isEmpty()) {
            for (Map.Entry<String, WebDriver> pair : driverPool.entrySet()) {
                pair.getValue().quit();
            }
            synchronized (driverPool) {
                driverPool.clear();
            }
        }
    }

    public WebDriver reloadBrowser(String key) {
        WebDriver webDriver2 = null;
        WebDriver webDriver = null;
        synchronized (driverPool) {
            if (driverPool.containsKey(key)) {
                if (driverPool.get(key) instanceof ChromeDriver) {
                    webDriver2 = getDriverByType("chrome");
                }
                if (driverPool.get(key) instanceof InternetExplorerDriver) {
                    webDriver2 = getDriverByType("ie");
                }
                if (driverPool.get(key) instanceof HtmlUnitDriver) {
                    webDriver2 = getDriverByType("html");
                }
                if (webDriver2 == null) {
                    webDriver2 = getDriverByType("firefox");
                }
                webDriver2.manage().window().maximize();
                webDriver = driverPool.get(key);
                webDriver.close();
                driverPool.replace(key, webDriver, webDriver2);
            } else {
                webDriver2 = getDriverByType("firefox");
                driverPool.put(key, webDriver2);
            }
        }
        return webDriver2;
    }

    public void closeDriverByKey(String key) {
        if (driverPool.containsKey(key)) {
            WebDriver deadDriver;
            synchronized (driverPool) {
                deadDriver = driverPool.get(key);
                driverPool.remove(key);
            }
            deadDriver.quit();
        }
    }

}
