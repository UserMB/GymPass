package com.example.jakubkurtiak.gympass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class ContactUsActivity extends AppCompatActivity {

    // -----------------------------------------
    // Contact Us activity is a point of contact with the gym.
    // Customer can:
    // - Find gym on the map
    // - Call gym
    // - Email gym
    // -----------------------------------------

    private static final int REQUEST_CALL = 1;
    Intent callIntent;
    ImageButton mCallButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    ImageButton callButton, mapButton, emailButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        //Display the Gympass logo and header/text fonts
        CommonMethods.awardBadge(ContactUsActivity.this, R.id.badge);
        CommonMethods.setImpactFont(ContactUsActivity.this, R.id.top, R.string.gympass);
        CommonMethods.setImpactFont(ContactUsActivity.this, R.id.contact_us_header, R.string.contact_us_header);
        CommonMethods.setImpactFont(ContactUsActivity.this, R.id.news_content, R.string.gym_address);

        setContactContent();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setContactContent() {
        TextView view = (TextView) findViewById(R.id.news_content);
        view.setText(R.string.gym_address);
        view.setTextColor(getResources().getColor(R.color.white));

        //view.setTextColor(getResources().getColor(R.color.black));

        init();

        mapButton = (ImageButton) findViewById(R.id.imageButtonMap);

    }

    //The following code below was taken from androidmaster.info to allow calls to be made on API 23
    //using Self Check Permission for ACTION_CALL ,PHONE_CALL
    //http://www.androidmaster.info/marshmallow-6-0-self-check-permission-for-calling-a-number-example/
    private void init(){
        mCallButton= (ImageButton) findViewById(R.id.buttonCall);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIntent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:(01) 716 3800 "));
                if (ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ContactUsActivity.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                }else {
                    startActivity(callIntent);
                }
            }
        });
    }
    //The following code was taken from androidmaster to allow calls to be made on API 23
    //Using Self Check Permission for ACTION_CALL ,PHONE_CALL
    //http://www.androidmaster.info/marshmallow-6-0-self-check-permission-for-calling-a-number-example/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CALL:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startActivity(callIntent);
                }else{
                    ////
                }
            }
        }
    }


    //`Loads the maps action
    public void onClickMap(View view) {

        //launches the map intent with the set Uri location from google maps
        Intent intent = new Intent(Intent.ACTION_VIEW,
                //uri tag from google maps
                Uri.parse("https://www.google.ie/maps/place/UCD+Sport+and+Fitness/@53.3081152,-6.2303547,17z/data=!4m5!3m4!1s0x4867093667320733:0x792c4381232c6b96!8m2!3d53.308112!4d-6.228166"));
        startActivity(intent);

    }


    //Lunches the code below after images button pressed
    public void onClickEmail(View View) {


        //launches the email which is stored on our phone
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //Email address to receiver
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{" fitness@ucd.ie"});
        //Holds the subject of the emails
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gym Query");
        //Holds the subject of the email messsage
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi ");
        //set the action send to data type emails
        emailIntent.setType("message/rfc822");
        try {
            startActivity(emailIntent);
        } catch (Exception e) {
            System.out.print("There is no email client installed.");
        }


    }

}