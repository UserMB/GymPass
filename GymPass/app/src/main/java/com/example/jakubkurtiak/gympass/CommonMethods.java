package com.example.jakubkurtiak.gympass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class CommonMethods {

    // -----------------------------------------
    // Common
    // Class where common methods reside. These methods are used in other activities.
    // -----------------------------------------

    // Set Impact font for given view and string. To use this method pass following parameters:
    // - Current activity name
    // - Object font should be set on
    // - String to be set
    public static void setImpactFont(Activity activity, int viewName, int theString) {

        TextView newfont = (TextView) activity.findViewById(viewName);
        Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/impact.ttf");
        newfont.setText(theString);
        newfont.setTypeface(font, Typeface.ITALIC);
    }

    // Badge awards. User can be awarded badge depends on how frequently they visit the gym.
    public static void awardBadge(Activity activity, int viewName) {
        ImageView badgeAward = (ImageView) activity.findViewById(viewName);

        // Following check will return either regular, silver or golden badge.
        if((CommonMethods.readNumberOfVisits(activity)) < 5) {
            badgeAward.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.badge_regular_member));
        } else if ((CommonMethods.readNumberOfVisits(activity)) < 10) {
            badgeAward.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.badge_silver_member));
        } else {
            badgeAward.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.badge_golden_member));
        }
    }


    // Current date and time.
    public static String currentDate() {

        String currentDateTimeString = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date());
        return currentDateTimeString;
    }

    // Is App registered. Returns true if user is registered in the database. It is used further
    // to handle Android life cycles in such a way that app behaves correctly when press BACK button
    // or when deregistered.
    public static boolean isAppRegistered(Activity activity) {

        String login = "";
        String tableCust = "tbGymPassCustomer";
        boolean isLoginInDb = false;

        try {

            Cursor cursorLogin = CommonMethods.readTableToCursor(activity, tableCust);
            cursorLogin.moveToFirst();
            int loginInx = cursorLogin.getColumnIndex("login");
            login = cursorLogin.getString(loginInx);
            if (login != null) {
                isLoginInDb = true;
            }
            cursorLogin.close();
        } catch (Exception e) {
            Log.e("MAIN_NO_DB_ERR", "Can't read database");
        }
        return isLoginInDb;
    }

    // Getting current user - login from SharedPreferences, to call it:
    // "CommonMethods.returnCurrentLogin(getApplicationContext())"
    // and will return a string.
    public static String returnCurrentLogin(Context context) {

        SharedPreferences prefGymPassLogin = PreferenceManager.getDefaultSharedPreferences(context);
        String currentLogin = prefGymPassLogin.getString("app_login", null);

        return currentLogin;
    }

    // -----------------------------------------
    // Database

    // Read table to cursor.
    public static Cursor readTableToCursor(Activity activity, String tableName) {
        // Read number of rows in cursors. This gives total number of visits to the gym.

        Cursor cursor = CommonMethods.openGymPassDatabase(activity, null).rawQuery(
                "SELECT * FROM " + tableName, null);
        return cursor;
    }

    protected static int readNumberOfVisits(Activity activity) {
        // Read number of rows in cursors. This gives total number of visits to the gym.

        int numberVisits = 0;
        Cursor cursorNoVisits = CommonMethods.readTableToCursor(activity,"tbGymPassCustomerVisits");
        numberVisits = cursorNoVisits.getCount();
        cursorNoVisits.close();

        return numberVisits;
    }

    // Time since last visit.
    public static String timeSinceLastVisit(Activity activity) {
        // Get the time of last visit and compare to current time.

        Cursor cursorNoVisits = CommonMethods.readTableToCursor(activity, "tbGymPassCustomerVisits");
        cursorNoVisits.moveToLast();

        int indexTime = cursorNoVisits.getColumnIndex("timestamp");
        String time = cursorNoVisits.getString(indexTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date dateOfLastVisit = null;
        try {
            dateOfLastVisit = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cursorNoVisits.close();

        // To calculate the difference between NOW and LAST VISIT, Date must be changed
        // into long. Then time of last visit is subtracted from actual time which
        // gives us the actual time since last visit.
        // This time is in seconds, so must be changed to human readable format.

        long dateTimeNowInSeconds = new Date().getTime()/1000;
        long dateTimeLastVisitInSeconds = dateOfLastVisit.getTime()/1000;
        long timeSinceLastVisitInSeconds = dateTimeNowInSeconds - dateTimeLastVisitInSeconds;

        int daysSinceLastVisit = (int) (timeSinceLastVisitInSeconds / 86400);
        int hoursSinceLastVisit = (int) ((timeSinceLastVisitInSeconds % 86400)) / 3600;
        int minutesSinceLastVisit = (int) (((timeSinceLastVisitInSeconds % 86400)) % 3600) / 60;
        int secondsSinceLastVisit = (int) (((timeSinceLastVisitInSeconds % 86400)) % 3600) % 60;

        // Return string with time passed since last visit.

        String timeSinceLastVisit = (daysSinceLastVisit+" days "
                +hoursSinceLastVisit+" hours "
                +minutesSinceLastVisit+" minutes "
                +secondsSinceLastVisit+" seconds ");

        return timeSinceLastVisit;
    }

    // Open database to read.
    public static SQLiteDatabase openGymPassDatabase(Activity activity, SQLiteDatabase db) {

        try {
            db = activity.openOrCreateDatabase("sqlGymPass", MODE_PRIVATE, null);
        } catch (Exception e) {
            Log.e("DB_read_error", "Database read error");
        }
        return db;
    }

    // -----------------------------------------
    // Location


    // Current device location. This is necessary for various reasons, it will be used in PassActivity
    // to validate if customers visit should count as genuine. It will also be use in ShareActivity.

    public static String currentDeviceLocation(Activity activity) {

        String loc = "0";

        if (Build.VERSION.SDK_INT >= 23) {
            try {
                // Since API 23, permissions cannot be granted only in manifest file, they should also be
                // explicitly asked when necessary. It applies to dangerous permissions only and such is location
                // First check if API is >=23, then check if permissions are granted and then initialize LocationManager,
                // else simply ask permissions explicitly.
                // Location precision is narrowed down to 2 decimal places to avoid situations where customer
                // is at the gym, but due to little differences, location does not match and visit will not count.

                if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                    // Although it would suffice to use only "getLastKnownLocation" method, because we need location
                    // only once when activity is opened and we don't need to receive updates. However, there was
                    // a problem when location in phone the changed and app already requested it once. There was no refresh.
                    // So to fix this problem, LocationListener is implemented and on every call to this class, we
                    // just refresh location. This then is fetched by "getLastKnownLocation".
                    LocationListener locationListener = new MyLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                    // Use both providers and then use GPS first and if null, skip to NET.
                    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (locationGPS != null) {
                        String latitude = String.format("%.2f", Double.valueOf(locationGPS.getLatitude()));
                        String longitude = String.format("%.2f", Double.valueOf(locationGPS.getLongitude()));
                        loc = latitude + ',' + longitude;
                        return loc;
                    } else {
                        String latitude = String.format("%.2f", Double.valueOf(locationNet.getLatitude()));
                        String longitude = String.format("%.2f", Double.valueOf(locationNet.getLongitude()));
                        loc = latitude + ',' + longitude;
                        return loc;
                    }

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                }
            }catch(Exception e){
                Log.e("LOC_MAN_ERR_23", "Cannot initialize Location Manager API 23");
            }
            return loc;
        } else {
            try {
                LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (locationGPS != null) {
                    String latitude = String.format("%.2f", Double.valueOf(locationGPS.getLatitude()));
                    String longitude = String.format("%.2f", Double.valueOf(locationGPS.getLongitude()));
                    loc = latitude + ',' + longitude;
                    return loc;
                } else {
                    String latitude = String.format("%.2f", Double.valueOf(locationNet.getLatitude()));
                    String longitude = String.format("%.2f", Double.valueOf(locationNet.getLongitude()));
                    loc = latitude + ',' + longitude;
                    return loc;
                }
            } catch (Exception e) {
                Log.e("LOC_MAN_ERR", "Cannot initialize Location Manager API < 23");
            }
            return loc;
        }
    }

    // Implementing LocationListener that will be used in currentDeviceLocation class.
    private static class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}















