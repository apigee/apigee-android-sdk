package com.apigee.eventmanager;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.apigee.sdk.data.client.entities.Entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateEventActivity extends Activity {

    private static String TAG = "CreateEventActivity";

    private Button cancelButton;
    private EditText eventNameEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private Switch publicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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

        ((Button) this.findViewById(R.id.addEventButton)).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameText = eventNameEditText.getText().toString();
                String cityText = cityEditText.getText().toString();
                String stateText = stateEditText.getText().toString();
                if( eventNameText.isEmpty() || cityText.isEmpty() || stateText.isEmpty() ) {
                    Client.showAlert(CreateEventActivity.this,"Error","All fields must be filled");
                } else {
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
                        }
                    }

                    Map<String, Object> eventLocationMap = new HashMap<String, Object>();
                    eventLocationMap.put("latitude",latitude);
                    eventLocationMap.put("longitude",longitude);

                    Map<String, Object> eventEntityMap = new HashMap<String, Object>();
                    eventEntityMap.put("eventName", eventNameText);
                    eventEntityMap.put("location", eventLocationMap);

                    Client.sharedClient().createEvent(publicSwitch.isChecked(),eventEntityMap, new ClientCreateEventCallback() {
                        @Override
                        public void onSuccess(Entity createdEntity) {
                            CreateEventActivity.this.finish();
                        }
                        @Override
                        public void onFailed(String error) {
                            Client.showAlert(CreateEventActivity.this,"Error Creating Event",error);
                        }
                    });
                }
            }
        });
    }
}
