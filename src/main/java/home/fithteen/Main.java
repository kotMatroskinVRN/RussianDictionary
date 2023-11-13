package home.fithteen;

import javax.swing.*;

/**
 * @author Andrey Manankov
 * @version 1.0.0
 *
 * Main Class
 * run GUI
 * *
 *
 */
public class Main {


    public static void main(String[] args) {

        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            Model model = new ModelDictionary();
            Controller controllerGUI = new ControllerGUI(model);

            SwingUtilities.invokeLater(() -> new Gui(controllerGUI));

        }
        catch (UnsupportedLookAndFeelException
                | ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                e) {
            e.printStackTrace();
        }

    }
}
