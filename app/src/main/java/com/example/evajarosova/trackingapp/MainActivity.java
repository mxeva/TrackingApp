package com.example.evajarosova.trackingapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    Button btnShowLocation;
    Button btnShowAddress;
    Button btnSendAddress;
    TextView tvAddress;

    GPSTracker gps;

    //LocationAddress address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        gps = new GPSTracker(MainActivity.this);
        //keeping screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnShowLocation = (Button) findViewById(R.id.show_location);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String result = "Zem. sirka: " + gps.getLatitude() + "\n" +
                            "Zem. delka: " + gps.getLongitude();
                    tvAddress.setText(result);

                    /*Toast.makeText(
                            getApplicationContext(),
                            "Nachazis se na -\nLat: " + latitude + "\nLong: "
                                    + longitude + result , Toast.LENGTH_LONG).show();*/
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        btnSendAddress = (Button) findViewById(R.id.btnSendAddress);
        btnSendAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new SendMail());
                } else {
                    gps.showSettingsAlert();
                }
            }
        });
    }

private class SendMail extends Handler {


    public void handleMessage(Message message) {
        String locationAddress;
        switch (message.what) {
            case 1:
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");

                //posli mail
                String result = "Zem. sirka: " + gps.getLatitude() + "\n" +
                        "Zem. delka: " + gps.getLongitude();
                tvAddress.setText(result);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jarosovae@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Evina lokace");
                i.putExtra(Intent.EXTRA_TEXT, locationAddress + "\n\nSent by app Evas spier :-)"); //"toto je Evina lokace");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                ///
                break;
            default:
                locationAddress = null;
        }
        tvAddress.setText(locationAddress);
    }
}

private class GeocoderHandler extends Handler {
    @Override
    public void handleMessage(Message message) {
        String locationAddress;
        switch (message.what) {
            case 1:
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
                break;
            default:
                locationAddress = null;
        }
        tvAddress.setText(locationAddress);
    }
}


}
