package com.lexdraven;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

/**
 * Created by Лекс on 25.07.2016.
 */
public class start {
    public static void main(String[] args) {
        Tellur tellur = new Tellur("chrome");
        tellur.goToPage("https://google.com");

        tellur.setSize(1200,600);
        tellur.setPosition(0,0);
        tellur.pause(5);
        Screen screen = new Screen();
        screen.setRect(new Region(0,0,1200,600));
        String path = System.getProperty("user.dir") + "/vendors/1.png";
        Pattern pattern = new Pattern(path).similar(0.75f);
        try {
            screen.find(pattern);
        } catch (FindFailed findFailed) {
            System.out.println("not match");
        }
        tellur.quit();
    }

}



