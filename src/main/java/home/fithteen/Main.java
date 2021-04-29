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


        Model model = new ModelDictionary();
        Controller controllerGUI = new ControllerGUI(model);

        SwingUtilities.invokeLater(() -> new Gui(controllerGUI));


    }
}
