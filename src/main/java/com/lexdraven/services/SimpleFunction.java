package com.lexdraven.services;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

public class SimpleFunction {

    public String getRandomName(int numberOfChars) {
        String chars="abcdefghijklmnopqrstuvwxyz";
        String result="";
        for (int i=0;i<numberOfChars;i++){
            int cas = new Random().nextInt(25);
            result = result+chars.charAt(cas);
        }
        return result;
    }

    public String getDate() {
        Calendar dt = Calendar.getInstance();
        int month, day, hour, min;
        dt.getTime();
        month = dt.get(Calendar.MONTH);
        month++;
        day = dt.get(Calendar.DAY_OF_MONTH);
        hour = dt.get(Calendar.HOUR);
        if (dt.get(Calendar.AM_PM) == Calendar.PM) {
            hour = hour + 12;
        }
        min = dt.get(Calendar.MINUTE);
        return day + "_" + month + "_" + hour + "-" + min;
    }

    public void createFoldersForLogsAndScreenshots(String path) {
        File f = new File(path + "/Logs/");
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Не удалось создать папку для логов!!!");
                System.exit(2);
            }
        }
        f = new File(path + "/Screenshots/");
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Не удалось создать папку для скринов!!!");
            }
        }
    }

}
