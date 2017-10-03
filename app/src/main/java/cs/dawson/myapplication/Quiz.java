package cs.dawson.myapplication;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tahar on 2017-10-01.
 */

public class Quiz {
    private int correctAnswers = 0;
    private int score = 0;
    private int questionCounter = 0;
    private ArrayList<String> questionNumbers;

    public Quiz(ArrayList<String> questions){
        this.questionNumbers = questions;
    }

    public ArrayList<String> getQuestions(){
        return this.questionNumbers;
    }

    /**
     * This method chooses the number of the question to be displayed
     * and removes the question from the questions array so it is not
     * chosen again
     * @return
     */
    public int chooseQuestion(){

        boolean notFound = true;
        int questionNum = 0;

        while(notFound) {

            questionNum = new Random().nextInt(this.questionNumbers.size()) + 1 ;

            if(!(questionNumbers.get(questionNum-1).equals("answered"))) {
                notFound = false;
                questionNumbers.set(questionNum, "answered");
            }
        }
        return questionNum;
    }

}
