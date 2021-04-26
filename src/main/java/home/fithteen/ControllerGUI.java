package home.fithteen;

public class ControllerGUI implements Controller{

    private Model model;
    String task , hint;

    ControllerGUI(Model model){

        this.model = model;
    }
    @Override
    public boolean action(final String input){

        return model.checkAnswer(input);

    }
    @Override
    public String newTask(){

        model.init();
        hint = model.getHint();


        return model.getTask();
    }

    @Override
    public String getHint() { return hint; }


}
