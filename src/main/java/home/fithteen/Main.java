package home.fithteen;

import javax.swing.*;
import java.io.IOException;
import java.util.PropertyResourceBundle;

/**
 * @author Andrey Manankov
 * @version 1.0.0
 *
 * Main Class
 * run GUI
 *
 * defines if result is rounded by first argument
 *
 * argument  - blank or sigma(0.1 0.001 0.0001 etc)
 *
 *
 */
public class Main {

    //private static Gui gui;
    //private static LinearEquation linearEquation;

    public static void main(String[] args) {


        Model model = new ModelDictionary();
//        if(args.length>0){
//            System.out.println("false round");
//            linearEquation.falseRound();
//            linearEquation.setSIGMA( Double.parseDouble(args[0]));
//
//        }


        Controller controllerGUI = new ControllerGUI(model);

        SwingUtilities.invokeLater(() -> new Gui(controllerGUI));





    }
}
