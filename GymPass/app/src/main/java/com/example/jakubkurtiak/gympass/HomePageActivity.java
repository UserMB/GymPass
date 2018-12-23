package com.example.jakubkurtiak.gympass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;



public class HomePageActivity extends AppCompatActivity {

    // -----------------------------------------
    // This is handling the initial round logo button screen.
    // -----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);

        //Set Delay Time
        int DelayText = 1000;

        //Creating handler object on main thread
        Handler handler = new Handler();
        //Creating new runnable object
        handler .postDelayed(new Runnable() {

            @Override
            //Runs the DisplayText method
            //This text will be displayed on main thread after 10seconds
            public void run() {

                Toast.makeText(HomePageActivity.this," Click the on Logo to Begin ", Toast.LENGTH_LONG).show();
            }

        },DelayText);

    }

    //Activates the MainActivity
    public void nextPageClick(View v)
    {

        Intent i = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(i);
    }



}
