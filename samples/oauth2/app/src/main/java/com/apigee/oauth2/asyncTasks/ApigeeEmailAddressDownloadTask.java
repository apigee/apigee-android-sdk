package com.apigee.oauth2.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.apigee.google.api.client.http.HttpMethods;
import com.apigee.oauth2.Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ApigeeCorporation on 11/21/14.
 */
public class ApigeeEmailAddressDownloadTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private TextView emailAddressTextView;
    private String emailAddressURL;
    private String accessToken;
    private String errorMessage = "";

    public ApigeeEmailAddressDownloadTask(Context context, TextView emailAddressTextView, String emailAddressURL, String accessToken) {
        this.context = context;
        this.emailAddressTextView = emailAddressTextView;
        this.emailAddressURL = emailAddressURL;
        this.accessToken = accessToken;
    }

    @Override
    protected String doInBackground(Void... params) {

        String emailAddress = null;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(emailAddressURL);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(HttpMethods.GET);
            urlConnection.setUseCaches(false);

            if  ( accessToken != null && accessToken.length() > 0 ) {
                String authStr = "Bearer " + accessToken;
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
                JSONArray entitiesArray = jsonObject.getJSONArray("entities");
                JSONObject entityObject = (JSONObject) entitiesArray.get(0);
                emailAddress = entityObject.getString("email");
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
        return emailAddress;
    }

    protected void onPostExecute(String emailAddress){
        if( emailAddress != null && emailAddress.length() > 0 ) {
            this.emailAddressTextView.setText(emailAddress);
        } else {
            Client.showAlert(this.context, "Error getting email address.", this.errorMessage);
        }
    }
}
