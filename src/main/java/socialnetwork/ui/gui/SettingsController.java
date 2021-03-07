package socialnetwork.ui.gui;

import socialnetwork.ui.gui.utils.ThemeHandler;


public class SettingsController {


    public void pinkThemeClicked() {
        ThemeHandler.writeTheme("pinkTheme");
    }


    public void defaultThemeClicked() {
        ThemeHandler.writeTheme("defaultTheme");
    }


    public void nightThemeClicked() {
        ThemeHandler.writeTheme("darkTheme");
    }
}
