package com.apigee.sdk.apm.android.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.apm.android.model.ClientLog;


public class StringUtils {

    /**
     * Static utility method to convert an InputStream to a String
     *
     * @param stream the InputStream to convert to a String
     * @return the converted String
     */

    public static String inputStreamToString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }
    
    public static String fileToString(String fileName) {
    	BufferedReader reader = null;
    	
    	try {
    		reader = new BufferedReader(new FileReader(fileName));
    		String         line = null;
    		StringBuilder  stringBuilder = new StringBuilder();
    		String         ls = System.getProperty("line.separator");

    		while( ( line = reader.readLine() ) != null ) {
    			stringBuilder.append( line );
    			stringBuilder.append( ls );
    		}

    		return stringBuilder.toString();
    	} catch( IOException e ) {
    		Log.e(ClientLog.TAG_MONITORING_CLIENT, "unable to read file '" + fileName + "': " + e.getLocalizedMessage());
    		return null;
    	} finally {
    		if( reader != null ) {
    			try {
    				reader.close();
    			} catch( IOException ignored ) {
    			}
    		}
    	}
    }

}
