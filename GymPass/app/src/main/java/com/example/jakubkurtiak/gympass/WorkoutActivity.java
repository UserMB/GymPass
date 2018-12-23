package com.example.jakubkurtiak.gympass;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class WorkoutActivity extends AppCompatActivity {


// -----------------------------------------
// Workout, where user is encouraged to workout.
// -----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        CommonMethods.awardBadge(WorkoutActivity.this, R.id.badge);
        CommonMethods.setImpactFont(WorkoutActivity.this,R.id.top,R.string.gympass);
        CommonMethods.setImpactFont(WorkoutActivity.this,R.id.workout,R.string.button_workout);
        CommonMethods.setImpactFont(WorkoutActivity.this,R.id.buttonWorkoutGO,R.string.WO_initial);
    }

    // Click Workout
    // The idea behind this feature is to encourage app user to work out, moreover, the
    // text showing up on the screen should motivate to finish up whole exercise as planned.
    // The solution is simple, open "workout" activity and click button.
    // It will come up with series of motivating expressions.

    int numberOfClicks = 1;
    protected void goWork(View view) {
        // Reference:
        // All quotes taken from this website:
        // http://www.shape.com/fitness/workouts/18-inspirational-fitness-quotes-motivate-every-aspect-your-workout


        TextView goWorkFont = (TextView) findViewById(R.id.buttonWorkoutGO);
        Typeface font = Typeface.createFromAsset(WorkoutActivity.this.getAssets(), "fonts/impact.ttf");
        goWorkFont.setTypeface(font, Typeface.ITALIC);
        goWorkFont.setText(String.valueOf(numberOfClicks));


        if (numberOfClicks == 1) {
            goWorkFont.setText(R.string.WO_welcome);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 2) {
            goWorkFont.setText(R.string.WO_1);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 3) {
            goWorkFont.setText(R.string.WO_2);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 4) {
            goWorkFont.setText(R.string.WO_3);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 5) {
            goWorkFont.setText(R.string.WO_4);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 5) {
            goWorkFont.setText(R.string.WO_5);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 6) {
            goWorkFont.setText(R.string.WO_6);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 7) {
            goWorkFont.setText(R.string.WO_7);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 8) {
            goWorkFont.setText(R.string.WO_8);
            numberOfClicks += 1;
        }
        else if (numberOfClicks == 9) {
            goWorkFont.setText(R.string.WO_9);
            numberOfClicks += 1;
        } else {
            goWorkFont.setText(R.string.WO_end);
            numberOfClicks = 1;
        }
    }
}
