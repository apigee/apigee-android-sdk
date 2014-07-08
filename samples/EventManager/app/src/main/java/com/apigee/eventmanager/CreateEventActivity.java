package com.apigee.eventmanager;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateEventActivity extends Activity {

    private static String TAG = "CreateEventActivity";

    private Button addEventButton;
    private Button cancelButton;
    private EditText eventNameEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private Switch publicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        addEventButton = (Button) this.findViewById(R.id.addEventButton);
        cancelButton = (Button) this.findViewById(R.id.cancelButton);
        eventNameEditText = (EditText) this.findViewById(R.id.eventNameEditText);
        cityEditText = (EditText) this.findViewById(R.id.cityEditText);
        stateEditText = (EditText) this.findViewById(R.id.stateEditText);
        publicSwitch = (Switch) this.findViewById(R.id.publicSwitch);

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventActivity.this.finish();
            }
        });

        addEventButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameText = eventNameEditText.getText().toString();
                String cityText = cityEditText.getText().toString();
                String stateText = stateEditText.getText().toString();
                if( !eventNameText.isEmpty() && !cityText.isEmpty() && !stateText.isEmpty() ) {

                    double latitude = 0.0;
                    double longitude = 0.0;
                    if(Geocoder.isPresent()){
                        try {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addressList= geocoder.getFromLocationName(cityText + ", " + stateText, 1);
                            if( addressList != null && addressList.size() > 0 ) {
                                Address address = addressList.get(0);
                                latitude = address.getLatitude();
                                longitude = address.getLongitude();
                            }
                        } catch (IOException e) {
                            // TODO: Event Location invalid alert.
                        }
                    }

                    Map<String, Object> eventLocationMap = new HashMap<String, Object>();
                    eventLocationMap.put("latitude",latitude);
                    eventLocationMap.put("longitude",longitude);

                    Map<String, Object> eventEntityMap = new HashMap<String, Object>();
                    eventEntityMap.put("eventName", eventNameText);
                    eventEntityMap.put("location", eventLocationMap);

                    ArrayList<Map<String, Object>> eventArray = new ArrayList<Map<String, Object>>();
                    eventArray.add(eventEntityMap);

                    final boolean isPublicEvent = publicSwitch.isChecked();
                    String eventType = (isPublicEvent)? "publicevents": "privateevents";

                    Client.sharedClient().dataClient().createEntitiesAsync(eventType, eventArray, new ApiResponseCallback() {
                        @Override
                        public void onResponse(ApiResponse response) {
                            if( response != null ) {
                                List entities = response.getEntities();
                                if( entities != null && entities.size() > 0 ) {
                                    Entity createdEventEntity = (Entity) entities.get(0);
                                    Log.d("Tag",createdEventEntity.getStringProperty("eventName"));
                                    if( !isPublicEvent ) {
                                        Client.sharedClient().dataClient().connectEntitiesAsync("users",Client.sharedClient().currentUser().getUuid().toString(),"private",createdEventEntity.getUuid().toString(), new ApiResponseCallback() {
                                            @Override
                                            public void onResponse(ApiResponse response) {
                                                if( response != null ) {
                                                    Log.d(TAG,response.toString());
                                                }
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                                Log.d(TAG,e.toString());
                                            }
                                        });
                                    }
                                    CreateEventActivity.this.finish();
                                }
                            } else {
                                Log.d(TAG,"Add Event Response is null!");
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            Log.d(TAG,e.toString());
                        }
                    });
                }
            }
        });
    }
}
