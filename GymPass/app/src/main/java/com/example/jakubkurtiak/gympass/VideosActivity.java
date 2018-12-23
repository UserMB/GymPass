package com.example.jakubkurtiak.gympass;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;


public class VideosActivity extends AppCompatActivity {


// -----------------------------------------
// Safety and classes videos.
// -----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        // Populating text on screen elements.
        CommonMethods.awardBadge(VideosActivity.this, R.id.badge);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.top, R.string.gympass);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.videos, R.string.button_videos);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.classes_text, R.string.video_text_ucd_induction_video);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.classes_text1, R.string.video_text_pre_workout);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.classes_text2, R.string.video_text_bench_press);
        CommonMethods.setImpactFont(VideosActivity.this, R.id.classes_text3, R.string.video_text_ucd_marketing);
    }

    protected void playClassesVideo1(View view){
        playVideo("https://www.youtube.com/watch?v=FRfmw3jw2c4");
    }

    protected void playClassesVideo2(View view){
        playVideo("https://www.youtube.com/watch?v=nN8cwK3zM20");
    }

    protected void playClassesVideo3(View view){
        playVideo("https://www.youtube.com/watch?v=UaOwz6DNdjw");
    }

    protected void playClassesVideo4(View view){
        playVideo("https://www.youtube.com/watch?v=xZXrXIkGrx4");
    }

    // Simple intent to play YouTube video after clicking on an image/button.
    protected void playVideo(String video) {
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(video)));
    }
}