package com.lexdraven.services;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Informer {
    private FileHandler handler;
    private String message;
    private int type;
    private static boolean hasInstance=false;
    private static Informer instance;
    private String userFolder;
    private SimpleFunction simpleFunction;
    private ScreenShooter shooter;

    private Informer(String userFolder, int size, int count) {
        try {
            this.userFolder = userFolder;
            simpleFunction = new SimpleFunction();
            simpleFunction.createFoldersForLogsAndScreenshots(userFolder);
            handler = new FileHandler(userFolder+"/Logs/Log" + simpleFunction.getDate() + ".txt", size, count);
            handler.setFormatter(new SimpleFormatter());
            handler.setEncoding("UTF-8");
            handler.setLevel(Level.INFO);
        } catch (IOException e) {
            System.out.println("Проблема при подключении логгера!");
            e.printStackTrace();
            System.exit(11);
        }
    }

    public static Informer createInformer (String userFolder, int size, int count) {
        if (!hasInstance) {
            hasInstance=true;
            instance = new Informer(userFolder,size,count);
        }
        return instance;
    }

    public void toLogAndConsole(String message, int type) {
        this.message = message;
        this.type = type;
        putInfoToSource(true,true);
    }

    public void toConsole (String message, int type) {
        this.message = message;
        this.type = type;
        putInfoToSource(true,false);
    }

    public void toLog (String message, int type) {
        this.message = message;
        this.type = type;
        putInfoToSource(false,true);
    }

    private void putInfoToSource(boolean console, boolean log) {
        if (console) {
            if (type == 0) {
                System.out.println(message);
            } else {
                System.err.println(message);
            }
        }
        if (log) {
            Level level = Level.INFO;
            if (type == 2) {
                level = Level.SEVERE;
            }
            if (type == 1) {
                level = Level.WARNING;
            }
            handler.publish(new LogRecord(level, message));
            handler.flush();
        }
    }

    public void getScreenshot(WebDriver browser, String problem){
        shooter = new ScreenShooter(browser,userFolder + "/Screenshots/");
        try {
            shooter.getScreenShot(problem);
        } catch (IOException e) {
            toLogAndConsole("Проблема при создании файла скриншота!", 2);
        }
    }

    public void close() {
        handler.close();
    }
}
