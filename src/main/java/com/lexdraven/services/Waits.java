package com.lexdraven.services;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Waits {
    private int timeOut =5;
    private WebDriver driver;

    public Waits(WebDriver driver) {
        this.driver = driver;
    }

    public boolean waitFor (By locator, WaitConditions conditions) {
        WebDriverWait wait = new WebDriverWait(driver,timeOut);
        try {
            wait.until(conditions.getType().apply(locator));
            return true;
        }
        catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean waitFor (By locator, WaitConditions conditions, int time){
        this.timeOut = time;
        return waitFor(locator,conditions);
    }




}
