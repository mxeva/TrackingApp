package com.example.evajarosova.trackingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by evajarosova on 8.10.2015.
 */
public class SearchUser extends Activity{
    private static final String SOAP_ACTION = "http://www.evasspier.cz/WebServiceSOAP/server.php/get_location";
    private static final String METHOD_NAME = "get_location";
    private static final String NAMESPACE = "http://www.evasspier.cz";
    private static final String URL = "http://www.evasspier.cz/WebServiceSOAP/server.php?wsdl";


    public class MyAsyncTask extends AsyncTask<String, String, String> {
        private String response = "zadejte email platneho uzivatele";
        private Context context;
        public MyAsyncTask(Context context){
            this.context=context;
        }
        public AsyncResponse delegate=null;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            delegate.processFinish(response);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... names) {
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
                Log.println(1, response, "Response is not OK");
                e.printStackTrace();
            }

            response = response.toString();

            return null;
        }
    }
}
