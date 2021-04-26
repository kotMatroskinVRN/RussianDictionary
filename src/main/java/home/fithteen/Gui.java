package home.fithteen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

    private final String HEADER  = "Словарные слова";
    //private final JTextField    task = new JTextField(" ",10 );
    private final JTextArea textArea = new JTextArea(   "Ошибки" + " :\n\n"  );
    private final JButton button;
    private JPanel askPanel ;
    private JPanel innerNorth;
    private JLabel hintLabel ;

    JLabel inArow = new JLabel("0");
    JLabel total  = new JLabel("0");

    private final ArrayList<OneSimbolTextField> gaps = new ArrayList<>();

    private String input ;
    private String hint ;
    private int landslide =0 , correct = 0 ;
    private boolean isLandslide = true;

    /**
     * Main constructor
     *
     * setup GUI
     */
    Gui(Controller controller) {

        // main window settings
        setTitle(HEADER);
        double RATIO = 0.5625;
        int x = 400;
        setBounds( 50 , 50 , x , (int)(x / RATIO)  );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.controller = controller;

        // prevent editiong of result text field
        textArea.setEditable(false);

        //task.setFont(new Font(null , "Monospace" , 10));
        //task.setMaximumSize(new Dimension(10,1));
        //task.setMinimumSize(new Dimension(10,1));
        //task.addKeyListener(new LimitEnter());

        //createAskPanel("гр  пфр т");

        // set result text field scrollable
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setHorizontalScrollBar(null);

        // create button "Решить"
        button = new JButton("Ответ");

        // create margins using empty panels in all sides except center
        JPanel north = new JPanel();
        JPanel west  = new JPanel();
        JPanel east  = new JPanel();
        JPanel south = new JPanel();


        south.add(inArow);
        south.add(total);

        // create panel for task and button
        innerNorth = new JPanel();


        input = controller.newTask();
        askPanel = createAskPanel( input );
        hint = controller.getHint();

        hintLabel = new JLabel(hint);

        // put task and button to inner pane
        // TODO
        //  choose layout for task pane

        innerNorth.setLayout( new GridLayout(1,3) );
        innerNorth.add( askPanel  );
        innerNorth.add( hintLabel );
        innerNorth.add(button );

        // fill north pane with inner pane and empty panes for margins
        //north.setLayout( new BorderLayout() );
        north.add(innerNorth);
        north.add(new JPanel() , BorderLayout.WEST);
        //north.add(new JPanel() , BorderLayout.EAST);
        north.add(new JPanel() , BorderLayout.NORTH);

        // put all in main frame
        add(north , BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(west  , BorderLayout.WEST);
        add(east  , BorderLayout.EAST);
        add(south , BorderLayout.SOUTH);


        // create Action Listener and set it to button and task field key ENTER
        // action must be performed in new Thread , not in EventQueue
        ActionListener actionListener = new ButtonAction();

        button.addActionListener( actionListener );
        button.addKeyListener  ( new SwapFocusField() );



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






        System.out.println( concatAnswer());

        // append text area
        if( isPanelFilled() ) {
            if (controller.action( concatAnswer() ) ){

                total.setText(String.valueOf(++correct));

                if(isLandslide){
                    inArow.setText(String.valueOf(++landslide));
                }

            }
            else{
                isLandslide = false;
                String result = textArea.getText() + "\n" + input ;

                SwingUtilities.invokeLater( () -> textArea.setText(result) );

            }

            SwingUtilities.invokeLater( () -> {
                //askPanel.removeAll();
                innerNorth.remove(askPanel);

                innerNorth.repaint();
                input = controller.newTask();
                askPanel = createAskPanel( input);
                hint = controller.getHint();
                System.out.println("new task");
                hintLabel.setText(hint);

                innerNorth.add( askPanel ,0);
                //innerNorth.add(new JLabel(hint), BorderLayout.CENTER);
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
                //if( gaps.size()==1 ) field.requestFocusInWindow();
                field.addKeyListener(new SwapFocusField());
                gaps.add(field);
                result.add(field);

                //System.out.println( gaps.size() );

            }
            else {
                result.add(new JLabel(part));

            }
        }


        //
        //result.revalidate();


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

            if(e.getComponent().getClass().isInstance(new OneSimbolTextField())) {
                OneSimbolTextField textField = (OneSimbolTextField) e.getSource();
                String text = textField.getText();
                char c = e.getKeyChar();

                if (!((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) && text.length() == 1) {

                    for (int i = 0; i < gaps.size(); i++) {
                        if (textField == gaps.get(i)) {
                            if (i < gaps.size() - 1) gaps.get(i + 1).requestFocusInWindow();
                            else button.requestFocusInWindow();
                        }
                    }
                }

            }

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

            if( e.getComponent().getClass().isInstance(button) && e.getKeyChar() == KeyEvent.VK_ENTER ){

                new Thread( new ActionThread() ).start();



            }

        }
    }


}


