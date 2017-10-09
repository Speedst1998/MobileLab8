package cs.dawson.myapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tahar on 2017-10-01.
 */

public class Quiz {

    private int questionCounter = 0;
    private int currentQuestion= 0;
    private int numOfCorrectAnswers = 0;
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
        Log.d("Questions",questionNumbers.toString() );

        while(notFound) {

            questionNum = new Random().nextInt(this.questionNumbers.size()) + 1 ;
            Log.d("Questions",questionNum+"");
            if(!(questionNumbers.get(questionNum-1).equals("answered"))) {
                Log.d("Questions",questionNumbers.toString() );
                notFound = false;
                questionNumbers.set(questionNum-1, "answered");
            }
        }
        this.currentQuestion = questionNum;
        return questionNum;
    }
    public void setCurrentQuestion(int q){
        this.currentQuestion= q;
    }
    public int getCurrentQuestion(){
        return this.currentQuestion;
    }
    public void addPoint(){
        this.numOfCorrectAnswers++;
    }
    public int getScore(){
        return this.numOfCorrectAnswers/questionCounter;
    }
    public void addToQuestionCounter(){
        this.questionCounter++;
    }
    public int getQuestionCounter(){
        return this.questionCounter;
    }
    public int getNumOfCorrectAnswers(){return this.numOfCorrectAnswers;}

}
