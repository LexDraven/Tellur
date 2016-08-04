package com.lexdraven.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class WebEventListener extends AbstractWebDriverEventListener {
    private ScreenShooter shooter;
    private boolean needToConsole;

    public WebEventListener(ScreenShooter shooter, boolean needToConsole) {
        this.shooter = shooter;
        this.needToConsole = needToConsole;
    }

    public WebEventListener(boolean needToConsole) {
        this.needToConsole = needToConsole;
    }

    public void setShooter(ScreenShooter shooter) {
        this.shooter = shooter;
    }

    public void onException(Throwable throwable, WebDriver driver) {
        String errorMessage = throwable.getMessage();
        String errorClass = throwable.getClass().getName();
        String errorType = errorClass.substring(errorClass.lastIndexOf(".")+1);
        shooter.getScreenShot(errorType);
        if (needToConsole) {
            System.out.println(errorMessage);
        }
    }


}
