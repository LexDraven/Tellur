package com.lexdraven;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by Лекс on 25.07.2016.
 */
public class start {
    public static void main(String[] args) {
        Tellur tellur = new Tellur(new FirefoxDriver());
        tellur.goToPage("https://www.zendesk.com.ru/register/#getstarted");
        if (tellur.isJQueryOnThisPage()) {
            tellur.waitForJQueryEnds();
        }
        tellur.setOwnUnhandledExceptionHandler();
        tellur.clickElement(By.id("error"));//error occurred
        throwError();
        tellur.quit();
    }

    private static void throwError(){
        throw new IndexOutOfBoundsException("wassa");
    }
}



