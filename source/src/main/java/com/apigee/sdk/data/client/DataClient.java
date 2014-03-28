package com.apigee.sdk.data.client;

import static com.apigee.sdk.data.client.utils.ObjectUtils.isEmpty;
import static com.apigee.sdk.data.client.utils.UrlUtils.addQueryParams;
import static com.apigee.sdk.data.client.utils.UrlUtils.encodeParams;
import static com.apigee.sdk.data.client.utils.UrlUtils.path;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import com.apigee.sdk.Logger;
import com.apigee.sdk.URLConnectionFactory;
import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.ClientAsyncTask;
import com.apigee.sdk.data.client.callbacks.DeviceRegistrationCallback;
import com.apigee.sdk.data.client.callbacks.GroupsRetrievedCallback;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Activity;
import com.apigee.sdk.data.client.entities.Collection;
import com.apigee.sdk.data.client.entities.Device;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.entities.Group;
import com.apigee.sdk.data.client.entities.Message;
import com.apigee.sdk.data.client.entities.User;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.apigee.sdk.data.client.utils.DeviceUuidFactory;
import com.apigee.sdk.data.client.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import com.apigee.sdk.data.client.push.GCMDestination;
import com.apigee.sdk.data.client.push.GCMPayload;


/**
 * The DataClient class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 * 
 */
public class DataClient implements LocationListener {

    private static Logger log = null; //new DefaultAndroidLog();
    
    public static final String LOGGING_TAG = "DATA_CLIENT";
    public static final String OPTION_KEY_BASE_URL = "baseURL";

    public static boolean FORCE_PUBLIC_API = false;

    // Public API
    public static String PUBLIC_API_URL = "https://api.usergrid.com";

    // Local API of standalone server
    public static String LOCAL_STANDALONE_API_URL = "http://localhost:8080";

    // Local API of Tomcat server in Eclipse
    public static String LOCAL_TOMCAT_API_URL = "http://localhost:8080/ROOT";

    // Local API
    public static String LOCAL_API_URL = LOCAL_STANDALONE_API_URL;

    protected static final String HTTP_METHOD_DELETE = "DELETE";
    protected static final String HTTP_METHOD_GET    = "GET";
    protected static final String HTTP_METHOD_POST   = "POST";
    protected static final String HTTP_METHOD_PUT    = "PUT";
    
    private String apiUrl = PUBLIC_API_URL;

    private String organizationId;
    private String applicationId;
    private String clientId;
    private String clientSecret;

    private User loggedInUser = null;

    private String accessToken = null;

    private String currentOrganization = null;
    private URLConnectionFactory urlConnectionFactory = null;
    
    private LocationManager locationManager;
    private UUID deviceID;
    
    private Context context = null;

    //static RestTemplate restTemplate = new RestTemplate(true);  // include default converters

    @Override
    public void onProviderDisabled(String provider) {
        
    }

    @Override
    public void onProviderEnabled(String provider) {
        
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        
    }

    public interface Query {

        public ApiResponse getResponse();

        public boolean more();

        public Query next();

    }

    public static boolean isUuidValid(UUID uuid) {
    	return( uuid != null );
    }

    
    public static void setLogger(Logger logger) {
    	log = logger;
    }
    
    protected static String arrayToDelimitedString(String[] arrayOfStrings, String delimiter) {
    	StringBuilder sb = new StringBuilder();
    	
    	for( int i = 0; i < arrayOfStrings.length; ++i ) {
    		if( i > 0 ) {
    			sb.append(delimiter);
    		}
    		
    		sb.append(arrayOfStrings[i]);
    	}
    	
    	return sb.toString();
    }
    
    protected static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
    	if (str == null) {
    		return null;
    	}

    	StringTokenizer st = new StringTokenizer(str, delimiters);
    	
    	int numTokens = st.countTokens();
    	List<String> listTokens;
    	
    	if( numTokens > 0 ) {

    		listTokens = new ArrayList<String>(numTokens);

    		while (st.hasMoreTokens()) {

    			String token = st.nextToken();

    			if (trimTokens) {
    				token = token.trim();
    			}

    			if (!ignoreEmptyTokens || token.length() > 0) {
    				listTokens.add(token);
    			}
    		}
    	} else {
    		listTokens = new ArrayList<String>();
    	}
    	
    	return listTokens.toArray(new String[listTokens.size()]);
    }
    
    /**
     * Default constructor for instantiating a client.
     */
    public DataClient() {
        init();
    }

    /**
     * Instantiate a data client for a specific app. This is used to call most 
     * SDK methods.
     * 
     * @param   organizationId  the API BaaS organization name
     * @param   applicationId   the API BaaS application id or name
     * @param   context the Android Context
     */
    public DataClient(String organizationId, String applicationId, Context context) {
        init();
        this.organizationId = organizationId;
        this.applicationId = applicationId;
        this.context = context;
        captureDeviceLocation();
    }

    /**
     * Instantiate a data client for a specific app with a base URL other than the default
     * api.usergrid.com. This is used to call most SDK methods.
     * 
     * @param   organizationId  the API BaaS organization name
     * @param   applicationId   the API BaaS application id or name
     * @param   baseURL the base URL to use for all API calls
     * @param   context the Android Context
     */
    public DataClient(String organizationId, String applicationId, String baseURL, Context context) {
        init();
        this.organizationId = organizationId;
        this.applicationId = applicationId;
        this.context = context;

        if( baseURL != null ) {
        	this.setApiUrl(baseURL);
        }
        
        //captureDeviceLocation();
    }

    public void init() {
    }
    

    /****************** LOGGING ***********************/
    /****************** LOGGING ***********************/


    /**
     * Logs a trace-level logging message with tag 'DATA_CLIENT'
     *
     * @param   logMessage  the message to log
     */
    public void logTrace(String logMessage) {
    	if( (log != null) && (logMessage != null) ) {
    		log.v(LOGGING_TAG,logMessage);
    	}
    }
    
    /**
     * Logs a debug-level logging message with tag 'DATA_CLIENT'
     *
     * @param   logMessage  the message to log
     */
    public void logDebug(String logMessage) {
    	if( (log != null) && (logMessage != null) ) {
    		log.d(LOGGING_TAG,logMessage);
    	}
    }
    
    /**
     * Logs an info-level logging message with tag 'DATA_CLIENT'
     *
     * @param   logMessage  the message to log
     */
    public void logInfo(String logMessage) {
    	if( (log != null) && (logMessage != null) ) {
    		log.i(LOGGING_TAG,logMessage);
    	}
    }
    
    /**
     * Logs a warn-level logging message with tag 'DATA_CLIENT'
     *
     * @param   logMessage  the message to log
     */
    public void logWarn(String logMessage) {
    	if( (log != null) && (logMessage != null) ) {
    		log.w(LOGGING_TAG,logMessage);
    	}
    }
    
    /**
     * Logs an error-level logging message with tag 'DATA_CLIENT'
     *
     * @param   logMessage  the message to log
     */
    public void logError(String logMessage) {
    	if( (log != null) && (logMessage != null) ) {
    		log.e(LOGGING_TAG,logMessage);
    	}
    }

    public void writeLog(String logMessage) {
        if( log != null ) {
            //TODO: do we support different log levels in this class?
            log.d(LOGGING_TAG, logMessage);
        }
    }
    

    /****************** ACCESSORS/MUTATORS ***********************/
    /****************** ACCESSORS/MUTATORS ***********************/

    /**
     * Sets a new URLConnectionFactory object in the DataClient
     *
     * @param  urlConnectionFactory  a new URLConnectionFactory object
     */
    public void setUrlConnectionFactory(URLConnectionFactory urlConnectionFactory) {
    	this.urlConnectionFactory = urlConnectionFactory;
    }

    /**
     * @return the Usergrid API url (default: http://api.usergrid.com)
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * Sets the base URL for API requests
     *
     * @param apiUrl the API base url to be set (default: http://api.usergrid.com)
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Sets the base URL for API requests and returns the updated DataClient object
     *
     * @param apiUrl the Usergrid API url (default: http://api.usergrid.com)
     * @return DataClient object for method call chaining
     */
    public DataClient withApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }
    
    
    /**
     * Sets the API BaaS organization ID and returns the DataClient object
     *
     * @param  organizationId  the organizationId to set
     * @return  the updated DataClient object
     */
    public DataClient withOrganizationId(String organizationId){
        this.organizationId = organizationId;
        return this;
    }
    
    

    /**
     * Gets the current API BaaS organization ID set in the DataClient
     *
     * @return the current organizationId
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the API BaaS organization ID
     *
     * @param  organizationId  the organizationId to set     
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Gets the current API BaaS application ID set in the DataClient
     *
     * @return the current organizationId or name
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the API BaaS application Id
     *
     * @param  applicationId  the application id or name
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
   

    /**
     * Sets the API BaaS application ID and returns the DataClient object
     *
     * @param  applicationId  the application ID to set
     * @return  the updated DataClient object
     */
    public DataClient withApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    /**
     * Gets the application (not organization) client ID credential for making calls as the 
     * application-owner. Not safe for most mobile use. 
     * @return the client id 
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the application (not organization) client ID credential, used for making 
     * calls as the application-owner. Not safe for most mobile use.
     * @param clientId the client id 
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Sets the client ID credential in the DataClient object. Not safe for most mobile use.
     *
     * @param clientId the client key id
     * @return DataClient object for method call chaining
     */
    public DataClient withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Gets the application (not organization) client secret credential for making calls as the 
     * application-owner. Not safe for most mobile use. 
     * @return the client secret 
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets the application (not organization) client secret credential, used for making 
     * calls as the application-owner. Not safe for most mobile use.
     *
     * @param clientSecret the client secret 
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Sets the client secret credential in the DataClient object. Not safe for most mobile use.
     *
     * @param clientSecret the client secret
     * @return DataClient object for method call chaining
     */
    public DataClient withClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * Gets the UUID of the logged-in user after a successful authorizeAppUser request
     * @return the UUID of the logged-in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the UUID of the logged-in user. Usually not set by host application
     * @param loggedInUser the UUID of the logged-in user
     */
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * Gets the OAuth2 access token for the current logged-in user after a 
     * successful authorize request
     *
     * @return the OAuth2 access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Saves the OAuth2 access token in the DataClient after a successful authorize
     * request. Usually not set by host application.
     *
     * @param accessToken an OAuth2 access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the current organization from DataClient 
     *
     * @return the currentOrganization
     */
    public String getCurrentOrganization() {
        return currentOrganization;
    }

    /**     
     * Sets the current organizanization from DataClient 
     *
     * @param currentOrganization
     */
    public void setCurrentOrganization(String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }
    
    /**
     * Gets the UUID of the device entity associated with the user's device from the device. 
     * If no UUID is present, one is generated and saved to the device.
     *
     * @return the device entity UUID
     */
    public UUID getUniqueDeviceID() {
        if (deviceID == null) {
            deviceID = new DeviceUuidFactory(context).getDeviceUuid();
        }
        
        return deviceID;
    }


    /****************** API/HTTP REQUEST ***********************/
    /****************** API/HTTP REQUEST ***********************/

    /**
     *  Forms and initiates a raw synchronous http request and processes the response.
     *
     *  @param  httpMethod the http method in the format: 
     *      HTTP_METHOD_<method_name> (e.g. HTTP_METHOD_POST)
     *  @param  params the URL parameters to append to the request URL
     *  @param  data the body of the request
     *  @param  segments  additional URL path segments to append to the request URL 
     *  @return  ApiResponse object
     */
	public ApiResponse doHttpRequest(String httpMethod, Map<String, Object> params, Object data, String... segments) {
		ApiResponse response = null;
		OutputStream out = null;
		InputStream in = null;
		HttpURLConnection conn = null;
		
		String urlAsString = path(apiUrl, segments);
		
		try {
	        String contentType = "application/json";
	        if (httpMethod.equals(HTTP_METHOD_POST) && isEmpty(data) && !isEmpty(params)) {
	            data = encodeParams(params);
	            contentType = "application/x-www-form-urlencoded";
	        } else {
	            urlAsString = addQueryParams(urlAsString, params);
	        }

			//logTrace("Invoking " + httpMethod + " to '" + urlAsString + "'");

			URL url = new URL(urlAsString);
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod(httpMethod);
			conn.setRequestProperty("Content-Type", contentType);
			conn.setUseCaches(false);
			
			if  ((accessToken != null) && (accessToken.length() > 0)) {
				String authStr = "Bearer " + accessToken;
				conn.setRequestProperty("Authorization", authStr);
			}

			conn.setDoInput(true);
			
	        if (httpMethod.equals(HTTP_METHOD_POST) || httpMethod.equals(HTTP_METHOD_PUT)) {
	            if (isEmpty(data)) {
	                data = JsonNodeFactory.instance.objectNode();
	            }
	            
	            String dataAsString = null;
	            
	            if ((data != null) && (!(data instanceof String))) {
	            	ObjectMapper objectMapper = new ObjectMapper();
	    			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	    			dataAsString = objectMapper.writeValueAsString(data);
	            } else {
	            	dataAsString = (String) data;
	            }
	            
	    		//logTrace("Posting/putting data: '" + dataAsString + "'");

				byte[] dataAsBytes = dataAsString.getBytes();

				conn.setRequestProperty("Content-Length", Integer.toString(dataAsBytes.length));
				conn.setDoOutput(true);

				out = conn.getOutputStream();
				out.write(dataAsBytes);
				out.flush();
				out.close();
				out = null;
	        }
	        
			in = conn.getInputStream();
			if( in != null ) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				
				while( (line = reader.readLine()) != null ) {
					sb.append(line);
					sb.append('\n');
				}
				
				String responseAsString = sb.toString();

				//logTrace("response from server: '" + responseAsString + "'");
				
				JacksonMarshallingService marshallingService = new JacksonMarshallingService();
				response = (ApiResponse) marshallingService.demarshall(responseAsString, ApiResponse.class);
				if( response != null ) {
					response.setRawResponse(responseAsString);
				}
				
				response.setDataClient(this);
			} else {
				response = null;
				logTrace("no response body from server");
			}

			//final int responseCode = conn.getResponseCode();
			//logTrace("responseCode from server = " + responseCode);
		}
		catch(Exception e) {
			logError("Error " + httpMethod + " to '" + urlAsString + "'" );
			if( e != null ) {
				e.printStackTrace();
				logError(e.getLocalizedMessage());
			}
			response = null;
		}
		catch(Throwable t) {
			logError("Error " + httpMethod + " to '" + urlAsString + "'" );
			if( t != null ) {
				t.printStackTrace();
				logError(t.getLocalizedMessage());
			}
			response = null;
		}
		finally {
			try {
				if( out != null ) {
					out.close();
				}
			
				if( in != null ) {
					in.close();
				}
				
				if( conn != null ) {
					conn.disconnect();
				}
			} catch(Exception ignored) {
			}
		}
		
	    return response;
	}


    /**
     * High-level synchronous API request. Implements the http request
     * for most SDK methods by calling 
     * {@link #doHttpRequest(String,Map<String,Object>,Object,String...)}
     * 
     *  @param  httpMethod the http method in the format: 
     *      HTTP_METHOD_<method_name> (e.g. HTTP_METHOD_POST)
     *  @param  params the URL parameters to append to the request URL
     *  @param  data the body of the request
     *  @param  segments  additional URL path segments to append to the request URL 
     *  @return  ApiResponse object
     */
    public ApiResponse apiRequest(String httpMethod,
            Map<String, Object> params, Object data, String... segments) {
        ApiResponse response = null;
        
        response = doHttpRequest(httpMethod, params, data, segments);
        
        if( (response == null) && (log != null) ) {
        	logError("doHttpRequest returned null");
        }
        
        return response;
    }

    protected void assertValidApplicationId() {
        if (isEmpty(applicationId)) {
            throw new IllegalArgumentException("No application id specified");
        }
    }


    /****************** LOG IN/LOG OUT/OAUTH ***********************/
    /****************** LOG IN/LOG OUT/OAUTH ***********************/

    /**
     * Logs the user in and get a valid access token.
     * 
     * @param usernameOrEmail the username or email associated with the user entity in API BaaS
     * @param password the users API BaaS password
     * @return non-null ApiResponse if request succeeds, check getError() for
     *         "invalid_grant" to see if access is denied.
     */
    public ApiResponse authorizeAppUser(String usernameOrEmail, String password) {
        validateNonEmptyParam(usernameOrEmail, "email");
        validateNonEmptyParam(password,"password");
        assertValidApplicationId();
        loggedInUser = null;
        accessToken = null;
        currentOrganization = null;
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("grant_type", "password");
        formData.put("username", usernameOrEmail);
        formData.put("password", password);
        ApiResponse response = apiRequest(HTTP_METHOD_POST, formData, null,
                organizationId, applicationId, "token");
        if (response == null) {
            return response;
        }
        if (!isEmpty(response.getAccessToken()) && (response.getUser() != null)) {
            loggedInUser = response.getUser();
            accessToken = response.getAccessToken();
            currentOrganization = null;
            logInfo("authorizeAppUser(): Access token: " + accessToken);
        } else {
            logInfo("authorizeAppUser(): Response: " + response);
        }
        return response;
    }

	/**
	 * Log the user in and get a valid access token. Executes asynchronously in
	 * background and the callbacks are called in the UI thread.
	 * 
	 * @param  usernameOrEmail  the username or email associated with the user entity in API BaaS
     * @param  password  the users API BaaS password
     * @param  callback  an ApiResponseCallback to handle the async response
	 */
	public void authorizeAppUserAsync(final String usernameOrEmail,
			final String password, final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return authorizeAppUser(usernameOrEmail, password);
			}
		}).execute();
	}

    /**
     * Change the password for the currently logged in user. You must supply the
     * old password and the new password.
     * 
     * @param username the username or email address associated with the user entity in API BaaS
     * @param oldPassword the user's old password
     * @param newPassword the user's new password
     * @return ApiResponse object
     */
    public ApiResponse changePassword(String username, String oldPassword,
            String newPassword) {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("newpassword", newPassword);
        data.put("oldpassword", oldPassword);

        return apiRequest(HTTP_METHOD_POST, null, data, organizationId,  applicationId, "users",
                username, "password");

    }

    /**
     * Log the user in with their numeric pin-code and get a valid access token.
     * 
     * @param  email  the email address associated with the user entity in API BaaS
     * @param  pin  the pin associated with the user entity in API BaaS
     * @return  non-null ApiResponse if request succeeds, check getError() for
     *         "invalid_grant" to see if access is denied.
     */
    public ApiResponse authorizeAppUserViaPin(String email, String pin) {
        validateNonEmptyParam(email, "email");
        validateNonEmptyParam(pin, "pin");
        assertValidApplicationId();
        loggedInUser = null;
        accessToken = null;
        currentOrganization = null;
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("grant_type", "pin");
        formData.put("username", email);
        formData.put("pin", pin);
        ApiResponse response = apiRequest(HTTP_METHOD_POST, formData, null,
                organizationId,  applicationId, "token");
        if (response == null) {
            return response;
        }
        if (!isEmpty(response.getAccessToken()) && (response.getUser() != null)) {
            loggedInUser = response.getUser();
            accessToken = response.getAccessToken();
            currentOrganization = null;
            logInfo("authorizeAppUser(): Access token: " + accessToken);
        } else {
            logInfo("authorizeAppUser(): Response: " + response);
        }
        return response;
    }

	/**
	 * Log the user in with their numeric pin-code and get a valid access token.
	 * Executes asynchronously in background and the callbacks are called in the
	 * UI thread.
	 * 
	 * @param  email  the email address associated with the user entity in API BaaS
     * @param  pin  the pin associated with the user entity in API BaaS     
	 */
	public void authorizeAppUserViaPinAsync(final String email,
			final String pin, final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return authorizeAppUserViaPin(email, pin);
			}
		}).execute();
	}

    /**
     * Log the user in with their Facebook access token retrieved via Facebook
     * OAuth. Sets the user's identifier and API BaaS OAuth2 access token in DataClient 
     * if successfully authorized.
     * 
     * @param fb_access_token the valid OAuth token returned by Facebook     
     * @return non-null ApiResponse if request succeeds, check getError() for
     *         "invalid_grant" to see if access is denied.
     */
    public ApiResponse authorizeAppUserViaFacebook(String fb_access_token) {
        validateNonEmptyParam(fb_access_token, "Facebook token");
        assertValidApplicationId();
        loggedInUser = null;
        accessToken = null;
        currentOrganization = null;
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("fb_access_token", fb_access_token);
        ApiResponse response = apiRequest(HTTP_METHOD_POST, formData, null,
                organizationId,  applicationId, "auth", "facebook");
        if (response == null) {
            return response;
        }
        if (!isEmpty(response.getAccessToken()) && (response.getUser() != null)) {
            loggedInUser = response.getUser();
            accessToken = response.getAccessToken();
            currentOrganization = null;
            logInfo("authorizeAppUserViaFacebook(): Access token: "
                    + accessToken);
        } else {
            logInfo("authorizeAppUserViaFacebook(): Response: "
                    + response);
        }
        return response;
    }
    
	/**
     * Log the user in with their Facebook access token retrieved via Facebook
     * OAuth. Sets the user's identifier and API BaaS OAuth2 access token in DataClient 
     * if successfully authorized. Executes asynchronously in background and the 
     * callbacks are called in the UI thread.
     * 
     * @param  fb_access_token the valid OAuth token returned by Facebook 
     * @param  callback  an ApiResponseCallback to handle the async response          
     */
	public void authorizeAppUserViaFacebookAsync(final String fb_access_token,
			final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return authorizeAppUserViaFacebook(fb_access_token);
			}
		}).execute();
	}

    /**
     * Log out a user and destroy the access token currently stored in DataClient 
     * on the server and in the DataClient.
     * 
     * @param  username  The username to be logged out
     * @return  non-null ApiResponse if request succeeds
     */
    public ApiResponse logOutAppUser(String username) {
        String token = getAccessToken();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("token",token);
        ApiResponse response = apiRequest(HTTP_METHOD_PUT, params, null,
                organizationId,  applicationId, "users",username,"revoketoken?");
        if (response == null) {
            return response;
        } else {
            logInfo("logoutAppUser(): Response: " + response);
            setAccessToken(null);
        }
        return response;
    }

    /**
     * Log out a user and destroy the access token currently stored in DataClient 
     * on the server and in the DataClient.
     * Executes asynchronously in background and the callbacks are called in the
     * UI thread.
     * 
     * @param  username  The username to be logged out
     * @param  callback  an ApiResponseCallback to handle the async response     
     */
    public void logOutAppUserAsync(final String username, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return logOutAppUser(username);
            }
        }).execute();
    }

   /**
     * Destroy a specific user token on the server. The token will also be cleared 
     * from the DataClient instance, if it matches the token provided.
     * 
     * @param username The username to be logged out
     * @param token The access token to be destroyed on the server
     * @return  non-null ApiResponse if request succeeds
     */
    public ApiResponse logOutAppUserForToken(String username, String token) {                
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("token",token);
        ApiResponse response = apiRequest(HTTP_METHOD_PUT, params, null,
                organizationId,  applicationId, "users",username,"revoketoken?");
        if (response == null) {
            return response;
        } else {
            logInfo("logoutAppWithTokenUser(): Response: " + response);
            if (token.equals(getAccessToken())) {
                setAccessToken(null);
            }
        }
        return response;
    }

    /**
     * Destroy a specific user token on the server. The token will also be cleared 
     * from the DataClient instance, if it matches the token provided.
     * Executes asynchronously in background and the callbacks are called in the UI thread.
     * 
     * @param  username  The username to be logged out
     * @param  token  The access token to be destroyed on the server     
     */
    public void logOutAppUserForTokenAsync(final String username, final String token, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return logOutAppUserForToken(username, token);
            }
        }).execute();
    }

    /**
     * Log out a user and destroy all associated tokens on the server.
     * The token stored in DataClient will also be destroyed.
     * 
     * @param  username The username to be logged out
     * @return  non-null ApiResponse if request succeeds
     */
    public ApiResponse logOutAppUserForAllTokens(String username) {
        ApiResponse response = apiRequest(HTTP_METHOD_PUT, null, null,
                organizationId,  applicationId, "users",username,"revoketokens");
        if (response == null) {
            return response;
        } else {
            logInfo("logoutAppUserForAllTokens(): Response: " + response);
            setAccessToken(null);
        }
        return response;
    }

    /**
     * Log out a user and destroy all associated tokens on the server.
     * The token stored in DataClient will also be destroyed.
     * Executes asynchronously in background and the callbacks are called in the UI thread.
     * 
     * @param  username  The username to be logged out
     * @return  non-null ApiResponse if request succeeds
     */
    public void logOutAppUserForAllTokensAsync(final String username, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return logOutAppUserForAllTokens(username);
            }
        }).execute();
    }

    /**
     * Log the app in with it's application (not organization) client id and 
     * client secret key. Not recommended for production apps.
     * 
     * @param  clientId  the API BaaS application's client ID 
     * @param  clientSecret  the API BaaS application's client secret     
     */
    public ApiResponse authorizeAppClient(String clientId, String clientSecret) {
        validateNonEmptyParam(clientId, "client identifier");
        validateNonEmptyParam(clientSecret, "client secret");
        assertValidApplicationId();
        loggedInUser = null;
        accessToken = null;
        currentOrganization = null;
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("grant_type", "client_credentials");
        formData.put("client_id", clientId);
        formData.put("client_secret", clientSecret);
        ApiResponse response = apiRequest(HTTP_METHOD_POST, formData, null,
                organizationId, applicationId, "token");
        if (response == null) {
            return response;
        }
        if (!isEmpty(response.getAccessToken())) {
            loggedInUser = null;
            accessToken = response.getAccessToken();
            currentOrganization = null;
            logInfo("authorizeAppClient(): Access token: "
                    + accessToken);
        } else {
            logInfo("authorizeAppClient(): Response: " + response);
        }
        return response;
    }


    /****************** PUSH NOTIFICATIONS ***********************/
    /****************** PUSH NOTIFICATIONS ***********************/

	/**
	 * Log the app in with it's application (not organization) client id and 
	 * client secret key. Not recommended for production apps. Executes asynchronously 
	 * in background and the callbacks are called in the UI thread.
	 * 
	 * @param  clientId  the API BaaS application's client ID 
     * @param  clientSecret  the API BaaS application's client secret
	 * @param  callback  an ApiResponseCallback to handle the async response
	 */
	public void authorizeAppClientAsync(final String clientId,
			final String clientSecret, final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {

			@Override
			public ApiResponse doTask() {
				return authorizeAppClient(clientId, clientSecret);
			}
		}).execute();
	}

    private void validateNonEmptyParam(Object param, String paramName) {
        if ( isEmpty(param) ) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }

    /**
     * Creates or updates a device entity using the provided deviceId, and saves the device model, 
     * device platform, and OS version in the entity. 
     * If an entity does not exist for the device, it is created with a UUID equal to deviceId.
     * 
     * @param  deviceId  the device entity's UUID
     * @param  properties  additional properties to save in the device entity.      
     * @return  a Device object if success. Models the API BaaS device entity.
     */
    public Device registerDevice(UUID deviceId, Map<String, Object> properties) {
        assertValidApplicationId();
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        properties.put("refreshed", System.currentTimeMillis());
        
        // add device meta-data
        properties.put("deviceModel", Build.MODEL);
        properties.put("devicePlatform", "android");
        properties.put("deviceOSVersion", Build.VERSION.RELEASE);
        
        ApiResponse response = apiRequest(HTTP_METHOD_PUT, null, properties,
                organizationId, applicationId, "devices", deviceId.toString());
        return response.getFirstEntity(Device.class);
    }

    /**
     * Creates or updates a device entity using the provided deviceId, and saves the device model, 
     * device platform, and OS version in the entity. 
     * If an entity does not exist for the device, it is created with a UUID equal to deviceId.
     * Executes asynchronously in background and the callbacks are called in the UI thread.
     * 
     * @param  deviceId  the device entity's UUID
     * @param  properties  additional properties to save in the device entity     
     * @param  callback  a DeviceRegistrationCallback to handle the async response
     */
    public void registerDeviceAsync(final UUID deviceId,
            final Map<String, Object> properties,
            final DeviceRegistrationCallback callback) {
        (new ClientAsyncTask<Device>(callback) {
            @Override
            public Device doTask() {
                return registerDevice(deviceId, properties);
            }
        }).execute();
    }

    /**
     * Registers a device for push notifications using the device entity UUID. If
     * the device does not have an associated entity, pass DataClient.getUniqueDeviceID()
     * and one will be created.
     *
     * @param  deviceId  the UUID of the device entity associated with this device
     * @param  notifier  the 'name' property of the notifier that will be used to send
     *      notifications to this device 
     * @param  token  the Google Cloud Messaging (GCM) API key. Should be the same as
     *      the API key saved in the notifier entity.
     * @param  properties  additional properties to be stored in the device entity
     * @return  a Device object if success
     */
    public Device registerDeviceForPush(UUID deviceId,
                                        String notifier,
                                        String token,
                                        Map<String, Object> properties) {
      if (properties == null) {
          properties = new HashMap<String, Object>();
      }
      String notifierKey = notifier + ".notifier.id";
      properties.put(notifierKey, token);
      return registerDevice(deviceId, properties);
    }

    /**
     * Registers a device for push notifications using the device entity UUID. If
     * the device does not have an associated entity, pass DataClient.getUniqueDeviceID()
     * and one will be created. Executes asynchronously in background and the callbacks      
     * are called in the UI thread.
     *
     * @param  deviceId  the UUID of the device entity associated with this device
     * @param  notifier  the 'name' property of the notifier that will be used to send
     *      notifications to this device 
     * @param  gcm_api_key  the Google Cloud Messaging (GCM) API key. Should be the same as
     *      the API key saved in the notifier entity.
     * @param  properties  additional properties to be stored in the device entity     
     * @param  callback  a DeviceRegistrationCallback to handle the async response
     */

    public void registerDeviceForPushAsync(final UUID deviceId,
                                           final String notifier,
                                           final String token,
                                           final Map<String, Object> properties,
                                           final DeviceRegistrationCallback callback) {
      (new ClientAsyncTask<Device>(callback) {
        @Override
        public Device doTask() {
          return registerDeviceForPush(deviceId, notifier, token, properties);
        }
      }).execute();
    }
    
    /**
     * Creates a notification Entity and populates it with push notification parameters 
     * in preparation for sending to notification service.
     * @param  payload the push notification payload
     * @param  destination the destination for the push notification. Can be a single device entity,
     *      set of devices, or all devices. For more information, see GCMDestination.
     * @param  notifier the 'name' property of the notifier entity to be used to send the notification
     * @return  populated Entity instance
     * @see  GCMPayload
     * @see  GCMDestination
     */
    protected Entity populatePushEntity(GCMPayload payload,GCMDestination destination,String notifier) {
    	if ((payload != null) && (destination != null) && (notifier != null)) {
    		String notificationsPath = destination.getDeliveryPath() + "/notifications";
    		Entity notification = new Entity(this,notificationsPath);
    		HashMap<String,String> payloads = new HashMap<String, String>();
    		payloads.put(notifier, payload.getAlertText());
    		notification.setProperty("payloads", JsonUtils.toJsonNode(payloads));
    		return notification;
    	} else {
    		if (payload == null) {
    			throw new IllegalArgumentException("payload cannot be null or empty");
    		} else if (destination == null) {
    			throw new IllegalArgumentException("destination cannot be null or empty");
    		} else if (notifier == null) {
    			throw new IllegalArgumentException("notifier cannot be null or empty");
    		}

    		return null;
    	}
    }
    
    /**
     * Creates a notification entity and sends a push notification synchronously.
     * @param  payload the push notification payload
     * @param  destination the destination for the push notification. Can be a single device entity,
     *      set of devices, or all devices. For more information, see GCMDestination.
     * @param  notifier the 'name' property of the notifier entity to be used to send the notification
     * @return  ApiResponse object from the notification entity creation
     * @see  GCMPayload
     * @see  GCMDestination
     */
    public ApiResponse pushNotification(GCMPayload payload,
    					GCMDestination destination,
    					String notifier) {
    	return this.createEntity(populatePushEntity(payload,destination,notifier));
    }

    /**
     * Creates a notification entity and sends a push notification asynchronously.
     * @param  payload the push notification payload
     * @param  destination the destination for the push notification. Can be a single device entity,
     *      set of devices, or all devices. For more information, see GCMDestination.
     * @param  notifier the 'name' property of the notifier entity to be used to send the notification     
     * @see  GCMPayload
     * @see  GCMDestination
     */
    public void pushNotificationAsync(GCMPayload payload,
			GCMDestination destination,
			String notifier,
			final ApiResponseCallback callback) {
    	this.createEntityAsync(populatePushEntity(payload,destination,notifier),callback);
    }


    /****************** GENERIC ENTITY MANAGEMENT ***********************/
    /****************** GENERIC ENTITY MANAGEMENT ***********************/

    /**
     * Create a new entity on the server.
     * 
     * @param entity
     * @return an ApiResponse with the new entity in it.
     */
    public ApiResponse createEntity(Entity entity) {
        assertValidApplicationId();
        if (isEmpty(entity.getType())) {
            throw new IllegalArgumentException("Missing entity type");
        }
        ApiResponse response = apiRequest(HTTP_METHOD_POST, null, entity,
                organizationId, applicationId, entity.getType());
        return response;
    }

    /**
     * Create a new entity on the server from a set of properties. Properties
     * must include a "type" property.
     * 
     * @param properties
     * @return an ApiResponse with the new entity in it.
     */
    public ApiResponse createEntity(Map<String, Object> properties) {
        assertValidApplicationId();
        if (isEmpty(properties.get("type"))) {
            throw new IllegalArgumentException("Missing entity type");
        }
        ApiResponse response = apiRequest(HTTP_METHOD_POST, null, properties,
                organizationId, applicationId, properties.get("type").toString());
        return response;
    }
    
    /**
  	 * Create a new entity on the server. Executes asynchronously in background
  	 * and the callbacks are called in the UI thread.
  	 * 
  	 * @param entity
  	 * @param callback
  	 */
  	public void createEntityAsync(final Entity entity,
  			final ApiResponseCallback callback) {
  		(new ClientAsyncTask<ApiResponse>(callback) {
  			@Override
  			public ApiResponse doTask() {
  				return createEntity(entity);
  			}
  		}).execute();
  	}

  	
  	/**
  	 * Create a new entity on the server from a set of properties. Properties
  	 * must include a "type" property. Executes asynchronously in background and
  	 * the callbacks are called in the UI thread.
  	 * 
  	 * @param properties
  	 * @param callback
  	 */
  	public void createEntityAsync(final Map<String, Object> properties,
  			final ApiResponseCallback callback) {
  		(new ClientAsyncTask<ApiResponse>(callback) {
  			@Override
  			public ApiResponse doTask() {
  				return createEntity(properties);
  			}
  		}).execute();
  	}
  	
  	/**
  	 * Create a set of entities on the server from an ArrayList. Each item in the array
  	 * contains a set of properties that define a entity.
  	 * 
  	 * @param type
  	 * @param entities
  	 */
  	public ApiResponse createEntities(String type, ArrayList<Map<String, Object>> entities) {
        assertValidApplicationId();                
        if (isEmpty(type)) {
            throw new IllegalArgumentException("Missing entity type");
        }
        ApiResponse response = apiRequest(HTTP_METHOD_POST, null, entities,
   		     organizationId, applicationId, type);	           		
   		return response;	
    }
    
    /**
  	 * Create a set of entities on the server from an ArrayList. Each item in the array
  	 * contains a set of properties that define a entity. Executes asynchronously in 
  	 * background and the callbacks are called in the UI thread.
  	 * 
  	 * @param type
  	 * @param entities
  	 */
    public void createEntitiesAsync(final String type, final ArrayList<Map<String, Object>> entities,
  			final ApiResponseCallback callback) {
  		(new ClientAsyncTask<ApiResponse>(callback) {
  			@Override
  			public ApiResponse doTask() {
  				return createEntities(type, entities);
  			}
  		}).execute();
  	}

    /**
     * Creates an object instance that corresponds to the provided entity type.
     * Supported object types are Activity, Device, Group, Message, and User.
     * All other types will return a generic Entity instance with no type assigned.
     *
     * @param  type  the entity type to an object instance of
     * @return  an object instance that corresponds to the type provided
    */
    public Entity createTypedEntity(String type) {
        Entity entity = null;
        
        if( Activity.isSameType(type) ) {
            entity = new Activity(this);
        } else if( Device.isSameType(type) ) {
            entity = new Device(this);
        } else if( Group.isSameType(type) ) {
            entity = new Group(this);
        } else if( Message.isSameType(type) ) {
            entity = new Message(this);
        } else if( User.isSameType(type) ) {
            entity = new User(this);
        } else {
            entity = new Entity(this);
        }
        
        return entity;
    }

    /**
     * Requests all entities of specified type that match the provided query string.
     *
     * @param  type  the entity type to be retrieved
     * @param  queryString  a query string to send with the request
     * @return  a non-null ApiResponse object if successful
    */
    public ApiResponse getEntities(String type,String queryString)
    {
        Map<String, Object> params = null;

        if (queryString.length() > 0) {
            params = new HashMap<String, Object>();
            params.put("ql", queryString);
        }
        
        return apiRequest(HTTP_METHOD_GET, // method
                            params, // params
                            null, // data
                            organizationId,
                            applicationId,
                            type);
    }

    /**
     * Asynchronously requests all entities of specified type that match the provided query string.
     *
     * @param  type  the entity type to be retrieved
     * @param  queryString  a query string to send with the request
     * @param  callback an ApiResponseCallback to handle the async response
    */
    public void getEntitiesAsync(final String type,
            final String queryString, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return getEntities(type, queryString);
            }
        }).execute();
    }

    /**
     * Update an existing entity on the server.
     * 
     * @param entityID the entity to update
     * @param updatedProperties the new properties
     * @return an ApiResponse with the updated entity in it.
     */
    public ApiResponse updateEntity(String entityID, Map<String, Object> updatedProperties) {
    	assertValidApplicationId();
    	if (isEmpty(updatedProperties.get("type"))) {
            throw new IllegalArgumentException("Missing entity type");
    	}
    	ApiResponse response = apiRequest(HTTP_METHOD_PUT, null, updatedProperties,
    			organizationId, applicationId, updatedProperties.get("type").toString(), entityID);
    	return response;
    }

    
    /**
     * Update an existing entity on the server. Properties
     * must include a "type" property. Executes asynchronously in background and
     * the callbacks are called in the UI thread.
     *
     * @param entityID
     * @param updatedProperties
     * @param callback
     */
    public void updateEntityAsync(final String entityID, final Map<String, Object> updatedProperties,
                                      final ApiResponseCallback callback) {
          (new ClientAsyncTask<ApiResponse>(callback) {
        	  @Override
        	  public ApiResponse doTask() {
        		  return updateEntity(entityID, updatedProperties);
        	  }
          }).execute();
    }

    /**
     * Updates the password associated with a user entity
     *
     * @param  usernameOrEmail  the username or email address associated with the user entity
     * @param  oldPassword  the user's old password
     * @param  newPassword  the user's new password
     * @return an ApiResponse with the updated entity in it.
     */
    public ApiResponse updateUserPassword(String usernameOrEmail, String oldPassword, String newPassword) {
    	Map<String,Object> updatedProperties = new HashMap<String,Object>();
    	updatedProperties.put("oldpassword", oldPassword);
    	updatedProperties.put("newpassword", newPassword);
    	return apiRequest(HTTP_METHOD_POST, null, updatedProperties,
    			organizationId, applicationId, "users", usernameOrEmail);
    }

    
    /**
     * Remove an existing entity on the server.
     * 
     * @param entityType the collection of the entity
     * @param entityID the specific entity to delete
     * @return an ApiResponse indicating whether the removal was successful
     */
    public ApiResponse removeEntity(String entityType, String entityID) {
    	assertValidApplicationId();
    	if (isEmpty(entityType)) {
            throw new IllegalArgumentException("Missing entity type");
    	}
    	ApiResponse response = apiRequest(HTTP_METHOD_DELETE, null, null,
    			organizationId, applicationId, entityType, entityID);
    	return response;
    }
    
    /**
     * Remove an existing entity on the server.
     * Executes asynchronously in background and
     * the callbacks are called in the UI thread.
     *
     * @param entityType
     * @param entityID
     * @param callback
     */
    public void removeEntityAsync(final String entityType, final String entityID,
    								final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
            	return removeEntity(entityType, entityID);
    		}
    	}).execute();
    }
    
    /**
     * Perform a query request and return a query object. The Query object
     * provides a simple way of dealing with result sets that need to be
     * iterated or paged through.
     * 
     * @param method
     * @param params
     * @param data
     * @param segments
     * @return
     */
    public Query queryEntitiesRequest(String httpMethod,
            Map<String, Object> params, Object data, String... segments) {
        ApiResponse response = apiRequest(httpMethod, params, data, segments);
        return new EntityQuery(response, httpMethod, params, data, segments);
    }

    /**
     * Perform a query request and return a query object. The Query object
     * provides a simple way of dealing with result sets that need to be
     * iterated or paged through. Executes asynchronously in background and the
     * callbacks are called in the UI thread.
     * 
     * @param callback
     * @param method
     * @param params
     * @param data
     * @param segments
     */
    public void queryEntitiesRequestAsync(final QueryResultsCallback callback,
            final String httpMethod, final Map<String, Object> params,
            final Object data, final String... segments) {
        (new ClientAsyncTask<Query>(callback) {
            @Override
            public Query doTask() {
                return queryEntitiesRequest(httpMethod, params, data, segments);
            }
        }).execute();
    }

    /**
     * Query object
     * 
     */
    private class EntityQuery implements Query {
        final String httpMethod;
        final Map<String, Object> params;
        final Object data;
        final String[] segments;
        final ApiResponse response;

        private EntityQuery(ApiResponse response, String httpMethod,
                Map<String, Object> params, Object data, String[] segments) {
            this.response = response;
            this.httpMethod = httpMethod;
            this.params = params;
            this.data = data;
            this.segments = segments;
        }

        private EntityQuery(ApiResponse response, EntityQuery q) {
            this.response = response;
            httpMethod = q.httpMethod;
            params = q.params;
            data = q.data;
            segments = q.segments;
        }

        /**
         * @return the api response of the last request
         */
        public ApiResponse getResponse() {
            return response;
        }

        /**
         * @return true if the server indicates more results are available
         */
        public boolean more() {
            if ((response != null) && (response.getCursor() != null)
                    && (response.getCursor().length() > 0)) {
                return true;
            }
            return false;
        }

        /**
         * Performs a request for the next set of results
         * 
         * @return query that contains results and where to get more from.
         */
        public Query next() {
            if (more()) {
                Map<String, Object> nextParams = null;
                if (params != null) {
                    nextParams = new HashMap<String, Object>(params);
                } else {
                    nextParams = new HashMap<String, Object>();
                }
                nextParams.put("cursor", response.getCursor());
                ApiResponse nextResponse = apiRequest(httpMethod, nextParams, data,
                        segments);
                return new EntityQuery(nextResponse, this);
            }
            return null;
        }

    }


    /****************** USER ENTITY MANAGEMENT ***********************/
    /****************** USER ENTITY MANAGEMENT ***********************/

    /**
     * Creates a user.
     * 
     * @param username
     *            required
     * @param name
     * @param email
     * @param password
     * @return
     */
    public ApiResponse createUser(String username, String name, String email,
            String password) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "user");
        if (username != null) {
            properties.put("username", username);
        }
        if (name != null) {
            properties.put("name", name);
        }
        if (email != null) {
            properties.put("email", email);
        }
        if (password != null) {
            properties.put("password", password);
        }
        return createEntity(properties);
    }

	/**
	 * Creates a user. Executes asynchronously in background and the callbacks
	 * are called in the UI thread.
	 * 
	 * @param username
	 * @param name
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void createUserAsync(final String username, final String name,
			final String email, final String password,
			final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return createUser(username, name, email, password);
			}
		}).execute();
	}

    /**
     * Perform a query of the users collection.
     * 
     * @return
     */
    public Query queryUsers() {
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, null, null,
                organizationId,  applicationId, "users");
        return q;
    }
    
    /**
     * Perform a query of the users collection. Executes asynchronously in
     * background and the callbacks are called in the UI thread.
     * 
     * @param callback The object with methods to call with the query response.
     */
    public void queryUsersAsync(QueryResultsCallback callback) {
        queryEntitiesRequestAsync(callback, HTTP_METHOD_GET, null, null,
                organizationId, applicationId, "users");
    }


    /**
     * Perform a query of the users collection using the provided query command.
     * For example: "name contains 'ed'".
     * 
     * @param ql
     * @return
     */
    public Query queryUsers(String ql) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ql", ql);
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, params, null,organizationId,
                applicationId, "users");
        return q;
    }

    /**
     * Perform a query of the users collection using the provided query command.
     * For example: "name contains 'ed'". Executes asynchronously in background
     * and the callbacks are called in the UI thread.
     * 
     * @param ql A query string.
     * @param callback The object with methods to call with the query response.
     */
    public void queryUsersAsync(String ql, QueryResultsCallback callback) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ql", ql);
        queryEntitiesRequestAsync(callback, HTTP_METHOD_GET, params, null, 
                organizationId, applicationId, "users");
    }
    
    /**
     * Perform a query of the users collection within the specified distance of
     * the specified location and optionally using the provided query command.
     * For example: "name contains 'ed'".
     * 
     * @param distance
     * @param location
     * @param ql
     * @return
     */
    public Query queryUsersWithinLocation(float distance, float lattitude,
            float longitude, String ql) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ql",
                this.makeLocationQL(distance, lattitude, longitude, ql));
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, params, null, organizationId,
                applicationId, "users");
        return q;
    }

    
    /****************** GROUP ENTITY MANAGEMENT ***********************/
    /****************** GROUP ENTITY MANAGEMENT ***********************/

    /**
     * Creates a group with the specified group path. Group paths can be slash
     * ("/") delimited like file paths for hierarchical group relationships.
     * 
     * @param groupPath The path to use for the new group.
     * @return Results of the operation.
     */
    public ApiResponse createGroup(String groupPath) {
        return createGroup(groupPath, null);
    }

    /**
     * Creates a group with the specified group path. Group paths can be slash
     * ("/") delimited like file paths for hierarchical group relationships.
     * Executes asynchronously in background and the callbacks are called in the
     * UI thread.
     * 
     * @param groupPath The path to use for the new group.
     * @param callback The object with methods to call with the response.
     */
    public void createGroupAsync(String groupPath,
            final ApiResponseCallback callback) {
        createGroupAsync(groupPath, null);
    }

    /**
     * Creates a group with the specified group path and group title. Group
     * paths can be slash ("/") delimited like file paths for hierarchical group
     * relationships.
     * 
     * @param groupPath The path to use for the new group.
     * @param groupTitle The title to use for the new group.
     * @return Results of the operation.
     */
    public ApiResponse createGroup(String groupPath, String groupTitle) {
     return createGroup(groupPath, groupTitle, null);  
    }
    
    /**
     * Creates a group with the specified group path and group title. Group
     * paths can be slash ("/") deliminted like file paths for hierarchical
     * group relationships. Executes asynchronously in background and the
     * callbacks are called in the UI thread.
     * 
     * @param groupPath The path to use for the new group.
     * @param groupTitle The title to use for the new group.
     * @param callback The object with methods to call with the response.
     */
    public void createGroupAsync(final String groupPath,
            final String groupTitle, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return createGroup(groupPath, groupTitle);
            }
        }).execute();
    }

    /**
     * Create a group with a path, title and name
     * @param groupPath The path to use for the new group.
     * @param groupTitle The title to use for the new group.
     * @param groupName The name to use for the new group.
     * @return
     */
    public ApiResponse createGroup(String groupPath, String groupTitle, String groupName){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", "group");
        data.put("path", groupPath);
        
        if (groupTitle != null) {
            data.put("title", groupTitle);
        }
        
        if(groupName != null){
            data.put("name", groupName);
        }
        
        return apiRequest(HTTP_METHOD_POST, null, data,  organizationId, applicationId, "groups");
    }

    /**
     * Get the groups for the user.
     * 
     * @param userId
     * @return a map with the group path as the key and the Group entity as the
     *         value
     */
    public Map<String, Group> getGroupsForUser(String userId) {
        ApiResponse response = apiRequest(HTTP_METHOD_GET, null, null,
                organizationId, applicationId, "users", userId, "groups");
        Map<String, Group> groupMap = new HashMap<String, Group>();
        if (response != null) {
            List<Group> groups = response.getEntities(Group.class);
            for (Group group : groups) {
                groupMap.put(group.getPath(), group);
            }
        }
        return groupMap;
    }

	/**
	 * Get the groups for the user. Executes asynchronously in background and
	 * the callbacks are called in the UI thread.
	 * 
	 * @param userId
	 * @param callback
	 */
	public void getGroupsForUserAsync(final String userId,
			final GroupsRetrievedCallback callback) {
		(new ClientAsyncTask<Map<String, Group>>(callback) {
			@Override
			public Map<String, Group> doTask() {
				return getGroupsForUser(userId);
			}
		}).execute();
	}

    /**
     * Queries the users for the specified group.
     * 
     * @param groupId ID of the group for which to find users.
     * @return Results of the query.
     */
    public Query queryUsersForGroup(String groupId) {
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, null, null, organizationId,
                applicationId, "groups", groupId, "users");
        return q;
    }

    /**
     * Queries the users for the specified group. Executes asynchronously in
     * background and the callbacks are called in the UI thread.
     * 
     * @param groupId The ID of the group from which to find users.
     * @param callback The object with methods to call with the query response.
     */
    public void queryUsersForGroupAsync(String groupId,
            QueryResultsCallback callback) {
        queryEntitiesRequestAsync(callback, HTTP_METHOD_GET, null, null,
                getApplicationId(), "groups", groupId, "users");
    }

    /**
     * Adds a user to the specified groups.
     * 
     * @param userId ID of the user to add.
     * @param groupId ID of the group. 
     * @return Results of the request.
     */
    public ApiResponse addUserToGroup(String userId, String groupId) {
        return apiRequest(HTTP_METHOD_POST, null, null, organizationId,  applicationId, "groups",
                groupId, "users", userId);
    }

    /**
     * Adds a user to the specified groups. Executes asynchronously in
     * background and the callbacks are called in the UI thread.
     * 
     * @param userId ID of the user to add.
     * @param groupId ID of the group.
     * @param callback The object with methods to call with the response.
     */
    public void addUserToGroupAsync(final String userId, final String groupId,
            final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return addUserToGroup(userId, groupId);
            }
        }).execute();
    }


    /****************** ACTIVITY ENTITY MANAGEMENT ***********************/
    /****************** ACTIVITY ENTITY MANAGEMENT ***********************/

    /**
     * Get a user's activity feed. Returned as a query to ease paging.
     * 
     * @param userId
     * @return
     */
    public Query queryActivityFeedForUser(String userId) {
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, null, null,
                organizationId, applicationId, "users", userId, "feed");
        return q;
    }
    
	/**
	 * Get a user's activity feed. Returned as a query to ease paging. Executes
	 * asynchronously in background and the callbacks are called in the UI
	 * thread.
	 * 
	 * 
	 * @param userId
	 * @param callback
	 */
	public void queryActivityFeedForUserAsync(final String userId, final QueryResultsCallback callback) {
		(new ClientAsyncTask<Query>(callback) {
			@Override
			public Query doTask() {
				return queryActivityFeedForUser(userId);
			}
		}).execute();
	}


    /**
     * Posts an activity to a user. Activity must already be created.
     * 
     * @param userId
     * @param activity
     * @return
     */
    public ApiResponse postUserActivity(String userId, Activity activity) {
        return apiRequest(HTTP_METHOD_POST, null, activity,  organizationId, applicationId, "users",
                userId, "activities");
    }

    /**
     * Creates and posts an activity to a user.
     * 
     * @param verb
     * @param title
     * @param content
     * @param category
     * @param user
     * @param object
     * @param objectType
     * @param objectName
     * @param objectContent
     * @return
     */
    public ApiResponse postUserActivity(String verb, String title,
            String content, String category, User user, Entity object,
            String objectType, String objectName, String objectContent) {
        Activity activity = Activity.newActivity(this, verb, title, content,
                category, user, object, objectType, objectName, objectContent);
        return postUserActivity(user.getUuid().toString(), activity);
    }

	/**
	 * Creates and posts an activity to a user. Executes asynchronously in
	 * background and the callbacks are called in the UI thread.
	 * 
	 * @param verb
	 * @param title
	 * @param content
	 * @param category
	 * @param user
	 * @param object
	 * @param objectType
	 * @param objectName
	 * @param objectContent
	 * @param callback
	 */
	public void postUserActivityAsync(final String verb, final String title,
			final String content, final String category, final User user,
			final Entity object, final String objectType,
			final String objectName, final String objectContent,
			final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return postUserActivity(verb, title, content, category, user,
						object, objectType, objectName, objectContent);
			}
		}).execute();
	}

    /**
     * Posts an activity to a group. Activity must already be created.
     * 
     * @param userId
     * @param activity
     * @return
     */
    public ApiResponse postGroupActivity(String groupId, Activity activity) {
        return apiRequest(HTTP_METHOD_POST, null, activity, organizationId, applicationId, "groups",
                groupId, "activities");
    }

    /**
     * Creates and posts an activity to a group.
     * 
     * @param groupId
     * @param verb
     * @param title
     * @param content
     * @param category
     * @param user
     * @param object
     * @param objectType
     * @param objectName
     * @param objectContent
     * @return
     */
    public ApiResponse postGroupActivity(String groupId, String verb, String title,
            String content, String category, User user, Entity object,
            String objectType, String objectName, String objectContent) {
        Activity activity = Activity.newActivity(this, verb, title, content,
                category, user, object, objectType, objectName, objectContent);
        return postGroupActivity(groupId, activity);
    }

	/**
	 * Creates and posts an activity to a group. Executes asynchronously in
	 * background and the callbacks are called in the UI thread.
	 * 
	 * @param groupId
	 * @param verb
	 * @param title
	 * @param content
	 * @param category
	 * @param user
	 * @param object
	 * @param objectType
	 * @param objectName
	 * @param objectContent
	 * @param callback
	 */
	public void postGroupActivityAsync(final String groupId, final String verb, final String title,
			final String content, final String category, final User user,
			final Entity object, final String objectType,
			final String objectName, final String objectContent,
			final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return postGroupActivity(groupId, verb, title, content, category, user,
						object, objectType, objectName, objectContent);
			}
		}).execute();
	}

    /**
     * Post an activity to the stream.
     * 
     * @param activity
     * @return
     */
    public ApiResponse postActivity(Activity activity) {
        return createEntity(activity);
    }

    /**
     * Creates and posts an activity to a group.
     * 
     * @param verb
     * @param title
     * @param content
     * @param category
     * @param user
     * @param object
     * @param objectType
     * @param objectName
     * @param objectContent
     * @return
     */
    public ApiResponse postActivity(String verb, String title,
            String content, String category, User user, Entity object,
            String objectType, String objectName, String objectContent) {
        Activity activity = Activity.newActivity(this, verb, title, content,
                category, user, object, objectType, objectName, objectContent);
        return createEntity(activity);
    }
    
    /**
     * Get a group's activity feed. Returned as a query to ease paging.
     * 
     * @param userId
     * @return
     */
    public Query queryActivity() {
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, null, null,
               organizationId, applicationId, "activities");
        return q;
    }

    

    /**
     * Get a group's activity feed. Returned as a query to ease paging.
     * 
     * @param userId
     * @return
     */
    public Query queryActivityFeedForGroup(String groupId) {
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, null, null,
                organizationId,  applicationId, "groups", groupId, "feed");
        return q;
    }

    /**
     * Get a group's activity feed. Returned as a query to ease paging. Executes
     * asynchronously in background and the callbacks are called in the UI
     * thread.
     * 
     * 
     * @param userId
     * @param callback
     */
    public void queryActivityFeedForGroupAsync(final String groupId,
            final QueryResultsCallback callback) {
        (new ClientAsyncTask<Query>(callback) {
            @Override
            public Query doTask() {
                return queryActivityFeedForGroup(groupId);
            }
        }).execute();
    }
    

    /****************** ENTITY CONNECTIONS ***********************/
    /****************** ENTITY CONNECTIONS ***********************/

    /**
     * Connect two entities together.
     * 
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntityId
     * @return
     */
    public ApiResponse connectEntities(String connectingEntityType,
            String connectingEntityId, String connectionType,
            String connectedEntityId) {
        return apiRequest(HTTP_METHOD_POST, null, null,  organizationId, applicationId,
                connectingEntityType, connectingEntityId, connectionType,
                connectedEntityId);
    }
    
    /**
     * Connect two entities together
     * @param connectorType
     * @param connectorID
     * @param connectionType
     * @param connecteeType
     * @param connecteeID
     * @return
     */
    public ApiResponse connectEntities(String connectorType,
    		String connectorID,
    		String connectionType,
    		String connecteeType,
    		String connecteeID) {
		return apiRequest(HTTP_METHOD_POST, null, null, organizationId, applicationId,
				connectorType, connectorID, connectionType, connecteeType, connecteeID);
    }


	/**
	 * Connect two entities together. Executes asynchronously in background and
	 * the callbacks are called in the UI thread.
	 * 
	 * @param connectingEntityType
	 * @param connectingEntityId
	 * @param connectionType
	 * @param connectedEntityId
	 * @param callback
	 */
	public void connectEntitiesAsync(final String connectingEntityType,
			final String connectingEntityId, final String connectionType,
			final String connectedEntityId, final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return connectEntities(connectingEntityType,
						connectingEntityId, connectionType, connectedEntityId);
			}
		}).execute();
	}
	
    /**
     * Disconnect two entities.
     * 
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param connectedEntityId
     * @return
     */
    public ApiResponse disconnectEntities(String connectingEntityType,
            String connectingEntityId, String connectionType,
            String connectedEntityId) {
        return apiRequest(HTTP_METHOD_DELETE, null, null,  organizationId, applicationId,
                connectingEntityType, connectingEntityId, connectionType,
                connectedEntityId);
    }

	/**
	 * Disconnect two entities. Executes asynchronously in background and the
	 * callbacks are called in the UI thread.
	 * 
	 * @param connectingEntityType
	 * @param connectingEntityId
	 * @param connectionType
	 * @param connectedEntityId
	 * @param callback
	 */
	public void disconnectEntitiesAsync(final String connectingEntityType,
			final String connectingEntityId, final String connectionType,
			final String connectedEntityId, final ApiResponseCallback callback) {
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return connectEntities(connectingEntityType,
						connectingEntityId, connectionType, connectedEntityId);
			}
		}).execute();
	}
	
    /**
     * Query the connected entities.
     * 
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param ql
     * @return
     */
    public Query queryEntityConnections(String connectingEntityType,
            String connectingEntityId, String connectionType, String ql) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ql", ql);
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, params, null,
                organizationId, applicationId, connectingEntityType, connectingEntityId,
                connectionType);
        return q;
    }

	/**
	 * Query the connected entities. Executes asynchronously in background and
	 * the callbacks are called in the UI thread.
	 * 
	 * @param connectingEntityType
	 * @param connectingEntityId
	 * @param connectionType
	 * @param ql
	 * @param callback
	 */
	public void queryEntityConnectionsAsync(String connectingEntityType,
			String connectingEntityId, String connectionType, String ql,
			QueryResultsCallback callback) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ql", ql);
		queryEntitiesRequestAsync(callback, HTTP_METHOD_GET, params, null,
				getApplicationId(), connectingEntityType, connectingEntityId,
				connectionType);
	}
	
    protected String makeLocationQL(float distance, double lattitude,
            double longitude, String ql) {
        String within = String.format("within %d of %d , %d", distance,
                lattitude, longitude);
        ql = ql == null ? within : within + " and " + ql;
        return ql;
    }

    /**
     * Query the connected entities within distance of a specific point.
     * 
     * @param connectingEntityType
     * @param connectingEntityId
     * @param connectionType
     * @param distance
     * @param latitude
     * @param longitude
     * @return
     */
    public Query queryEntityConnectionsWithinLocation(
            String connectingEntityType, String connectingEntityId,
            String connectionType, float distance, float lattitude,
            float longitude, String ql) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ql", makeLocationQL(distance, lattitude, longitude, ql));
        Query q = queryEntitiesRequest(HTTP_METHOD_GET, params, null, organizationId,
                applicationId, connectingEntityType, connectingEntityId,
                connectionType);
        return q;
    }

	/**
	 * Query the connected entities within distance of a specific point. .
	 * Executes asynchronously in background and the callbacks are called in the
	 * UI thread.
	 * 
	 * @param connectingEntityType
	 * @param connectingEntityId
	 * @param connectionType
	 * @param distance
	 * @param latitude
	 * @param longitude
	 * @param callback
	 */
	public void queryEntityConnectionsWithinLocationAsync(
			String connectingEntityType, String connectingEntityId,
			String connectionType, float distance, Location location,
			String ql, QueryResultsCallback callback) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ql", makeLocationQL(distance, location.getLatitude(), location.getLongitude(), ql));
		params.put("ql", ql);
		queryEntitiesRequestAsync(callback, HTTP_METHOD_GET, params, null,
				getApplicationId(), connectingEntityType, connectingEntityId,
				connectionType);
	}
	
	public void captureDeviceLocation() {
		/*
		if (this.context != null) {
			if (locationManager == null) {
				locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			}
			
			if (locationManager != null) {
				try {
					String locationProvider = LocationManager.GPS_PROVIDER;
					if ( locationManager.isProviderEnabled( locationProvider ) ) {
						locationManager.requestLocationUpdates(locationProvider,
								5000,          // milliseconds between location updates (minimum)
								(float) 10.0,  // minimum distance in meters between location updates (minimum)
								this,          // location update listener
								Looper.getMainLooper());  // message thread for receiving location updates
					}
				} catch (SecurityException e) {
					// ignore it -- developer probably didn't add ACCESS_FINE_LOCATION to manifest
				}
			}
		}
		*/
	}
	
	@Override
    public void onLocationChanged(Location location) {
		/*
	    if (location != null) {
	    	locationManager.removeUpdates(this);
	        
	        final UUID deviceId = getUniqueDeviceID();
	        
	        final Map<String,Object> entity = new HashMap<String,Object>();
	        entity.put("type", "device");
	        entity.put("uuid", deviceId);
	        
	        // grab device meta-data
	        entity.put("deviceModel", Build.MODEL);
	        entity.put("devicePlatform", "android");
	        entity.put("deviceOSVersion", Build.VERSION.RELEASE);

	        Map<String,Object> locationData = new HashMap<String,Object>();
	        locationData.put("latitude", new Double(location.getLatitude()));
	        locationData.put("longitude", new Double(location.getLongitude()));
	        entity.put("location", locationData);

	        // perform the update on a background thread
	        new AsyncTask<Void, Void, Void>() {

	            @Override
	            protected Void doInBackground(Void... params)
	            {
	    	        updateEntity(deviceId.toString(), entity);
					return null;
	            }

	        }.execute();
	        
	    }
	    */
    }

    
    /****************** MESSAGE QUEUES ***********************/
    /****************** MESSAGE QUEUES ***********************/

    private String normalizeQueuePath(String path) {
        return arrayToDelimitedString(
                tokenizeToStringArray(path, "/", true, true), "/");
    }

    public ApiResponse postMessage(String path, Map<String, Object> message) {
        return apiRequest(HTTP_METHOD_POST, null, message, organizationId,  applicationId,
                "queues", normalizeQueuePath(path));
    }

    public ApiResponse postMessage(String path,
            List<Map<String, Object>> messages) {
        return apiRequest(HTTP_METHOD_POST, null, messages,  organizationId, applicationId,
                "queues", normalizeQueuePath(path));
    }

    public enum QueuePosition {
        START("start"), END("end"), LAST("last"), CONSUMER("consumer");

        private final String shortName;

        QueuePosition(String shortName) {
            this.shortName = shortName;
        }

        static Map<String, QueuePosition> nameMap = new ConcurrentHashMap<String, QueuePosition>();

        static {
            for (QueuePosition op : EnumSet.allOf(QueuePosition.class)) {
                if (op.shortName != null) {
                    nameMap.put(op.shortName, op);
                }
            }
        }

        public static QueuePosition find(String s) {
            if (s == null) {
                return null;
            }
            return nameMap.get(s);
        }

        @Override
        public String toString() {
            return shortName;
        }
    }

    public ApiResponse getMessages(String path, String consumer, UUID last,
            Long time, Integer prev, Integer next, Integer limit,
            QueuePosition pos, Boolean update, Boolean sync) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (consumer != null) {
            params.put("consumer", consumer);
        }
        if (last != null) {
            params.put("last", last);
        }
        if (time != null) {
            params.put("time", time);
        }
        if (prev != null) {
            params.put("prev", prev);
        }
        if (next != null) {
            params.put("next", next);
        }
        if (limit != null) {
            params.put("limit", limit);
        }
        if (pos != null) {
            params.put("pos", pos.toString());
        }
        if (update != null) {
            params.put("update", update);
        }
        if (sync != null) {
            params.put("synchronized", sync);
        }
        return apiRequest(HTTP_METHOD_GET, params, null,  organizationId, applicationId,
                "queues", normalizeQueuePath(path));
    }

    public ApiResponse addSubscriber(String publisherQueue,
            String subscriberQueue) {
        return apiRequest(HTTP_METHOD_POST, null, null, organizationId,  applicationId, "queues",
                normalizeQueuePath(publisherQueue), "subscribers",
                normalizeQueuePath(subscriberQueue));
    }

    public ApiResponse removeSubscriber(String publisherQueue,
            String subscriberQueue) {
        return apiRequest(HTTP_METHOD_DELETE, null, null, organizationId,  applicationId,
                "queues", normalizeQueuePath(publisherQueue), "subscribers",
                normalizeQueuePath(subscriberQueue));
    }
    
    private class QueueQuery implements Query {
        final String httpMethod;
        final Map<String, Object> params;
        final Object data;
        final String queuePath;
        final ApiResponse response;

        private QueueQuery(ApiResponse response, String httpMethod,
                Map<String, Object> params, Object data, String queuePath) {
            this.response = response;
            this.httpMethod = httpMethod;
            this.params = params;
            this.data = data;
            this.queuePath = normalizeQueuePath(queuePath);
        }

        private QueueQuery(ApiResponse response, QueueQuery q) {
            this.response = response;
            httpMethod = q.httpMethod;
            params = q.params;
            data = q.data;
            queuePath = q.queuePath;
        }

        /**
         * @return the api response of the last request
         */
        public ApiResponse getResponse() {
            return response;
        }

        /**
         * @return true if the server indicates more results are available
         */
        public boolean more() {
            if ((response != null) && (response.getCursor() != null)
                    && (response.getCursor().length() > 0)) {
                return true;
            }
            return false;
        }

        /**
         * Performs a request for the next set of results
         * 
         * @return query that contains results and where to get more from.
         */
        public Query next() {
            if (more()) {
                Map<String, Object> nextParams = null;
                if (params != null) {
                    nextParams = new HashMap<String, Object>(params);
                } else {
                    nextParams = new HashMap<String, Object>();
                }
                nextParams.put("start", response.getCursor());
                ApiResponse nextResponse = apiRequest(httpMethod, nextParams, data,
                        queuePath);
                return new QueueQuery(nextResponse, this);
            }
            return null;
        }

    }

    public Query queryQueuesRequest(String httpMethod,
            Map<String, Object> params, Object data, String queuePath) {
        ApiResponse response = apiRequest(httpMethod, params, data, queuePath);
        return new QueueQuery(response, httpMethod, params, data, queuePath);
    }
    
    
    /****************** COLLECTION MANAGEMENT ***********************/
    /****************** COLLECTION MANAGEMENT ***********************/

    public Collection getCollection(String type)
    {
        return getCollection(type,null);
    }

    public Collection getCollection(String type,Map<String,Object> qs)
    {
        return new Collection(this,type,qs);
    }

    public void getCollectionAsync(final String type, final Map<String,Object> qs,
		final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return getCollection(type, qs).fetch();
            }
        }).execute();
    }
    

    /****************** EVENT ENTITY MANAGEMENT ***********************/
    /****************** EVENT ENTITY MANAGEMENT ***********************/

	public ApiResponse createEvent(Map<String,Object> mapEvent)
	{
		if (mapEvent != null) {
			boolean needTypeSet = true;

			if (mapEvent.containsKey("type")) {
				Object typeValue = mapEvent.get("type");
				if (typeValue != null) {
					if (typeValue instanceof String) {
						String typeValueString = (String) typeValue;
						if (typeValueString.equals("event") || typeValueString.equals("events")) {
							needTypeSet = false;
						}
					}
				}
			}
			
			if (needTypeSet) {
				mapEvent.put("type", "events");
			}
			
			if (!mapEvent.containsKey("timestamp")) {
				// let the server assign the timestamp
				mapEvent.put("timestamp", "0");
			}
		}
		
		return createEntity(mapEvent);
	}
	
	public ApiResponse createEvent(Map<String,Object> mapEvent, Date timestamp)
	{
		if (mapEvent == null) {
			mapEvent = new HashMap<String,Object>();
		}

		populateTimestamp(timestamp, mapEvent);
		return createEvent(mapEvent);
	}
	
	public ApiResponse createEvent(Map<String,Object> mapEvent, CounterIncrement counterIncrement)
	{
		if (mapEvent == null) {
			mapEvent = new HashMap<String,Object>();
		}
		
		populateTimestamp(null, mapEvent);
		populateCounter(counterIncrement, mapEvent);
		return createEvent(mapEvent);
	}
	
	public void createEventAsync(final Map<String,Object> mapEvent, final CounterIncrement counterIncrement, final ApiResponseCallback callback)
	{
		(new ClientAsyncTask<ApiResponse>(callback) {
			@Override
			public ApiResponse doTask() {
				return createEvent(mapEvent,counterIncrement);
			}
		}).execute();
	}
	
	public ApiResponse createEvent(Map<String,Object> mapEvent, Date timestamp, CounterIncrement counterIncrement)
	{
		if (mapEvent == null) {
			mapEvent = new HashMap<String,Object>();
		}

		populateTimestamp(timestamp, mapEvent);
		populateCounter(counterIncrement, mapEvent);
		return createEvent(mapEvent);
	}
	
	public ApiResponse createEvent(Map<String,Object> mapEvent, Date timestamp, List<CounterIncrement> counterIncrements)
	{
		if (mapEvent == null) {
			mapEvent = new HashMap<String,Object>();
		}

		populateTimestamp(timestamp, mapEvent);
		
		if (counterIncrements != null) {
			for (CounterIncrement counterIncrement : counterIncrements) {
				populateCounter(counterIncrement, mapEvent);
			}
		}
		
		return createEvent(mapEvent);
	}

    protected void populateTimestamp(Date timestamp, Map<String,Object> mapEvent)
    {
        if (timestamp != null) {
            mapEvent.put("timestamp", "" + timestamp.getTime());
        } else {
            // let the server assign the timestamp
            mapEvent.put("timestamp", "0");
        }
    }
    
    protected void populateCounter(CounterIncrement counterIncrement, Map<String,Object> mapEvent)
    {
        if ((counterIncrement != null) && (mapEvent != null)) {
            String counterName = counterIncrement.getCounterName();
            if ((counterName != null) && (counterName.length() > 0)) {
                Map<String, Object> mapCounters = null;
                
                Object existingCounters = mapEvent.get("counters");
                
                if (existingCounters != null) {
                    if (existingCounters instanceof Map) {
                        try {
                            mapCounters = (Map<String,Object>) existingCounters;
                        } catch (Throwable t) {
                            mapCounters = null;
                        }
                    }
                }
                
                if (null == mapCounters) {
                    mapCounters = new HashMap<String,Object>();
                }
                
                mapCounters.put(counterName, new Long(counterIncrement.getCounterIncrementValue()));
                mapEvent.put("counters", mapCounters);
            }
        }
    }
}
