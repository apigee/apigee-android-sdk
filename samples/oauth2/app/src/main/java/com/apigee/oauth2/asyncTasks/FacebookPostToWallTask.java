package com.apigee.oauth2.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.apigee.google.api.client.http.HttpMethods;
import com.apigee.oauth2.Client;
import com.apigee.oauth2.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ApigeeCorporation on 11/21/14.
 */
public class FacebookPostToWallTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String postToWallAddress;
    private String postBody;
    private String errorMessage;

    public FacebookPostToWallTask(Context context, String postToWallAddress, String accessToken) {
        this.context = context;
        this.postToWallAddress = postToWallAddress;
        this.postBody = "access_token=" + accessToken + "&message=" + Constants.kFacebookPostMessage;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(postToWallAddress);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(HttpMethods.POST);
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            byte[] dataAsBytes = this.postBody.getBytes();

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(dataAsBytes.length));

            outputStream = urlConnection.getOutputStream();
            outputStream.write(dataAsBytes);
            outputStream.flush();
            outputStream.close();
            outputStream = null;

            try {
                inputStream = urlConnection.getInputStream();
            } catch (Exception exception) {
                inputStream = urlConnection.getErrorStream();
            }

            if( inputStream != null ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                if( jsonObject.has("error") ) {
                    JSONObject errorDictionaryObject = jsonObject.getJSONObject("error");
                    String errorMessage = errorDictionaryObject.getString("message");
                    if( errorMessage != null ) {
                        this.errorMessage = errorMessage;
                    }
                }
            }
        } catch ( Exception exception ) {
            this.errorMessage = exception.getLocalizedMessage();
        } finally {
            try {
                if( inputStream != null ) {
                    inputStream.close();
                }
                if( outputStream != null ) {
                    outputStream.close();
                }
                if( urlConnection != null ) {
                    urlConnection.disconnect();
                }
            } catch(Exception ignored) {
            }
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        if( this.errorMessage != null ) {
            Client.showAlert(this.context, "Error Posting to Facebook", this.errorMessage);
        } else {
            Client.showAlert(this.context, "Successfully Posted to Facebook", "Facebook Message: " + Constants.kFacebookPostMessage);
        }
    }
}
