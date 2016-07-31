package com.lexdraven;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class OwnExceptionHandler  implements Thread.UncaughtExceptionHandler{
    private boolean isStackTraceNeeded=false;
    private boolean isLogNeeded=false;
    private boolean isScreenshotNeeded=true;
    private String userDir;
    private WebDriver driver;

    public OwnExceptionHandler(WebDriver driver, boolean isStackTraceNeeded, boolean isLogNeeded) {
        this.isStackTraceNeeded = isStackTraceNeeded;
        this.isLogNeeded = isLogNeeded;
        this.driver = driver;
        userDir = System.getProperty("user.dir")+"/Screenshots/";
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
        String errorMessage = e.getLocalizedMessage();
        String errorClass = e.getClass().getName();
        if (isScreenshotNeeded) {
            String errorType = errorClass.substring(errorClass.lastIndexOf(".")+1);
            getScreenShot(errorType);
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

    public void getScreenShot(String problem) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String fileName = userDir + "scr_" + "_" + problem + ".png";
        try {
            FileUtils.copyFile(scrFile, new File(fileName));
        } catch (IOException e) {
            System.out.println("Error making screenshot:" + fileName + " Error-" + e.getMessage());
            e.printStackTrace();
        }
    }

}
