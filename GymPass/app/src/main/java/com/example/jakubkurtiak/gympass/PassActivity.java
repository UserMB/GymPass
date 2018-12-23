package com.example.jakubkurtiak.gympass;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.EAN8;
import com.onbarcode.barcode.android.IBarcode;


// -----------------------------------------
// PassActivity is primarily to serve barcode to allow customer to scan it
// using gym scanners.
// It also stores gym visits, so it can be further used in other activities.
//
// To simulate it to work it is best to set UCD's gym laction in Android Emulator
// like this: "latitude": "53.30", "longitude": "-6.22"
// -----------------------------------------

public class PassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        CommonMethods.awardBadge(PassActivity.this, R.id.badge);
        CommonMethods.setImpactFont(PassActivity.this,R.id.top,R.string.gympass);
        CommonMethods.setImpactFont(PassActivity.this,R.id.your_pass,R.string.scan_code);
        storeGymVisit();
        try{
            visitsSoFar();
        } catch(Exception e) {
            System.out.print("Cannot show visits so far.");
        }

        ImageView imageView=(ImageView) findViewById(R.id.qrcode);
        imageView.setImageBitmap(createBarcode(readBarcode()));

    }

    private void visitsSoFar() {
        // Setting text with information about visits.
        TextView view = (TextView) findViewById(R.id.visits_so_far);
        Typeface font=Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        view.setText(
                "Total visits: "+CommonMethods.readNumberOfVisits(PassActivity.this)+" "
                +"\n"
                +"\n"
                +"Last one: "+lastVisit()+" "
                +"\n"
                +"\n"
                +"It already passed "+CommonMethods.timeSinceLastVisit(PassActivity.this)+" since your last visit."
        );
        view.setTypeface(font, Typeface.ITALIC);
    }

    protected void storeGymVisit() {
        // If member opened PassActivity and their current location is the same as gym's location then
        // gym visit is genuine and can be stored in database.

        if (CommonMethods.currentDeviceLocation(PassActivity.this).equals(readGymLocation())) {
            storeGymVisitInDB(CommonMethods.returnCurrentLogin(PassActivity.this),CommonMethods.currentDate());

            Toast.makeText(PassActivity.this,R.string.in_the_gym,Toast.LENGTH_SHORT).show();
        }
    }

    protected void storeGymVisitInDB(String login, String time) {
        // Populate database with visits data.

        String tableNameVisits = "tbGymPassCustomerVisits";
        CommonMethods.openGymPassDatabase(PassActivity.this,null).execSQL("INSERT INTO "
                + tableNameVisits
                + " (login, timestamp)"
                + " VALUES ('"+login+"', '"+time+"');");
    }

    protected String readGymLocation() {
        // Read gym location from database.

        Cursor cursorGymPass = CommonMethods.readTableToCursor(PassActivity.this,"tbGymPassCustomer");
        int indexLocation = cursorGymPass.getColumnIndex("gymlocation");
        cursorGymPass.moveToFirst();
        String gymLoc = cursorGymPass.getString(indexLocation);
        cursorGymPass.close();
        return gymLoc;
    }


    protected String lastVisit() {
        // Read number of rows in cursors. This gives
        // total number of visits to the gym.

        Cursor cursorNoVisits = CommonMethods.readTableToCursor(PassActivity.this,"tbGymPassCustomerVisits");
        cursorNoVisits.moveToLast();
        int indexTime = cursorNoVisits.getColumnIndex("timestamp");
        String time = cursorNoVisits.getString(indexTime);
        cursorNoVisits.close();

        return time;
    }

    protected int readBarcode() {
        // Read barcode

        int barcode = 0;
        Cursor cursorBarcode = CommonMethods.readTableToCursor(PassActivity.this,"tbGymPassCustomer");
        int indexBarcode = cursorBarcode.getColumnIndex("barcode");
        cursorBarcode.moveToFirst();
        barcode = Integer.parseInt(cursorBarcode.getString(indexBarcode));
        cursorBarcode.close();

        return barcode;
    }

    protected Bitmap createBarcode(int barcodeCode) {

        // The whole code in this method is taken from:
        // http://www.onbarcode.com/products/android_barcode/barcodes/ean8.html
        // as well as OnBarcode.AndroidBarcode.jar library attached to this project
        // This library is generating EAN8 barcode from string.

        EAN8 barcode = new EAN8();

        Bitmap bitmap = Bitmap.createBitmap(1200, 900, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        barcode.setData(String.valueOf(barcodeCode));

        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcode bar module width (X) in pixel
        barcode.setX(12f);
        // barcode bar module height (Y) in pixel
        barcode.setY(750f);

        // barcode image margins
        barcode.setLeftMargin(30f);
        barcode.setRightMargin(30f);
        barcode.setTopMargin(30f);
        barcode.setBottomMargin(60f);

        // barcode image resolution in dpi
        barcode.setResolution(72);

        // disply barcode encoding data below the barcode
        barcode.setShowText(true);
        // barcode encoding data font style
        barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 70));
        // space between barcode and barcode encoding data
        barcode.setTextMargin(30);
        barcode.setTextColor(AndroidColor.black);

        // barcode bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        //barcode.set

	    /*
	    specify your barcode drawing area
	    */
        RectF bounds = new RectF(0, 0, 0, 0);

        try {
            barcode.drawBarcode(canvas,bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}








