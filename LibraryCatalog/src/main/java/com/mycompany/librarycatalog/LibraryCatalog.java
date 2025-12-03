package com.mycompany.librarycatalog;

public class LibraryCatalog {

    public static void main(String[] args) {
        // Run the LoginFrame inside SwingUtilities.invokeLater for thread safety
//        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame());

        HomeFrame homeFrame = new HomeFrame();
        homeFrame.showHomeFrame();
    }
}
