package com.lexdraven.services;

import org.openqa.selenium.WebDriver;

public class OwnExceptionHandler  implements Thread.UncaughtExceptionHandler{
    private boolean isStackTraceNeeded=false;
    private boolean isLogNeeded=false;
    private boolean isScreenshotNeeded=true;
    private String userDir;
    private WebDriver driver;
    private ScreenShooter shooter;

    public OwnExceptionHandler(WebDriver driver, boolean isStackTraceNeeded, boolean isLogNeeded) {
        this.isStackTraceNeeded = isStackTraceNeeded;
        this.isLogNeeded = isLogNeeded;
        this.driver = driver;
        userDir = System.getProperty("user.dir")+"/Screenshots/";
        shooter = new ScreenShooter(driver,userDir);
    }

    public void setStackTraceNeeded(boolean stackTraceNeeded) {
        isStackTraceNeeded = stackTraceNeeded;
    }

    public void setLogNeeded(boolean logNeeded) {
        isLogNeeded = logNeeded;
    }

    public void setScreenshotNeeded(boolean screenshotNeeded) {
        isScreenshotNeeded = screenshotNeeded;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public void uncaughtException(Thread t, Throwable e) {
        String errorMessage = e.getMessage();
        String errorClass = e.getClass().getName();
        if (isScreenshotNeeded) {
            String errorType = errorClass.substring(errorClass.lastIndexOf(".")+1);
            shooter.getScreenShot(errorType);
        }
        if (!isLogNeeded) {
            System.err.println("Error occured in "+t+" - "+errorMessage);
            if (isStackTraceNeeded) {
                e.printStackTrace();
            }
        }
        else {
            //TODO write to logs
        }
    }

}
