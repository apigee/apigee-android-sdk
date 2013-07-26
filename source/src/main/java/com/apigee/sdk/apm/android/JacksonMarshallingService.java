package com.apigee.sdk.apm.android;

import java.io.IOException;

import android.util.Log;

import com.apigee.sdk.apm.android.model.ClientLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Note : This class isn't being used yet. 
 */
public class JacksonMarshallingService {

	ObjectMapper objectMapper = new ObjectMapper();

	public JacksonMarshallingService() {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//ensures that null values do not get sent across the wire to optimize performance.
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	}

	public String marshall(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Object demarshall(String input, Class clazz) {
		try {
			return objectMapper.readValue(input, clazz);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			Log.e(ClientLog.TAG_MONITORING_CLIENT, e.toString());
		} catch (JsonMappingException e) {
			Log.e(ClientLog.TAG_MONITORING_CLIENT, e.toString());
		} catch (IOException e) {
			Log.e(ClientLog.TAG_MONITORING_CLIENT, e.toString());
		}
		return null;
	}

}
