package com.lexdraven.services;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BrowserDriverKeeper {
    private WebDriver webDriver = null;
    private static HashMap<String, WebDriver> driverPool = new HashMap<>();
    private final int timeToWait = 3;
    private boolean needLogs = false;

    public void setNeedLogs(boolean needLogs) {
        this.needLogs = needLogs;
    }

    private WebDriver getDriverByType(String browserType) {
        if (browserType.equals("ie")) {
            System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "/vendors/IEDriverServer.exe");
            webDriver = new InternetExplorerDriver();
        }
        if (browserType.contains("chrome")) {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/vendors/chromedriver.exe");
            if (browserType.startsWith("mobile")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--no-sandbox");
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "Apple iPhone 5");
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                webDriver = new ChromeDriver(capabilities);
            } else {
                webDriver = new ChromeDriver();
            }
        }
        if (browserType.equals("firefox")) {
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/vendors/geckodriver.exe");
            webDriver = new FirefoxDriver();
        }
        if (browserType.equals("html")) {
            webDriver = new HtmlUnitDriver(false);
        }
        return webDriver;
    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public WebDriver getDriver(String key, String type) {
        if (driverPool.containsKey(key)) {
            return driverPool.get(key);
        }
        webDriver = getDriverByType(type);
        webDriver.manage().timeouts().implicitlyWait(timeToWait, TimeUnit.SECONDS);
        if (!type.contains("mobile")) {
            webDriver.manage().window().maximize();
        }
        synchronized (driverPool) {
            driverPool.put(key, webDriver);
        }
        return webDriver;
    }

    public int getSize() {
        return driverPool.size();
    }

    public void closeDriver(WebDriver newDriver) {
        if (newDriver != null) {
            if (driverPool.containsValue(newDriver)) {
                synchronized (driverPool) {
                    driverPool.remove(newDriver);
                }
                try {
                    if (needLogs) {
                        getAllLogs(newDriver);
                    }
                    newDriver.quit();
                } catch (RuntimeException ignore) {
                    System.out.println("Kill driver message-" + ignore.getMessage());
                }
            }
        }
    }

    private void getAllLogs(WebDriver driver) {
        Logs logs = driver.manage().logs();
        LogEntries logEntries = logs.get(LogType.BROWSER);
        for (LogEntry logEntry : logEntries.filter(Level.SEVERE)) {
            if (!logEntry.getMessage().contains("Invalid 'X-Frame-Options'")) {
                System.out.println("!$! " + logEntry.getMessage());
            }
        }
    }

    public void closeDriverByKey(String key) {
        if (driverPool.containsKey(key)) {
            WebDriver deadDriver = driverPool.get(key);
            closeDriver(deadDriver);
        }
    }

    public void closeAll() {
        if (!driverPool.isEmpty()) {
            for (Map.Entry<String, WebDriver> pair : driverPool.entrySet()) {
                closeDriver(pair.getValue());
            }
            synchronized (driverPool) {
                driverPool.clear();
            }
        }
    }

    public WebDriver reloadBrowser(String key) {
        WebDriver webDriver2 = null;
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


}

