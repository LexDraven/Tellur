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

    public void getScreenShot(String problem) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String fileName = folder + "scr_" + "_" +getCurrentDateTime()+"_"+problem + ".png";
        try {
            FileUtils.copyFile(scrFile, new File(fileName));
        } catch (IOException e) {
            System.out.println("Error making screenshot:" + fileName + " Error-" + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getCurrentDateTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hhmm_dd-MM-yyyy");
        return format.format(date);
    }
}
