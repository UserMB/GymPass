package com.example.jakubkurtiak.gympass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// -----------------------------------------
// Menu Activity is main page of the app once successfully
// registered. It forwards to further activities:
// -----------------------------------------

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If App is not registered than finish MenuActivity and go to MainActivity
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }

        setContentView(R.layout.activity_menu);

        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.top,R.string.gympass);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.your_pass,R.string.your_pass);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.and_more,R.string.and_more);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonShowPass,R.string.your_pass_button);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonReadNews,R.string.button_read_news);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonContact,R.string.button_contact_us);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonShare,R.string.button_share);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonVideos,R.string.button_videos);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonWorkout,R.string.button_workout);
        CommonMethods.setImpactFont(MenuActivity.this,R.id.buttonDeregister,R.string.button_deregister);
    }

    @Override
    public void onBackPressed() {
        finish();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        //Toast.makeText(MenuActivity.this,"Back",Toast.LENGTH_SHORT).show();
    }

    // All the code below managing life cycles is added to avoid showing up MenuActivity and
    // DeregisterActivity after app is deregistered.

    public void onResume() {
        super.onResume();
        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }

        //Toast.makeText(MenuActivity.this,"onResume",Toast.LENGTH_SHORT).show();
    }

    public void onPause() {
        super.onResume();
        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }

        //Toast.makeText(MenuActivity.this,"onPause",Toast.LENGTH_SHORT).show();
    }

    public void onStop() {
        super.onResume();
        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }
        //Toast.makeText(MenuActivity.this,"onStop",Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        super.onResume();
        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }
        //Toast.makeText(MenuActivity.this,"onDestroy",Toast.LENGTH_SHORT).show();
    }

    public void onRestart() {
        super.onResume();
        CommonMethods.awardBadge(MenuActivity.this, R.id.badge);
        if (!CommonMethods.isAppRegistered(MenuActivity.this)) {
            Intent moveToMainActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(moveToMainActivity);
            MenuActivity.this.finish();
        }
        //Toast.makeText(MenuActivity.this,"onRestart",Toast.LENGTH_SHORT).show();
    }

    // Click Open Gym PassActivity to view pass
    protected void clickOpenPass(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,PassActivity.class);
        startActivity(clickOpen);
    }

    // Click ReadActivity to read news
    protected void clickReadNews(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,ReadNewsActivity.class);
        startActivity(clickOpen);
    }

    // Click Contact Us
    protected void clickContactUs(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,ContactUsActivity.class);
        startActivity(clickOpen);
    }

    // Click Share
    protected void clickShare(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,ShareActivity.class);
        startActivity(clickOpen);
    }

    // Click Share
    protected void clickVideos(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,VideosActivity.class);
        startActivity(clickOpen);
    }

    // Click Workout
    protected void clickWorkout(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,WorkoutActivity.class);
        startActivity(clickOpen);
    }
    // Click Deregister
    protected void clickDeregister(View view) {
        Intent clickOpen = new Intent(MenuActivity.this,DeregisterActivity.class);
        startActivity(clickOpen);
    }
}
