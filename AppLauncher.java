package SplitterApp;
//It executes the App
import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        // Safely create the Gui
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Creates new window and make it visible on the screen
                new SplitterAppGui().setVisible(true);
            }
        });
    }
}