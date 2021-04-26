package home.fithteen;

public interface Model {

    void init();
    //void solve();
    boolean checkAnswer(String string);
    String getTask();
    String getHint();
}
