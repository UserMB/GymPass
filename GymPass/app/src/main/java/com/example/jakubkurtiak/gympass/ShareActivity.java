package com.example.jakubkurtiak.gympass;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


// -----------------------------------------
// User can share status (currently at the gym) to social network.
// -----------------------------------------

public class ShareActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        CommonMethods.awardBadge(ShareActivity.this, R.id.badge);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.top, R.string.gympass);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.share_now, R.string.share_now);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.share_now_top_text, R.string.share_top_textbox);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.button, R.string.button_share_checkin);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.button2, R.string.button_activate_voice_control);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.share_now_bottom_text, R.string.share_bottom_textbox);
        CommonMethods.setImpactFont(ShareActivity.this, R.id.button3, R.string.button_share_programme);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    // Constant to use for the request coded needed for VC
    public static final int VC_REQUEST_CODE = 7777;

    // Accessor method for the String.xml object, returning a String: Intent.createChooser can't accept int from R.id.string
    private String getChooseAppString() {
        return getString(R.string.share_to_app);

    }

    public void shareStatus(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String msgToShare = "Working out at the gym - sent from GymPass";
        shareIntent.putExtra(Intent.EXTRA_TEXT, msgToShare);
        startActivity(Intent.createChooser(shareIntent, getChooseAppString()));
    }

    public void shareUpdate(View view) {
       startVoiceRecognitionActivity();
    }

    public void shareAll(View view) {
        Intent shareAllIntent = new Intent(Intent.ACTION_SEND);
        shareAllIntent.setType("text/plain");
        int totalVisits = CommonMethods.readNumberOfVisits(ShareActivity.this);
        String durationSinceFirstSession = periodSinceFirstVisit(ShareActivity.this);
        String sessions;
        sessions = (totalVisits == 1) ? "session" : "sessions";

        String msgToShareAll = "I've done a total of " + totalVisits + " " + sessions + " since I started my programme " + durationSinceFirstSession + " ago!";
        shareAllIntent.putExtra(Intent.EXTRA_TEXT, msgToShareAll);
        startActivity(Intent.createChooser(shareAllIntent, getChooseAppString()));
    }

    // Inspired by a good SO answer at: stackoverflow.com/questions/11798337/how-to-voice-commands-into-an-android-application
    public void startVoiceRecognitionActivity() {
        Intent vcIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        vcIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Put this into strings.xml. Interpolation: check Jakub's code DeregisterActivity.setAreYouSure
        vcIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say \"Status\" or \"All\" to share which update version you'd prefer:");
        startActivityForResult(vcIntent, VC_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VC_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matchedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String listOfWords = matchedWords.get(0);
            int count = 0;
            for (String splitUp: listOfWords.split(" ")) { count++; }
            String [] splitMatches = new String[count];
            int splitMatchesIndex = 0;
            for (String singleWord: listOfWords.split(" ")) {
                splitMatches[splitMatchesIndex] = singleWord;
                splitMatchesIndex++;
            }

            if (matchedWords.contains("status") || matchedWords.contains("stats") || matchedWords.contains("stars")) {
                Button btn = (Button) findViewById(R.id.button);
                shareStatus(btn);
            }
            else if (matchedWords.contains("all") || matchedWords.contains("oil") || matchedWords.contains("owl") || matchedWords.contains("call")) {
                Button btn = (Button) findViewById(R.id.button2);
                shareAll(btn);
            }

            else {
                // open a basic TextView with list of matched words
                setContentView(R.layout.array_words);
                setContenttoListArrayWords(listOfWords);
            }
        }

    }

    private void setContenttoListArrayWords(String listOfWords) {
        TextView view = (TextView) findViewById(R.id.array_words_content);
        view.setText("GymPass didn't quite catch that... Click the back button to try again!\n\nThe words Android Voice-Recognition picked up are as follows:\n\n\n" + listOfWords + "\n\n");
        view.setTextColor(getResources().getColor(R.color.black));
    }

    // Customised DB methods, with accessibility to the methods set as: private to this Class

    private static String periodSinceFirstVisit(Activity activity) {
        // Get the time of last visit and compare to current time.

        Cursor cursorNoVisits = CommonMethods.readTableToCursor(activity, "tbGymPassCustomerVisits");
        cursorNoVisits.moveToFirst();

        int indexTime = cursorNoVisits.getColumnIndex("timestamp");
        String time = cursorNoVisits.getString(indexTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date dateOfFirstVisit = null;
        try {
            dateOfFirstVisit = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cursorNoVisits.close();

        // To calculate the difference between NOW and LAST VISIT, Date must be changed
        // into long. Then time of last visit is subtracted from actual time which
        // gives us the actual time since last visit.
        // This time is in seconds, so must be changed to human readable format.
        long dateTimeNowInSeconds = new Date().getTime() / 1000;
        long dateTimeFirstVisitInSeconds = dateOfFirstVisit.getTime() / 1000;
        long timeSinceLastVisitInSeconds = dateTimeNowInSeconds - dateTimeFirstVisitInSeconds;
        int daysSinceFirstVisit = (int) (timeSinceLastVisitInSeconds / 86400);

        // Returns a String of how long since user's first gym session, formatted in days or weeks.
        String formattedPeriodSinceFirstVisit;

            if (daysSinceFirstVisit == 1) {
                formattedPeriodSinceFirstVisit = "1 day";
            } else if (daysSinceFirstVisit < 7) {
                formattedPeriodSinceFirstVisit = daysSinceFirstVisit + " days";
            } else if (daysSinceFirstVisit == 7) {
                formattedPeriodSinceFirstVisit = "1 week";
            } else {
                int weeksSinceFirstVisit = daysSinceFirstVisit / 7;
                formattedPeriodSinceFirstVisit = weeksSinceFirstVisit + " weeks";
            }

        return formattedPeriodSinceFirstVisit;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Share Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}