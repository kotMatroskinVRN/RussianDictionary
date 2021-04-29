package home.fithteen;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class OneSimbolTextField extends JTextField {

    OneSimbolTextField() {
        super(" ", 1);

        addKeyListener(new LimitEnter());

    }


    class LimitEnter implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e)
        {

            char c = e.getKeyChar();
            //String text = ((JTextField) e.getSource()).getText();
            String text = OneSimbolTextField.this.getText();

            if (!((c >= 'А') && (c <= 'я') ||
                    (c == KeyEvent.VK_BACK_SPACE) ||
                    (c == KeyEvent.VK_DELETE))
                    || text.length()>1
            ) {

                getToolkit().beep();

                e.consume();

            }



        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
