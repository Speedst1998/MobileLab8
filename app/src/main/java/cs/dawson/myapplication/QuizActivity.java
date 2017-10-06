package cs.dawson.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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
    ArrayList<String> imagesToSet;
    ArrayList<String> positions = new ArrayList<String>();
    ArrayList<ImageButton> choices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

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

        //Here i represent the images i need to set in an array
        imagesToSet = new ArrayList<String>();
        populateImageArray(imagesToSet);

        //Here are stored the positions so I can keep track of which ones are already set
        positions.add("0");
        positions.add("1");
        positions.add("2");
        positions.add("3");
        Log.d("Cycle","CREATE IN PROGRESS");
        //Here I randomly choose the question to display
        quiz = new Quiz(generateQuestions());

        int questionNum = quiz.chooseQuestion();
        setImages(questionNum);
        Log.d("Cycle","CREATE DONE");


    }

    private void populateImageArray(ArrayList<String> imagesToSet){
        Field[] drawablesFields = R.drawable.class.getFields();

        for (Field field : drawablesFields) {
            try {
                if(field.getName().startsWith("question")) {
                    Log.i("GETDRAWABLES", "R.drawable." + field.getName());
                    imagesToSet.add(field.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void setImages(int question){
        int pos = new Random().nextInt(positions.size()) ;
        Log.d("Setting", "Position before setting in position array " + pos);
        positions.set(pos,"set");
        Log.d("Setting", "Position before setting in images to set array " + pos);
        imagesToSet.remove("question"+question+"image");
        Log.d("Setting", "Position " + pos);
        int imageId;
        switch(question+""){
            case "1":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                Log.d("Setting", question+ " 1");
                choices.get(pos).setBackground(getDrawable(imageId));
                break;
            case "2":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setBackground(getDrawable(imageId));
                Log.d("Setting", question+ " 2");
                break;
            case "3":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setBackground(getDrawable(imageId));
                Log.d("Setting", question+ " 3");
                break;
            case "4":
                imageId = getResources().getIdentifier("question"+question+"image", "drawable",
                        getPackageName());
                choices.get(pos).setBackground(getDrawable(imageId));
                Log.d("Setting", question+ " 4");
                break;
        }

        for(String p : positions){
            if (!(p.equals("set"))) {
                Log.d("Setting", " Other Position " + p);
                //Select a random pic that has not been used for this question
                int rndPic = new Random().nextInt(imagesToSet.size())  ;
                //pull its name from the available images array
                String newPic = imagesToSet.get(rndPic);
                imagesToSet.remove(newPic);
                Log.d("Setting", " Image Position " + rndPic);
                //this allows me to get the drawable by only using its name
                //this way I keep the code general instead of hardcoding the names of the images
                imageId = getResources().getIdentifier(newPic, "drawable",
                        getPackageName());
                choices.get(Integer.parseInt(p)).setBackground(getDrawable(imageId));
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
            Log.d("chooseQuestion",numOfQuestions+"");
            if(field.getName().startsWith("question")){
                Log.d("chooseQuestion","IF TRUE");
                numOfQuestions++;
                questions.add(numOfQuestions+"");
            }
        }

        return questions;
    }


    public void checkAnswer(View v){

    }
}
