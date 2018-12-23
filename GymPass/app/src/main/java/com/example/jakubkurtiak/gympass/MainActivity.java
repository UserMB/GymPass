package com.example.jakubkurtiak.gympass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

// -----------------------------------------
// Main Activity is the initial screen where gym customer activates the app. The process is as follows:
// -    Connects to gym server (we use mock server) and downloads customer details,gym location and qr code
// -    Stores them locally in SqlLite
//
// Activation is mandatory, once activated this page will no longer appear, instead MenuActivity appears.
// -----------------------------------------

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If App is registered than skip to MenuActivity
        if (CommonMethods.isAppRegistered(MainActivity.this)) {
            Intent moveToMenuActivity = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(moveToMenuActivity);
            MainActivity.this.finish();
        }

        setContentView(R.layout.activity_main);

        CommonMethods.setImpactFont(MainActivity.this,R.id.top,R.string.gympass);
        CommonMethods.setImpactFont(MainActivity.this,R.id.member_login,R.string.member_login);
        CommonMethods.setImpactFont(MainActivity.this,R.id.member_password,R.string.member_password);
        CommonMethods.setImpactFont(MainActivity.this,R.id.email_sign_in_button,R.string.register);

    }

    @Override
    public void onBackPressed() {
        finish();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Login is validated on the server, JSON mock is setup for
    // demo purposes: https://demo3966630.mockable.io/gympass_reg
    // It contains login/pass, barcode and gym location

    private class getGymPassDetails extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {

            String jsonLoginCredentials = "";
            StringBuilder sbGymPass = new StringBuilder();
            HttpURLConnection urlGymPassConnection = null;
            BufferedReader readerGymPass = null;
            String urlGymPass = "https://demo3966630.mockable.io/gympass_register";

            try {

                URL url = new URL(urlGymPass);

                // Connect to GymServer (using mock)
                urlGymPassConnection = (HttpURLConnection) url.openConnection();
                urlGymPassConnection.setRequestMethod("GET");
                urlGymPassConnection.connect();

                // Read the input stream into ArrayList
                InputStream inputGymPassStream = urlGymPassConnection.getInputStream();

                readerGymPass = new BufferedReader((new InputStreamReader(inputGymPassStream)));
                String line = "";
                while ((line = readerGymPass.readLine()) != null) {
                    sbGymPass.append(line);
                    if (isCancelled()) break;
                }

                jsonLoginCredentials = sbGymPass.toString();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonLoginCredentials;
        }


        protected void onPostExecute(String result) {

            // Define JSON objects
            JSONObject jsonGymPasCredentials = null;
            JSONArray gymMembersArray = null;
            JSONObject gymLocation = null;
            JSONObject gymMember = null;
            JSONArray gymNewsArray = null;
            JSONObject gymNews = null;
            String gymLatitude;
            String gymLongitude;


            // Getting inputs from text fields
            EditText user = (EditText) findViewById(R.id.username);
            EditText pass = (EditText) findViewById(R.id.password);
            String username = user.getText().toString();
            String password = pass.getText().toString();

            try {
                //Parsing String and changing into JSON Object
                jsonGymPasCredentials = new JSONObject(result);
                // Getting all gym members into Array
                gymMembersArray = jsonGymPasCredentials.getJSONArray("gymmember");
                // Getting gym location into Array
                //gymLocation = jsonGymPasCredentials.get("")      // = jsonGymPasCredentials.getJSONArray("gymlocation");
                gymLocation = jsonGymPasCredentials.getJSONObject("gymlocation");
                gymNewsArray = jsonGymPasCredentials.getJSONArray("gymnews");


                gymLatitude = String.valueOf(gymLocation.get("latitude"));
                gymLongitude = String.valueOf(gymLocation.get("longitude"));

                //gymLatitude = String.valueOf(gymLocation.getJSONObject(0).get("latitude"));
                //gymLongitude = String.valueOf(gymLocation.getJSONObject(0).get("longitude"));

                int j;

                for (j = 0; j < gymNewsArray.length(); j++) {
                    gymNews = gymNewsArray.getJSONObject(j);

                    createOpenGymPassDatabase();

                    String saveDbHeader = String.valueOf(gymNews.get("news_header"));
                    String saveDbContent = String.valueOf(gymNews.get("news_content"));

                    storeNewsDetailsInDataBase(saveDbHeader, saveDbContent);


                }

                // Looping through all logins
                int i;
                for (i = 0; i < gymMembersArray.length(); i++) {
                    gymMember = gymMembersArray.getJSONObject(i);

                    if (username.equals(gymMember.get("login")) && password.equals(gymMember.get("password"))) {

                        createOpenGymPassDatabase();

                        String saveDbLogin = String.valueOf(gymMember.get("login"));
                        String saveDbPass = String.valueOf(gymMember.get("password"));
                        Integer saveDbBarcode = Integer.valueOf((gymMember.get("barcode")).toString());
                        String saveDbLocation = "'"+gymLatitude + "," + gymLongitude+"'";

                        // Login & password are valid so details are stored
                        // in the database.

                        storeMemberDetailsInDataBase(saveDbLogin, saveDbPass, saveDbBarcode, saveDbLocation);

                        // Login is stored using SharedPreferences, it will
                        // be used to be displayed in other activities.
                        saveAppSharedPrefLogin(saveDbLogin);

                        // Storing first visit in the database, this is because app was crashing on other
                        // method in PassActivity. There is a logic in the calculations of visits to
                        // not take this one row into consideration.
                        storeGymVisitInDB("login",CommonMethods.currentDate());

                        // Below read is just to throw Toast to read data from database
                        // and check if they were properly stored
                        //readLoginPassGymPassDatabase(); //remove!!!

                        // Another important task here is to store data in the database.
                        // Member username, name, last name and password are stored.
                        // In addition, gym location (used in other activities)
                        // Finally, gym barcode identifier. It will be later used
                        // in PassActivity.

                        // Go to next page if credentials valid
                        Intent goToMenu = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(goToMenu);
                        finish();

                        break;

                    } else {
                        // To be added, Toast with prompt and counter to check if
                        // three attempts passed, if yes, then reload app.

                        // Reload activity if invalid
                        finish();
                        startActivity(getIntent());
                        //Toast.makeText(MainActivity.this, ("Incorrect login or password, please try again."), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loginGymPass (View view){

        // Calling AsyncTask to execute login process.
        new getGymPassDetails().execute();
    }

    protected void createOpenGymPassDatabase() {

        // Create database and store details pulled from the server
        SQLiteDatabase dbGymPass = null;
        String tableName = "tbGymPassCustomer";
        String tableNameVisits = "tbGymPassCustomerVisits";
        String tableNews = "tbGymPassNews";


        try {
            dbGymPass = openOrCreateDatabase("sqlGymPass", MODE_PRIVATE, null);
            dbGymPass.execSQL("DROP TABLE IF EXISTS " + tableName);
            dbGymPass.execSQL("CREATE TABLE IF NOT EXISTS "
                    + tableName
                    + " (login VARCHAR, password VARCHAR, barcode INTEGER, gymlocation VARCHAR)");
            //dbGymPass.execSQL("DROP TABLE IF EXISTS " + tableNameVisits);
            dbGymPass.execSQL("CREATE TABLE IF NOT EXISTS "
                    + tableNameVisits
                    + " (login VARCHAR, timestamp DATETIME)");
            dbGymPass.execSQL("CREATE TABLE IF NOT EXISTS "
                    + tableNews
                    + " (news_header VARCHAR, news_content VARCHAR)");
        } catch (Exception e) {
            Log.e("DB_create_error", "Database creation error");
        }
    }

    protected SQLiteDatabase openGymPassDatabase (SQLiteDatabase db) {
        try {
            db = openOrCreateDatabase("sqlGymPass", MODE_PRIVATE, null);
        } catch (Exception e) {
            Log.e("DB_read_error", "Database read error");
        }
        return db;
    }

    protected void storeMemberDetailsInDataBase(String login, String password, int barcode, String gymlocation) {

        // Populate database with customer data
        String tableName = "tbGymPassCustomer";

        openGymPassDatabase(null).execSQL("INSERT INTO "
                + tableName
                + " (login, password, barcode, gymlocation)"
                + " VALUES ('"+login+"', '"+password+"',"+barcode+", "+gymlocation+");");
    }

    protected void storeNewsDetailsInDataBase(String header, String content) {

        // Populate database with news data
        String tableNews = "tbGymPassNews";

        openGymPassDatabase(null).execSQL("INSERT INTO "
                + tableNews
                + " (news_header, news_content)"
                + " VALUES ('"+header+"', '"+content+"');");
    }

    protected void saveAppSharedPrefLogin(String login) {

        // This method saves the 'app_registered' flag in
        // SharedPreferences, it will be used to check if app
        // registered on startup to prevent login screen from
        // appearing once customer signed up.

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefGymPass = preferences.edit();

        prefGymPass.clear();
        prefGymPass.putString("app_login",login);
        prefGymPass.commit();
    }

    protected void storeGymVisitInDB(String login, String time) {
        // Populate database with visits data.

        String tableNameVisits = "tbGymPassCustomerVisits";
        CommonMethods.openGymPassDatabase(MainActivity.this,null).execSQL("INSERT INTO "
                + tableNameVisits
                + " (login, timestamp)"
                + " VALUES ('"+login+"', '"+time+"');");
    }
}










