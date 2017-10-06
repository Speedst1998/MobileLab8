package cs.dawson.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 1534979 on 9/29/2017.
 */


public class QuizActivity extends AppCompatActivity {

    ImageButton image1;
    ImageButton image2;
    ImageButton image3;
    ImageButton image4;
    Quiz quiz;
    int rightAnswerPos =0;
    ArrayList<String> imagesToSet;

    ArrayList<ImageButton> choices;
    TextView questionString;
    TextView score;
    TextView completed;
    Button next;
    boolean secondTry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //image420dab
        score = (TextView) findViewById(R.id.scoreNum);
        completed = (TextView) findViewById(R.id.completeQuestions);
        next = (Button) findViewById(R.id.nextButton);
        //get a handle on the image buttons
        image1 = (ImageButton)findViewById(R.id.image1);
        image2 = (ImageButton)findViewById(R.id.image2);
        image3 = (ImageButton)findViewById(R.id.image3);
        image4 = (ImageButton)findViewById(R.id.image4);


        choices = new ArrayList<>();
        choices.add(image1);
        choices.add(image2);
        choices.add(image3);
        choices.add(image4);

        this.questionString = (TextView)findViewById(R.id.question);
        //Here i represent the images i need to set in an array
        imagesToSet = new ArrayList<String>();
        populateImageArray(imagesToSet);

        //Here are stored the positions so I can keep track of which ones are already set

        Log.d("Cycle","CREATE IN PROGRESS");
        //Here I randomly choose the question to display
        quiz = new Quiz(generateQuestions());
        setNextQuestion();

        Log.d("Cycle","CREATE DONE");


    }

    /**
     * this method sets the next question page
     */
    private void setNextQuestion(){
        int questionNum = quiz.chooseQuestion();
        int questionId = getResources().getIdentifier("question"+questionNum, "string",
                getPackageName());
        this.questionString.setText(getResources().getString(questionId));
        setImages(questionNum);
    }

    /**
     * this method is called when the next question button is clicked to set the next series of pictures
     * and questions
     * @param v
     */
    public void setNextQuestion(View v){
        this.next.setVisibility(View.INVISIBLE);
        setNextQuestion();
    }

    private void populateImageArray(ArrayList<String> imagesToSet){
        Field[] drawablesFields = R.drawable.class.getFields();

        for (Field field : drawablesFields) {
            try {
                if(field.getName().startsWith("question")||field.getName().startsWith("random")) {
                    Log.i("GETDRAWABLES", "R.drawable." + field.getName());
                    imagesToSet.add(field.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method sets the pictures in their appropriate cells while making sure they are not repeated
     * and setting right answer
     * @param question
     */
    private void setImages(int question){
        ArrayList<String> positions = new ArrayList<String>();
        positions.add("0");
        positions.add("1");
        positions.add("2");
        positions.add("3");
        int pos = new Random().nextInt(positions.size()) ;
        this.quiz.setCurrentQuestion(question);
        this.rightAnswerPos = pos;
        Log.d("Setting", "Position before setting in position array " + pos);
        positions.set(pos,"set");
        Log.d("Setting", "Position before setting in images to set array " + pos);
        //remove image from array so its not reused
        imagesToSet.remove("question"+question+"image");
        Log.d("Setting", "Position " + pos);
        int imageId;
        switch(question+""){
            case "1":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                Log.d("Setting", question+ " 1");
                choices.get(pos).setImageResource(imageId);
                break;
            case "2":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setImageResource(imageId);
                Log.d("Setting", question+ " 2");
                break;
            case "3":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setImageResource(imageId);
                Log.d("Setting", question+ " 3");
                break;
            case "4":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setImageResource(imageId);
                Log.d("Setting", question+ " 4");
                break;
        }

        for(String p : positions){
            if (!(p.equals("set"))) {
                Log.d("Setting", " Other Position " + p);
                //Select a random pic that has not been used for this question
                Log.d("ImageArray", imagesToSet.size()+"");
                int rndPic = new Random().nextInt(imagesToSet.size())  ;
                //pull its name from the available images array
                String newPic = imagesToSet.get(rndPic);
                imagesToSet.remove(newPic);
                Log.d("Setting", " Image Position " + rndPic);
                //this allows me to get the drawable by only using its name
                //this way I keep the code general instead of hardcoding the names of the images
                imageId = getResources().getIdentifier(newPic, "drawable",
                        getPackageName());
                choices.get(Integer.parseInt(p)).setImageResource(imageId);
            }
        }

    }

    private ArrayList<String> generateQuestions(){

        Field[] fields = R.drawable.class.getFields();
        ArrayList<String> questions = new ArrayList<String>();
        int numOfQuestions = 0;
        //here I get the number of questions we have in the quiz
        //by counting the question pictures
        for(Field field : fields){
            if(field.getName().startsWith("question")){
                numOfQuestions++;
                questions.add(numOfQuestions+"");
            }
        }

        return questions;
    }


    public void checkAnswer(View v){
        //Get the object that is concerned by the event
        ImageButton img = (ImageButton) v;
        //get the id of the imagebutton (the one from the layout)
        String imgname = getResources().getResourceEntryName(img.getId());
        //get the number at the end of the id to be able to determine position
        String imgnum = imgname.substring(imgname.length()-1);
        Log.d("AnswerCheck", imgnum);
        //position is 0 based while the id is 1 based so i substract one from the id to get
        //the position
        int imgpos = Integer.parseInt(imgnum)-1;
        if(this.rightAnswerPos == imgpos){
            Log.d("AnswerCheck", "Right answer");
            quiz.addPoint();;
            quiz.addToQuestionCounter();
            this.completed.setText(getResources().getString(R.string.completed) +" "+ quiz.getQuestionCounter());
            img.setBackgroundColor(0xFF0000FF);
            this.next.setVisibility(View.VISIBLE);

        }
        else{
            if(secondTry) {
                quiz.addToQuestionCounter();
                this.secondTry = false;
                img.setImageResource(R.drawable.wrong);
                this.completed.setText(getResources().getString(R.string.completed) +" "+ quiz.getQuestionCounter());
                choices.get(this.rightAnswerPos).setBackgroundColor(0xFF0000FF);
                this.next.setVisibility(View.VISIBLE);
            }
            else{
                img.setImageResource(R.drawable.wrong);
                this.secondTry = true;
            }

        }
    }
}
