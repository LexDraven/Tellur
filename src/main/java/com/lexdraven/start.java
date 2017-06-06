package com.lexdraven;

import org.openqa.selenium.By;

/**
 * Created by Лекс on 25.07.2016.
 */
public class start {
    public static void main(String[] args) {
        Tellur tellur = new Tellur("chrome");
        tellur.goToPage("https://google.com");
        tellur.waitForPageToLoad(5);
        tellur.typeTextToInput(By.xpath("//input[@title='Поиск']"),"firefox");
       // tellur.getElement(By.id("q")).submit();
        tellur.pause(2);
        //tellur.setOwnUnhandledExceptionHandler();
        tellur.quit();
    }

}



