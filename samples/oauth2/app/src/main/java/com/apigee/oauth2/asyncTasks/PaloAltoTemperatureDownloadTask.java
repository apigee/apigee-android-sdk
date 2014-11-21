package com.apigee.oauth2.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.apigee.google.api.client.http.HttpMethods;
import com.apigee.oauth2.Client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ApigeeCorporation on 11/21/14.
 */
public class PaloAltoTemperatureDownloadTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private TextView paloAltoTempTextView;
    private String temperatureURL;
    private String accessToken;
    private String errorMessage = "";

    public PaloAltoTemperatureDownloadTask(Context context, TextView paloAltoTempTextView, String temperatureURL, String accessToken) {
        this.context = context;
        this.paloAltoTempTextView = paloAltoTempTextView;
        this.temperatureURL = temperatureURL;
        this.accessToken = accessToken;
    }

    @Override
    protected String doInBackground(Void... params) {

        String temperatureString = null;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(temperatureURL);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(HttpMethods.GET);
            urlConnection.setUseCaches(false);

            if  ( this.accessToken != null && this.accessToken.length() > 0 ) {
                String authStr = "Bearer " + this.accessToken;
                urlConnection.setRequestProperty("Authorization", authStr);
            }

            urlConnection.setDoInput(true);

            inputStream = urlConnection.getInputStream();

            if( inputStream != null ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONObject weatherAttributes = jsonObject.getJSONObject("rss")
                        .getJSONObject("channel")
                        .getJSONObject("item")
                        .getJSONObject("yweather:condition")
                        .getJSONObject("#attrs");
                temperatureString = weatherAttributes.getString("@temp") + "" + (char)186;
            }
        } catch ( Exception exception ) {
            this.errorMessage = exception.getLocalizedMessage();
        } finally {
            try {
                if( inputStream != null ) {
                    inputStream.close();
                }
                if( urlConnection != null ) {
                    urlConnection.disconnect();
                }
            } catch(Exception ignored) {
            }
        }
        return temperatureString;
    }

    protected void onPostExecute(String temperature){
        if( temperature != null && temperature.length() > 0 ) {
            this.paloAltoTempTextView.setText(temperature);
        } else {
            Client.showAlert(this.context, "Error getting temperature", this.errorMessage);
        }
    }
}
