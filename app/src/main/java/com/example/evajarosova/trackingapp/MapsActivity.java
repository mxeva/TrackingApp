package com.example.evajarosova.trackingapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;

public class MapsActivity extends FragmentActivity implements AsyncResponse {

    ImageButton btnShowAddress;
    ImageButton btnSendAddress;
    ImageButton btnSearchUser;
    ImageButton mapType;
    TableRow btnBar;
//    AutoCompleteTextView etUser2;

    TextView tvAddress;
    TextView tvSearchAddress;
    GPSTracker gps;
    EditText etUser;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvSearchAddress = (TextView) findViewById(R.id.tvSearchAddress);
        etUser = (EditText) findViewById(R.id.etUser);
        gps = new GPSTracker(MapsActivity.this);

        Intent intent = getIntent();
        email=intent.getStringExtra("email");

        // keep screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /////////////////////////////////////////////////////////////////////////////////////////////////
        ///GeocoderHandler insertLoc = new GeocoderHandler();

        //////////////

        btnShowAddress = (ImageButton) findViewById(R.id.showAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                etUser.setVisibility(View.GONE);
                etUser.setText("");
                tvSearchAddress.setVisibility(View.GONE);

                gps = new GPSTracker(MapsActivity.this);
                //schovat klavesnici
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        etUser = (EditText) findViewById(R.id.etUser);
        AutoCompleteTextView mAutoView = (AutoCompleteTextView)findViewById(R.id.etUser);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,new String[] {"jarosovae@gmail.com","ejaroso@gmail.com","fousek.vladimir@gmail.com"});
        mAutoView.setAdapter(myAdapter);
        //schovat klavesnici
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

        btnSearchUser = (ImageButton) findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUser.setVisibility(View.VISIBLE);
                searchUser();
            }
        });


        etUser.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchUser();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnSendAddress = (ImageButton) findViewById(R.id.btnSendAddress);
        btnSendAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View tvAddress) {
                etUser.setVisibility(View.GONE);
                etUser.setText("");
                tvSearchAddress.setVisibility(View.GONE);

                gps = new GPSTracker(MapsActivity.this);

                //schovat klavesnici
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new ShareLocation());
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        mapType = (ImageButton) findViewById(R.id.mapType);
        mapType.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                etUser.setVisibility(View.GONE);
                etUser.setText("");
                tvSearchAddress.setVisibility(View.GONE);

                btnBar = (TableRow) findViewById(R.id.btnBar);

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                    btnBar.setBackgroundResource(R.color.material_deep_teal_200);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//                    btnBar.setBackgroundResource(R.color.wallet_holo_blue_light);
                   }
            }
        });

    }

    public void searchUser(){
        if (etUser.getText()!=null){

            String user = etUser.getText().toString();

            //schovat klavesnici
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

            // call WS
            SearchUser search = new SearchUser();
            SearchUser.MyAsyncTask myRequest = search.new MyAsyncTask(MapsActivity.this);
            myRequest.execute(user);
            myRequest.delegate = MapsActivity.this;

            // zde bude kod zajistujici zobrazeni lokace nalezeneho uzivatele na zaklade souradnic z db

            tvSearchAddress.setVisibility(View.VISIBLE);
            tvAddress.setVisibility(View.GONE);

        } else {
        }
    }

    @Override
    public void processFinish(String output) {
        Log.d("Response AsyncTaskWS:", (String) output);
        String result = ((String) output);
        if (result.equals("zadejte email platneho uzivatele")){
            tvSearchAddress.setText("zadejte email platneho uzivatele");
        } else {

            String latLong = result.substring(result.lastIndexOf(';') + 1);
            Log.d("Response latLong:", latLong);
            String[] latHilfe = latLong.split("\n", 0);
            String latitString = latHilfe[0];
            String longitString = latLong.substring(latLong.lastIndexOf('\n') + 1);
            double latit = parseDouble(latitString);
            double longit = parseDouble(longitString);


            String[] finalRes = result.split(";", 0);
            String finalResult = finalRes[0];
            Log.d("Response finalResult:", finalResult);

//        String finalResult = latit + "\n" + longit;

            tvSearchAddress.setText(finalResult);

            setUpMap(latit, longit);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected boolean isRouteDisplayed() {
        return false;
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */




//    public void showAddress(View view){
//        LocationAddress adr = new LocationAddress();
//    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        GPSTracker gps = new GPSTracker(MapsActivity.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

// create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Here is Eva");

// Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

// adding marker
        mMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(17).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpMap(Double latitude, Double longitude) {
        GPSTracker gps = new GPSTracker(MapsActivity.this);

// create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Here searched user");

// Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

// adding marker
        mMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(17).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    private class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            tvAddress = (TextView) findViewById(R.id.tvAddress);
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    //String result = "Zem. sirka: " + gps.getLatitude() + "\n" + "Zem. delka: " + gps.getLongitude();
                    //tvAddress.setText(result);
                    tvAddress.setText(locationAddress);
                    break;
                default:
                    locationAddress = "ciao";
            }
            //data pro WS insert
            Date date1 = new Date();
            String date = date1.toString();
            // volani z loginactivity - predani info z gplus login api
            // emailMain = email;
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // call insertActivity, set up params-data for WS to insert
            InsertUserActivity.MyTaskParams params = new InsertUserActivity.MyTaskParams(date, email, tvAddress.getText().toString(), latitude, longitude);
            InsertUserActivity save = new InsertUserActivity();
            InsertUserActivity.myAsyncTask2 myRequest2 = save.new myAsyncTask2();
            myRequest2.execute(params);
            tvSearchAddress.setVisibility(View.GONE);
            tvAddress.setVisibility(View.VISIBLE);
        }
    }

    protected class ShareLocation extends Handler {

        public void handleMessage(Message message) {
            String locationAddress;
            tvAddress = (TextView) findViewById(R.id.tvAddress);
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    //posli mail
                    String result = "Zem. sirka: " + gps.getLatitude() + "\n" + "Zem. delka: " + gps.getLongitude();
                    tvAddress.setText(result);

                    Resources resources = getResources();

                    Intent emailIntent = new Intent();
                    emailIntent.setAction(Intent.ACTION_SEND);
                    // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                    emailIntent.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
                    emailIntent.setType("message/rfc822");

                    PackageManager pm = getPackageManager();
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");

                    Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

                    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                    for (int i = 0; i < resInfo.size(); i++) {
                        // Extract the label, append it, and repackage it in a LabeledIntent
                        ResolveInfo ri = resInfo.get(i);
                        String packageName = ri.activityInfo.packageName;
                        if (packageName.contains("android.email")) {
                            emailIntent.setPackage(packageName);
                        } else if (packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") ||
                                packageName.contains("whatsapp")) {
                        //|| packageName.contains("android.gm")
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                            intent.setAction(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            if (packageName.contains("twitter")) {
                                //intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_twitter));
                                intent.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)");
                            } else if (packageName.contains("whatsapp")) {
                                intent.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)");
                            }else if (packageName.contains("facebook")) {
                                // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                                // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                                // will show the <meta content ="..."> text from that page with our link in Facebook.
                                intent.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)");
                            } else if (packageName.contains("mms")) {
                                intent.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)");
                            }
                            intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                        }
                    }
                    // convert intentList to array
                    LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

                    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                    startActivity(openInChooser);
                    break;
                default:
                    locationAddress = null;
            }
            tvAddress.setText(locationAddress);
        }
    }
}