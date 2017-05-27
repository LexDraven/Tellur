package com.lexdraven.services;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShooter {
    private WebDriver driver;
    private String folder;

    public ScreenShooter(WebDriver driver, String folder) {
        this.driver = driver;
        this.folder = folder;
    }

    public void getScreenShot(String problem) throws IOException{
        if (isDriverAlive(driver)) {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fileName = folder + "scr_" + "_" + getCurrentDateTime() + "_" + problem + ".png";
            FileUtils.copyFile(scrFile, new File(fileName));
        }
    }

    public String getCurrentDateTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hhmm_dd-MM-yyyy");
        return format.format(date);
    }

    private boolean isDriverAlive(WebDriver newDriver) {
        try {
            newDriver.getTitle();
            return true;
        } catch (Exception e) {
            System.out.println("Driver is not respond! " + e.getMessage());
            return false;
        }
    }
}
