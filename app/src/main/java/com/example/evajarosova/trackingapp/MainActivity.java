package com.example.evajarosova.trackingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.os.AsyncTask;


import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends MapsActivity {
    private static final String SOAP_ACTION = "http://www.evasspier.cz/WebServiceSOAP/server.php/get_location";
    private static final String SOAP_ACTION2 = "http://www.evasspier.cz/WebServiceSOAP/server.php/insert_location";
    private static final String METHOD_NAME = "get_location";
    private static final String METHOD_NAME2 = "insert_location";
    private static final String NAMESPACE = "http://www.evasspier.cz";
    private static final String URL = "http://www.evasspier.cz/WebServiceSOAP/server.php?wsdl";
    private TextView tv;
    private String response;

    Button btnShowAddress;
    Button btnSendAddress;
    Button btnShowOnMap;
    Button btnSearchUser;
//    Button btnSignOut;

    TextView tvAddress;
    GPSTracker gps;
    EditText etUser;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = new GPSTracker(MainActivity.this);

        //volani z loginactivity - predani info z gplus login api
        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");
        //final String name= intent.getStringExtra("name");

        //keeping screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

                    Date date1 = new Date();
                    String date = date1.toString();
                    String address = tvAddress.getText().toString().trim();

                    MyTaskParams params = new MyTaskParams(date, email, address, latitude, longitude);
                    myAsyncTask2 myRequest2 = new myAsyncTask2();
                    myRequest2.execute(params);
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        tvAddress = (TextView) findViewById(R.id.tvAddress);

        btnSendAddress = (Button) findViewById(R.id.btnSendAddress);
        btnSendAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View tvAddress) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,getApplicationContext(), new SendMail());
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        btnShowOnMap = (Button) findViewById(R.id.btnShowOnMap);
        btnShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //markLoc = new MapsActivity(MainActivity.this);
                Intent intent1 = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent1);
            }
        });

        etUser = (EditText) findViewById(R.id.etUser);
        AutoCompleteTextView mAutoView = (AutoCompleteTextView)findViewById(R.id.etUser);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,new String[] {"jarosovae@gmail.com","fousek.vladimir@gmail.com"});
        mAutoView.setAdapter(myAdapter);
        //schovat klavesnici
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

        btnSearchUser = (Button) findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUser.getText().toString();

                //schovat klavesnici
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etUser.getWindowToken(), 0);

                Toast msg = Toast.makeText(getBaseContext(), user, Toast.LENGTH_LONG);
                msg.show();
                // call WS
                String nameParam = etUser.getText().toString();
                myAsyncTask myRequest = new myAsyncTask();
                myRequest.execute(nameParam);
            }
        });
    }

    private class myAsyncTask extends AsyncTask<String, Void, Void>    {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            tvAddress.setText("");
            tvAddress.setText(response);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... names) {
//            String namePar = names[0];
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("name",names[0]);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(URL);

            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //send request
            try {
                response = (String)envelope.getResponse();
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            response = response.toString();
            return null;
        }
    }

    private static class MyTaskParams implements Serializable {
        String date;
        String nick;
        String address;
        Double latitude;
        Double longitude;

        MyTaskParams(String date, String nick, String address, Double latitude, Double longitude) {
            this.date = date;
            this.nick = nick;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private class myAsyncTask2 extends AsyncTask<MyTaskParams, Void, Void>    {

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvAddress.setText("Waiting for response...");
        }

        @Override
        protected Void doInBackground(MyTaskParams... params) {
            String date = params[0].date;
            String nick = params[0].nick;
            String address = params[0].address;
            Double latitude = params[0].latitude;
            Double longitude = params[0].longitude;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("date",date);
            request.addProperty("user",nick);
            request.addProperty("address", address);
            request.addProperty("lat", latitude);
            request.addProperty("longitude", longitude);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            MarshalDouble doubleMarshal = new MarshalDouble();
            doubleMarshal.register(envelope);

            MarshalDate dateMarshal = new MarshalDate();
            dateMarshal.register(envelope);

            HttpTransportSE httpTransport = new HttpTransportSE(URL);

            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION2, envelope);

            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //send request
            try {
                response = (String)envelope.getResponse();
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response = response.toString();
            return null;
        }
    }
    public class MarshalDouble implements Marshal
    {
        @Override
        public Object readInstance(XmlPullParser parser, String namespace, String name,
                                   PropertyInfo expected) throws IOException, XmlPullParserException {
            return Double.parseDouble(parser.nextText());
        }
        public void register(SoapSerializationEnvelope cm) {
            cm.addMapping(cm.xsd, "double", Double.class, this);
        }
        @Override
        public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
            writer.text(obj.toString());
        }
    }
    protected class SendMail extends Handler {

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
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"jarosovae@gmail.com"});//{"jarosovae@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Evina lokace");
                    i.putExtra(Intent.EXTRA_TEXT, locationAddress + "\nhttp://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude() + "\n\nSent by app Evas spier :-)"); //"toto je Evina lokace");
                    //i.putExtra(Intent.EXTRA_REFERRER, "http://maps.google.com/?q=" + gps.getLatitude() + "," + gps.getLongitude());
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