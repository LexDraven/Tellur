package com.lexdraven.services;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

    public void waitForJQueryEnds() {
        while ((Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active!=0"));
    }

    public boolean isJQueryOnThisPage() {
        return ((JavascriptExecutor) driver).executeScript("return (window.jQuery)") != null;
    }

    public boolean waitForPageToLoad(int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver,timeInSeconds);
        try {
            wait.until((ExpectedCondition<Boolean>) driver -> ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean waitUntilPageTitle(String title, int time) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        try {
            wait.until(ExpectedConditions.titleIs(title));
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }




}
