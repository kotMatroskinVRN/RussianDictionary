package home.fithteen;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelDictionary implements Model {

    private String task , answer = "" , hint = "";

    private List<String> answerSet ;

    private PropertyResourceBundle prb;

    @Override
    public void init() {

        setAnswerSet();
        createTask();
    }

//    @Override
//    public void solve() {
//
//    }

    @Override
    public boolean checkAnswer(String string) {
        return string.replaceAll("\\(.*\\)" , "").equals(answer);
    }
    @Override
    public String getTask() { return task; }
    @Override
    public String getHint() { return hint; }

///////// PRIVATE ///////////////

    private void createTask(){

        //Random random = new Random();

        if( answer.isEmpty() ){
            answer = answerSet.get((int) (Math.random() * answerSet.size()));
        }else{
            String previousAnswer = answer;
            while( answer.equals(previousAnswer))  {
                answer = answerSet.get((int) (Math.random() * answerSet.size()));
            }
        }





        task   = prb.getString(answer);

        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(task);

        if(matcher.find()) {
            System.out.println(matcher.group(0));
            hint = matcher.group(0)
                    //.replaceAll("[\\(\\)]" , "")
            ;
            task = task.replaceAll( matcher.group(0) , "" )
                    .replaceAll("[\\(\\)]" , "")
            ;
        }
        else hint = "";
    }

    private void setAnswerSet(){

        try {

            InputStreamReader isr = new InputStreamReader(
                    ClassLoader.getSystemResourceAsStream("Words.properties" ), "windows-1251"
                    );
            prb = new PropertyResourceBundle( isr );
            answerSet = new ArrayList<>( prb.keySet() ) ;
        } catch (IOException e) { e.printStackTrace(); }


    }
}
