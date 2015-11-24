package com.example.evajarosova.trackingapp;

import android.app.Activity;
import android.os.AsyncTask;

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

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by evajarosova on 7.10.2015.
 */
public class InsertUserActivity extends Activity{
    private static final String SOAP_ACTION2 = "http://www.evasspier.cz/WebServiceSOAP/server.php/insert_location";
    private static final String METHOD_NAME2 = "insert_location";
    private static final String NAMESPACE = "http://www.evasspier.cz";
    private static final String URL = "http://www.evasspier.cz/WebServiceSOAP/server.php?wsdl";
    private String response;

    public class myAsyncTask2 extends AsyncTask<MyTaskParams, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            InsertUserActivity.MarshalDouble doubleMarshal = new InsertUserActivity.MarshalDouble();
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

    public static class MyTaskParams implements Serializable {
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

}
