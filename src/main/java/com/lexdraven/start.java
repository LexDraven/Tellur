package com.lexdraven;

import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by Лекс on 25.07.2016.
 */
public class start {
    public static void main(String[] args) {
        Tellur tellur = new Tellur(new FirefoxDriver());
        tellur.goToPage("https://www.zendesk.com.ru/register/#getstarted");
        System.out.println(tellur.isJQueryOnThisPage());
        tellur.waitForJQueryEnds();
        tellur.quit();
    }
}



