package com.example.evajarosova.trackingapp;

/**
 * Created by evajarosova on 21.9.2015.
 */

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransport;

import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;

public class WSLocation extends Activity {
    private static final String SOAP_ACTION = "http://www.evasspier.cz/WebServiceSOAP/server.php/get_location";
    private static final String METHOD_NAME = "get_location";
    private static final String NAMESPACE = "http://www.evasspier.cz/WebServiceSOAP/server.php?wsdl";
    private static final String URL = "http://www.evasspier.cz/WebServiceSOAP/server.php?wsdl";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.setOutputSoapObject(request);
        HttpTransport ht = new HttpTransport(URL);
        try {
            ht.call(SOAP_ACTION, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            TextView tv = new TextView(this);
            tv.setText("Message :" + response.toString());
            setContentView(tv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}