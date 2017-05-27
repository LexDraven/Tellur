package com.lexdraven.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.IOException;
import java.lang.reflect.Field;

public class TestController extends TestListenerAdapter {
    private static Informer informer;

    public static void setInformer (Informer newInformer){
        informer = newInformer;
    }


    @Override
    public void onTestFailure(ITestResult tr) {
        String errorMessage = tr.getThrowable().getLocalizedMessage();
        String name = tr.getMethod().getMethodName();
        createScreenshotIfPossible(tr);
        if ((errorMessage != null) && (!errorMessage.contains("expected ["))) {
            toLogAndConsole("Ошибка в тесте " + name + "! " + errorMessage, 2);
        }
        toLogAndConsole(name + " ПРОВАЛЕН!!!", 2);
    }

    private void createScreenshotIfPossible(ITestResult tr) {
        try {
            Field realDriver = tr.getMethod().getRealClass().getField("driver");
            try {
                WebDriver driver = (WebDriver) realDriver.get(tr.getInstance());
                if ((driver != null) && !(driver instanceof HtmlUnitDriver)) {
                    try {
                        new ScreenShooter(driver, "/Screens/").getScreenShot(tr.getMethod().getMethodName() + "_TestFailed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Failed url: "+driver.getCurrentUrl());
                }
            } catch (IllegalAccessException e) {
                System.out.println("Ошибка доступа! " + e.getMessage());
            }
        } catch (NoSuchFieldException ex) {
            System.out.println("Не доступен драйвер для скрина!");
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        toLogAndConsole(tr.getMethod().getMethodName() + " успешно завершен!", 0);
    }

    @Override
    public void onTestStart(ITestResult tr) {
        toLogAndConsole("Старт теста " + tr.getMethod().getMethodName(), 0);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        toLogAndConsole(tr.getMethod().getMethodName() + " пропущен!", 1);
    }

    private void toLogAndConsole(String message, int type) {
        informer.toLogAndConsole(message, type);
    }
}
