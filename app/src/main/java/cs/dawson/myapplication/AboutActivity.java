package cs.dawson.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * This is the About activity that will open when the about button is
 * pressed.The purpose of this activity is to instruct on how to play
 * the game and to show previous scores.
 * @author Keylen Alleyne
 */
public class AboutActivity extends AppCompatActivity {
    SharedPreferences prefs;
    Integer score1, score2;
    TextView result1, result2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        result1 = (TextView) findViewById(R.id.score1);
        result2 = (TextView) findViewById(R.id.score2);

        /**
         * Using shared preferences to load up the last two scores
         * If there wasn't anything saved in shared preferences, then
         * the default value will be -1, in which case I wont display
         * anything.
         */
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        score1 = prefs.getInt("score1", -1);
        Log.d("SCORE", "Check the value in prefs score 1: " + score1);
        score2 = prefs.getInt("score2",-1);
        Log.d("SCORE", "Check the value in prefs score 2: " + score2);

        if(score1 != -1)
            result1.setText(getString(R.string.hall_of_fame)+score1);

        if(score2 != -1)
            result2.setText(getString(R.string.hall_of_fame)+score2);
    }
}
