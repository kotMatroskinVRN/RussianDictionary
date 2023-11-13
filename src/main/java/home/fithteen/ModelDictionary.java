package home.fithteen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelDictionary implements Model {

    private String task , answer = "" , hint = "";

    private List<String> answerSet ;
    private Map<String , Integer> wordFrequency = new TreeMap<>() ;

    private double average;

    private PropertyResourceBundle prb;

    ModelDictionary(){
        setAnswerSet();
    }

    @Override
    public void init() {


        createTask();

        //System.out.println("init");
    }


    @Override
    public boolean checkAnswer(String string) {
        System.out.println(string);
        return string.replaceAll("\\(.*\\)" , "").equals(answer.replaceAll("_" , ""));
    }
    @Override
    public String getTask() { return task; }
    @Override
    public String getHint() { return hint; }

///////// PRIVATE ///////////////

    private void createTask(){

        Random random = new Random( new Date().getTime() );

        if( answer.isEmpty() ){
            answer = answerSet.get( random.nextInt( answerSet.size() ) );

            wordFrequency.replace( answer , wordFrequency.get(answer) + 1);

            average = 0;
            //System.out.println( "answer is empty" );
        }else{
            String previousAnswer = answer;
            while( answer.equals(previousAnswer) || wordFrequency.get(answer) > average)  {
                answer = answerSet.get( random.nextInt( answerSet.size() ) );
            }

            wordFrequency.replace( answer , wordFrequency.get(answer) + 1);

            //calculate average usage of words
            average = 0;
            for( int i : wordFrequency.values() ) average += (double) i / (double) wordFrequency.size();
        }

        task   = prb.getString(answer);

        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(task);

        if(matcher.find()) {
            //System.out.println(matcher.group(0));
            hint = matcher.group(0)
            ;
            task = task.replaceAll( matcher.group(0) , "" )
                    .replaceAll("[()]" , "")
            ;
        }
        else hint = "";

        //System.out.println("createTask");
    }

    private void setAnswerSet(){

        try {

            InputStreamReader isr = new InputStreamReader(
                    ClassLoader.getSystemResourceAsStream("Words.properties"), StandardCharsets.UTF_8
                    );
            prb = new PropertyResourceBundle( isr );
            answerSet = new ArrayList<>( prb.keySet() ) ;
            answerSet.forEach( word -> wordFrequency.put(word,0) );
        } catch (IOException e) { e.printStackTrace(); }


    }
}
