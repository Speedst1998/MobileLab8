package cs.dawson.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    Drawable defaultButtonBackground;
    ArrayList<String> imagesToSet;
    ArrayList<String> buttonBackgrounds = new ArrayList<String>();
    ArrayList<String> currentImages= new ArrayList<String>();
    ArrayList<ImageButton> choices;
    TextView questionString;
    TextView score;
    TextView completed;
    Boolean clickable = true;
    Button next;
    Boolean nextVisible = false;
    boolean secondTry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        currentImages.add("");
        currentImages.add("");
        currentImages.add("");
        currentImages.add("");
        buttonBackgrounds.add("white");
        buttonBackgrounds.add("white");
        buttonBackgrounds.add("white");
        buttonBackgrounds.add("white");
        //image420dab
        score = (TextView) findViewById(R.id.scoreNum);
        completed = (TextView) findViewById(R.id.completeQuestions);
        next = (Button) findViewById(R.id.nextButton);
        //get a handle on the image buttons
        image1 = (ImageButton)findViewById(R.id.image1);
        image2 = (ImageButton)findViewById(R.id.image2);
        image3 = (ImageButton)findViewById(R.id.image3);
        image4 = (ImageButton)findViewById(R.id.image4);

        defaultButtonBackground = image1.getBackground();
        choices = new ArrayList<>();
        choices.add(image1);
        choices.add(image2);
        choices.add(image3);
        choices.add(image4);

        this.questionString = (TextView)findViewById(R.id.question);
        //Here i represent the images i need to set in an array
        imagesToSet = new ArrayList<String>();
        Log.d("Cycle","Before shared Prefs");
        if(prefs != null){
            restorePrefs(prefs);
        }

        Log.d("Cycle","After shared Prefs");
        populateImageArray(imagesToSet);


        Log.d("Cycle","CREATE IN PROGRESS");
        //Here I randomly choose the question to display
        Log.d("Question", "Logging generate Questions" + generateQuestions());
        quiz = new Quiz(generateQuestions());
        restoreQuiz(prefs);
        setNextQuestion();

        Log.d("Cycle","CREATE DONE");
    }

    @Override
    protected void onPause(){
        super.onPause();
        savePreferences();
    }

    public void restoreQuiz(SharedPreferences prefs){
        ArrayList strings = new ArrayList<String>();
        Set<String> set;

        this.quiz.setNumOfCorrectAnswers(prefs.getInt("numOfCorrectAnswers", 0));
        Log.i("PREFS","Number of correct answers: "+ this.quiz.getNumOfCorrectAnswers());

        this.quiz.setQuestionCounter(prefs.getInt("questionCounter", 0));
        this.quiz.setCurrentQuestion(prefs.getInt("currentQuestion", 0));

        //Retrieve the question number

        ArrayList<String> list = new ArrayList<String>();
        list.addAll(generateQuestions());
        
        Set<String> set1 = new HashSet<String>();
        set1.addAll(list);



        set = prefs.getStringSet("questionNumbers",set1);
        strings.addAll(set);
        this.quiz.setQuestionNumbers(strings);

        Log.d("QuestionCounter", "This is the question counter number in restoreQuiz : " + this.quiz.getQuestionCounter() );
    }

    public void restorePrefs(SharedPreferences prefs){
        ArrayList strings = new ArrayList<String>();
        Set<String> set1, set2, set3;

        Log.i("PREFS","BEFORE Right answer position: "+ this.rightAnswerPos);
        this.rightAnswerPos = prefs.getInt("rightAnswerPos", 0);
        Log.i("PREFS","AFTER Right answer position: "+ this.rightAnswerPos);

        this.secondTry = prefs.getBoolean("secondTry", false);
        Log.i("PREFS","Is this the second try: "+ this.secondTry);


        //Retrieve the images to set
        set1 = prefs.getStringSet("imagesToSet", new HashSet<String>());
        this.imagesToSet.addAll(set1);

        //Retrieve the currentImages
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        list.add("");
        list.add("");
        list.add("");

        Set<String> setEmpty = new HashSet<String>();
        setEmpty.addAll(list);

        set2 = prefs.getStringSet("currentImages", setEmpty);
        this.currentImages.addAll(set2);

        //Retrieve the button backgrounds

        ArrayList<String> listBackground = new ArrayList<String>();
        list.add("white");
        list.add("white");
        list.add("white");
        list.add("white");

        Set<String> setBackground = new HashSet<String>();
        setBackground.addAll(listBackground);
        set3 = prefs.getStringSet("buttonBackground", setBackground);
        this.buttonBackgrounds.addAll(set3);


        this.clickable = prefs.getBoolean("clickable", true);
    }

    /**
     * this method sets the next question page
     */
    private void setNextQuestion(){
        //make next button invisible
        this.next.setVisibility(View.INVISIBLE);
        this.nextVisible=false;
        //set clickables
        for(ImageButton imgb : choices){
            imgb.setClickable(true);
        }
        //reset background state appropriately
        for(int i=0; i < buttonBackgrounds.size();i++){
            buttonBackgrounds.set(i,"white");
        }
        int questionNum = quiz.chooseQuestion();
        int questionId = getResources().getIdentifier("question"+questionNum, "string",
                getPackageName());
        Log.d("questionNum", "Question num is :" + questionNum);
        this.questionString.setText(getResources().getString(questionId));
        this.score.setText(getResources().getString(R.string.score) + this.quiz.getScore());
        Log.d("counters",quiz.getNumOfCorrectAnswers()+ " "+ quiz.getQuestionCounter()+" "+quiz.getScore());
        this.completed.setText(getResources().getString(R.string.completed) +" "+ quiz.getQuestionCounter()+ " "+getResources().getString(R.string.total));
        setImages(questionNum);
    }

    /**
     * this method is called when the next question button is clicked to set the next series of pictures
     * and questions
     * @param v
     */
    public void setNextQuestion(View v){
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

        imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                getPackageName());
        //save images and their positions to use in state
        currentImages.set(pos,"question"+question+"image");
        Log.d("Setting", question+ " 1");
        choices.get(pos).setImageResource(imageId);

        int i =0;
        for(String p : positions){
            //reset background colors
            if(p.equals("set")){
                choices.get(i).setBackground(this.defaultButtonBackground);
            }
            if (!(p.equals("set"))) {
                //save images and their positions to use in state

                //reset background colors
                choices.get(Integer.parseInt(p)).setBackground(this.defaultButtonBackground);
                Log.d("Setting", " Other Position " + p);
                //Select a random pic that has not been used for this question
                Log.d("ImageArray", imagesToSet.size()+"");
                int rndPic = new Random().nextInt(imagesToSet.size())  ;
                //pull its name from the available images array
                String newPic = imagesToSet.get(rndPic);
                imagesToSet.remove(newPic);
                currentImages.set(Integer.parseInt(p),newPic);
                Log.d("Setting", " Image Position " + rndPic);
                //this allows me to get the drawable by only using its name
                //this way I keep the code general instead of hardcoding the names of the images
                imageId = getResources().getIdentifier(newPic, "drawable",
                        getPackageName());
                choices.get(Integer.parseInt(p)).setImageResource(imageId);
            }
            i++;
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
        Log.d("QuestionCounter", "This is the question counter number in check answers : " + this.quiz.getQuestionCounter());
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
            //set image background for state
            this.buttonBackgrounds.set(imgpos, "blue");
            img.setBackgroundColor(0xFF0000FF);
            //set buttons unclickable
            for(ImageButton imgb : choices){
                imgb.setClickable(false);
                this.clickable = false;
            }
            this.next.setVisibility(View.VISIBLE);
            this.nextVisible=true;

        }
        else{
            if(secondTry) {
                quiz.addToQuestionCounter();
                this.secondTry = false;
                img.setImageResource(R.drawable.wrong);
                choices.get(this.rightAnswerPos).setBackgroundColor(0xFF0000FF);
                //save state
                this.buttonBackgrounds.set(this.rightAnswerPos, "blue");
                this.currentImages.set(imgpos,"wrong");
                this.next.setVisibility(View.VISIBLE);
                this.nextVisible=true;
                for(ImageButton imgb : choices){
                    imgb.setClickable(false);
                    this.clickable = false;
                }
            }
            else{
                //save state of wrong answer
                this.currentImages.set(imgpos,"wrong");
                img.setImageResource(R.drawable.wrong);
                this.secondTry = true;
            }

        }
        if(this.quiz.getQuestionCounter() == 4) {
            this.next.setVisibility(View.INVISIBLE);
            this.completed.setText(R.string.finished);
            this.nextVisible = false;

        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("numOfCorrectAnswers",this.quiz.getNumOfCorrectAnswers());
        savedInstanceState.putInt("questionCounter",this.quiz.getQuestionCounter());
        savedInstanceState.putInt("currentQuestion",this.quiz.getCurrentQuestion());
        savedInstanceState.putInt("rightAnswerPos",this.rightAnswerPos);
        savedInstanceState.putBoolean("secondTry",this.secondTry);
        savedInstanceState.putBoolean("clickable",this.clickable);
        savedInstanceState.putBoolean("nextVisible",this.nextVisible);
        savedInstanceState.putStringArrayList("imagesToSet",this.imagesToSet);
        savedInstanceState.putStringArrayList("buttonBackground",this.buttonBackgrounds);
        savedInstanceState.putStringArrayList("questionNumbers",this.quiz.getQuestions());
        savedInstanceState.putStringArrayList("currentImages",this.currentImages);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        this.rightAnswerPos = savedInstanceState.getInt("rightAnswerPos");
        this.quiz.setNumOfCorrectAnswers(savedInstanceState.getInt("numOfCorrectAnswers"));
        this.secondTry = savedInstanceState.getBoolean("secondTry");
        this.quiz.setQuestionCounter(savedInstanceState.getInt("questionCounter"));
        this.quiz.setCurrentQuestion(savedInstanceState.getInt("currentQuestion"));
        this.quiz.setQuestionNumbers(savedInstanceState.getStringArrayList("questionNumbers"));
        this.imagesToSet = savedInstanceState.getStringArrayList("imagesToSet");
        this.currentImages = savedInstanceState.getStringArrayList("currentImages");
        this.buttonBackgrounds = savedInstanceState.getStringArrayList("buttonBackground");
        this.clickable = savedInstanceState.getBoolean("clickable");
        this.score.setText(getResources().getString(R.string.score) + this.quiz.getScore());
        int questionId = getResources().getIdentifier("question"+this.quiz.getCurrentQuestion(), "string",
                getPackageName());
        this.score.setText(getResources().getString(R.string.score) + this.quiz.getScore());
        this.questionString.setText(getResources().getString(questionId));
        this.completed.setText(getResources().getString(R.string.completed) +" "+ quiz.getQuestionCounter()+ " "+getResources().getString(R.string.total));
        this.nextVisible = savedInstanceState.getBoolean("nextVisible");
        restoreImages(this.currentImages,this.buttonBackgrounds);
    }
    private void restoreImages(ArrayList<String> currentImages,ArrayList<String> buttonBackgrounds){
        int imageId =0;
        Log.d("restoring",buttonBackgrounds.toString());
        for(int i =0; i < choices.size(); i++){

            imageId = getResources().getIdentifier(this.currentImages.get(i), "drawable",
                    getPackageName());
            this.choices.get(i).setImageResource(imageId);
            if(this.buttonBackgrounds.get(i).equals("blue")){
                this.choices.get(i).setBackgroundColor(0xFF0000FF);
            }
        }
        //make sure to keep track of weither or not pics are clickable
        if(this.clickable){
            for(ImageButton imgb : choices){
                imgb.setClickable(true);
            }
        }
        else{
            for(ImageButton imgb : choices){
                imgb.setClickable(false);
            }
        }
        if(this.nextVisible){
            this.next.setVisibility(View.VISIBLE);
        }
        else
        {
            this.next.setVisibility(View.INVISIBLE);
        }
        Log.d("restoring",this.currentImages.toString());
    }


    public void showHint(View view) {
        String query = getResources().getString(R.string.road_sign)+" ";

        switch(quiz.getCurrentQuestion()){
            case 1:
                query += getResources().getString(R.string.question1);
                break;
            case 2:
                query += getResources().getString(R.string.question2);
                break;
            case 3:
                query += getResources().getString(R.string.question3);
                break;
            case 4:
                query += getResources().getString(R.string.question4);
                break;
        }

        Log.i("hint","Description to search: "+ query);
        Log.d("hint","The returning question number : "+ quiz.getCurrentQuestion());
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        query="";
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void savePreferences(){
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("score1",this.quiz.getScore());

        Log.d("SCORE", "Saved score1 as : " + this.quiz.getScore());
        if(this.quiz.getScore() != 0) {
            editor.putInt("score2", prefs.getInt("score1", 0));
        }
        editor.putInt("numOfCorrectAnswers",this.quiz.getNumOfCorrectAnswers());
        editor.putInt("questionCounter",this.quiz.getQuestionCounter());
        editor.putInt("currentQuestion",this.quiz.getCurrentQuestion());
        editor.putInt("rightAnswerPos",this.rightAnswerPos);
        editor.putBoolean("secondTry",this.secondTry);
        editor.putBoolean("clickable",this.clickable);
        editor.putBoolean("nextVisible",this.nextVisible);


        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        Set<String> set3 = new HashSet<String>();
        Set<String> set4 = new HashSet<String>();
        //Save the images to be set

        set1.addAll(this.imagesToSet);
        editor.putStringSet("imagesToSet", set1);

        //editor.putStringArrayList("imagesToSet",this.imagesToSet);

        //Save the button backgrounds to be set

        set2.addAll(this.buttonBackgrounds);
        editor.putStringSet("buttonBackground", set2);

        //editor.putStringArrayList("buttonBackground",this.buttonBackgrounds);
        Log.d("Questionss", "Get questions returns" + this.quiz.getQuestions());
        //Save the question numbers to be set

        set3.addAll(this.quiz.getQuestions());
        editor.putStringSet("questionNumbers", set3);

        //editor.putStringArrayList("questionNumbers",this.quiz.getQuestions());

        //Save the current images to be set

        set4.addAll(this.currentImages);
        editor.putStringSet("currentImages", set4);

        //editor.putStringArrayList("currentImages",this.currentImages);
        editor.commit();
        Log.d("SCORE", "After the commit");

    }
}
