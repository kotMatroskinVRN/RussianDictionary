package home.fithteen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputContext;
import java.util.ArrayList;


/**
 * @author Manankov Andrey
 * @version 1.0.0
 *
 * GUI for solving equation program
 *
 * Task field
 * Solve button
 * Result area
 * Margins
 */
class Gui extends JFrame implements View{

    private final Controller controller;


    private final Font font = new Font("Serif", Font.PLAIN, 20);
    private final JTextArea textArea = new JTextArea(   "Ошибки" + " :\n\n"  );
    private final JButton button = new JButton("Ответ");
    private final JPanel innerNorth;

    private final JLabel total   = new JLabel( "Верно всего : " + "0");
    private final JLabel inArow  = new JLabel("Верно подряд : " + "0");

    private final ArrayList<OneSimbolTextField> gaps = new ArrayList<>();

    private JPanel askPanel ;
    private JLabel hintLabel ;

    private String input ;
    private String hint ;
    private int landslide = 0 , correct = 0 ;

    /**
     * Main constructor
     *
     * setup GUI
     */
    Gui(Controller controller) {
        this.controller = controller;

        // main window settings
        setTitle("Словарные слова");
        double RATIO = 0.5625;
        int x = 400;
        setBounds( 350 , 50 , x , (int)(x / RATIO)  );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // prevent editiong of result text field
        textArea.setEditable(false);

        // set result text field scrollable
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setHorizontalScrollBar(null);

        // create button "Решить"


        //JPanel emptyPanel = new JPanel();

        // create margins using empty panels in all sides except center
        JPanel north = new JPanel();
        JPanel west  = new JPanel();
        JPanel east  = new JPanel();
        JPanel south = new JPanel();


        // south status bar
        GridLayout gridLayoutSouth = new GridLayout(1,2);
        inArow.setHorizontalAlignment(JLabel.CENTER);
        inArow.setForeground(Color.BLUE);
         total.setHorizontalAlignment(JLabel.CENTER);

        south.setLayout( gridLayoutSouth );
        south.add(inArow);
        south.add(total);

        // create panel for task and button
        innerNorth = new JPanel();
        input = controller.newTask();
        askPanel = createAskPanel( input );
        hint = controller.getHint();

        hintLabel = new JLabel(hint);
        hintLabel.setFont(font);
        button.setFont(font);

        // put task and button to inner pane
        // new JPanel does not have BorderLayout!!! JFrame has!!!
        BorderLayout topLayout = new BorderLayout();
        innerNorth.setLayout( topLayout );

        innerNorth.add( askPanel     , BorderLayout.BEFORE_LINE_BEGINS );
        innerNorth.add( hintLabel    , BorderLayout.CENTER );
        innerNorth.add( button       , BorderLayout.LINE_END  );
        innerNorth.add( new JPanel() , BorderLayout.SOUTH );

        // fill north pane with inner pane and empty panes for margins
        north.setLayout( new BorderLayout() );
        north.add(innerNorth   , BorderLayout.CENTER);
        north.add(new JPanel() , BorderLayout.WEST);
        north.add(new JPanel() , BorderLayout.EAST);
        north.add(new JPanel() , BorderLayout.NORTH);


        // put all in main frame
        add(north , BorderLayout.NORTH);
        add(jsp   , BorderLayout.CENTER);
        add(west  , BorderLayout.WEST);
        add(east  , BorderLayout.EAST);
        add(south , BorderLayout.SOUTH);


        // create Action Listener and set it to button and task field key ENTER
        // action must be performed in new Thread , not in EventQueue
        ActionListener actionListener = new ButtonAction();
        button.addActionListener( actionListener );
        button.addKeyListener  ( new SwapFocusField() );


        checkInputLanguage();

        // make main frame visible
        setVisible(true);
    }



    /**
     * Main action
     *
     * gets text from task field
     * creates equation instance
     * appends text area
     * clears task field
     *
     * contains some examples : 1 2 3 4 0
     * type just number and press ENTER
     */
    @Override
    public void action() {

        SwingUtilities.invokeLater( () -> showFields(false));

        //System.out.println( concatAnswer());

        // append text area
        if( isPanelFilled() ) {
            if (controller.action( concatAnswer() ) ){

                SwingUtilities.invokeLater( () -> {
                     total.setText( "Верно всего : "  + ++correct);
                    inArow.setText( "Верно подряд : " + ++landslide);
                    revalidate();
                });
            }
            else{
                landslide = 0;
                String result = textArea.getText() + "\n" + input.replaceAll("@" , "_") ;

                SwingUtilities.invokeLater( () -> {
                    textArea.setText(result);
                    inArow.setText( "Верно подряд : " + landslide);
                    revalidate();
                });

            }

            SwingUtilities.invokeLater( () -> {
                innerNorth.remove(askPanel);

                innerNorth.repaint();
                input = controller.newTask();
                askPanel = createAskPanel( input);
                hint = controller.getHint();
                hintLabel.setText(hint);

                innerNorth.add( askPanel ,BorderLayout.BEFORE_LINE_BEGINS);
                innerNorth.revalidate();

                showFields(true);


            } );
        }





    }

    private boolean isPanelFilled(){

        for (OneSimbolTextField field : gaps){
            if(field.getText().isEmpty()) return false;
        }
        return true;
    }

    private String concatAnswer(){
        Component[] components = askPanel.getComponents();

        StringBuilder answer = new StringBuilder();

        for (Component component : components){

            if(component.getClass().isInstance(new JLabel())){
                answer.append( ((JLabel)component).getText().trim() );
            }
            if(component.getClass().isInstance(new OneSimbolTextField())){
                answer.append( ((OneSimbolTextField)component).getText().trim() );
            }
        }
        return answer.toString();
    }

    private JPanel createAskPanel(String string){
        JPanel result = new JPanel();

        gaps.clear();

        String[] parts = string.trim()
                .replaceAll("@" , " _ ")
                .split("\\s+");
        //System.out.println(parts.length);
        for(String part: parts){
            //System.out.println( part + "!");

            if(part.equals("_")){

                OneSimbolTextField field = new OneSimbolTextField();
                field.setFont(font);
                field.addKeyListener(new SwapFocusField());
                gaps.add(field);
                result.add(field);

                //System.out.println( gaps.size() );

            }
            else {
                JLabel label = new JLabel(part);
                label.setFont(font);
                result.add(label);

            }
        }

        return result;
    }


    private void showFields(boolean enable){

        if(enable){
            button.setEnabled(true);
            //task.setEnabled(true);
            gaps.get(0).requestFocusInWindow();
        }
        else {
            button.setEnabled(false);
            //task.setEnabled(false);

        }
    }

    private void checkInputLanguage(){
        InputContext context = InputContext.getInstance();
        //System.out.println(context.getLocale().getCountry());

        switch (context.getLocale().getCountry()){
            case "RU":
            case "ru":
                button.setEnabled(true);
                break;
            case "EN":
            case "US":
            case "en":
            case "us":
                button.setEnabled(false);
                break;
        }

    }


    class ButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread( new ActionThread() ).start();
        }
    }

    class ActionThread implements Runnable{
        @Override
        public void run() {
            action();
        }
    }


    class SwapFocusField implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e){

            char c = e.getKeyChar();


            if(e.getComponent().getClass().isInstance(new OneSimbolTextField())) {
                OneSimbolTextField textField = (OneSimbolTextField) e.getSource();
                String text = textField.getText();


                if (!( (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) && text.length() == 1) {

                    if( (c >= 'А') && (c <= 'я') ) {
                        focusRight(textField);
                    }
                }

            }



            Gui.this.checkInputLanguage();


        }

        @Override
        public void keyPressed(KeyEvent e) { }

        @Override
        public void keyReleased(KeyEvent e) {

            if( e.getComponent().getClass().isInstance(button) && e.getKeyChar() == KeyEvent.VK_ENTER ){

                new Thread( new ActionThread() ).start();



            }

        }

        private void focusRight(OneSimbolTextField textField ){

            for (int i = 0; i < gaps.size(); i++) {
                if (textField == gaps.get(i)) {
                    if (i < gaps.size() - 1) gaps.get(i + 1).requestFocusInWindow();
                    else button.requestFocusInWindow();
                }
            }
        }
        private void focusLeft(OneSimbolTextField textField ){

            for (int i = 1; i < gaps.size(); i++) {
                if (textField == gaps.get(i)) { gaps.get(i - 1).requestFocusInWindow(); }
            }
        }

    }


}


