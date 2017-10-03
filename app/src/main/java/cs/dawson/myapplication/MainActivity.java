package cs.dawson.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("About exectution","Testing if execution makes it this far in the main method");
        setContentView(R.layout.activity_main);
    }

    public void startQuiz(View v){
        Intent intent = new Intent(this,QuizActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void launchAboutActivity(View v){
        Intent i = new Intent(this, AboutActivity.class);

            Log.d("About exectution","Testing if execution amkes it this far in the about method");
            startActivity(i);

    }
}
