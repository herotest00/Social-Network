package socialnetwork.ui.gui.utils;

import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ThemeHandler {
    private static final ArrayList<Parent> panes = new ArrayList<>();

    public static void addRoot(Parent parent){
        panes.add(parent);
    }

    public static void readTheme(Parent node) {
        Scanner sc2;
        try {
            sc2 = new Scanner(new File("currentTheme.txt"));
            String s = sc2.next();
            node.getStylesheets().removeAll("css/darkTheme.css", "css/defaultTheme.css", "css/pinkTheme.css");
            if (!s.equals("darkTheme") && !s.equals("pinkTheme") && !s.equals("defaultTheme")) {
                s = "defaultTheme";
            }
            node.getStylesheets().add("css/" + s + ".css");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeTheme(String theme) {
        try {
            FileWriter myWriter = new FileWriter("currentTheme.txt");
            myWriter.write(theme);
            myWriter.close();
            panes.forEach(x->{
                x.getStylesheets().removeAll("css/darkTheme.css", "css/defaultTheme.css", "css/pinkTheme.css");
                x.getStylesheets().add("css/" + theme + ".css");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void removeRoot(Parent parent){
        panes.remove(parent);
    }


}
