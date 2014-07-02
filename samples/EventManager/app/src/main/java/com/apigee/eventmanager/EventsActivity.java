package com.apigee.eventmanager;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.apigee.fasterxml.jackson.databind.JsonNode;
import com.apigee.sdk.data.client.entities.Collection;
import com.apigee.sdk.data.client.entities.Entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventsActivity extends Activity {

    private ListView listView;
    private Button logoutButton;
    private Button addEventButton;
    private EditText searchEditText;
    private Button cancelButton;
    private Switch locationBasedSearchSwitch;

    private Collection publicEventsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        listView = (ListView) this.findViewById(R.id.listView);
        logoutButton = (Button) this.findViewById(R.id.logoutButton);
        addEventButton = (Button) this.findViewById(R.id.addEventButton);
        searchEditText = (EditText) this.findViewById(R.id.searchEditText);
        searchEditText.clearFocus();

        cancelButton = (Button) this.findViewById(R.id.cancelButton);
        locationBasedSearchSwitch = (Switch) this.findViewById(R.id.locationBasedSearchSwitch);

        logoutButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.sharedClient().logoutUser();
                EventsActivity.this.finish();
            }
        });

        addEventButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to the addEvent screen.
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEditText.clearFocus();
                    InputMethodManager in = (InputMethodManager)EventsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    // TODO: Perform search.
//                    performSearch();
                    return true;
                }

                return false;
            }
        });

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.clearFocus();
                InputMethodManager in = (InputMethodManager)EventsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                // TODO: Clear searchEditText, close the keyboard, reload the listview with data with no query.
            }
        });


        Client.sharedClient().getPublicEvents(new HashMap<String, Object>(), new ClientEventCallback() {
            @Override
            public void onEventsGathered(List<Entity> events) {
                ArrayList<EventContainer> eventContainers = new ArrayList<EventContainer>();
                if( events != null ) {
                    for( Entity entity:events ) {
                        String eventName = entity.getStringProperty("eventName");
                        String eventLocation = null;

                        JsonNode locationObject= (JsonNode) entity.getProperties().get("location");
                        if( locationObject != null ) {
                            double latitude = locationObject.get("latitude").doubleValue();
                            double longitude = locationObject.get("longitude").doubleValue();
                            Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List<Address> addressList = myLocation.getFromLocation(latitude, longitude, 1);
                                Address locationAddress = addressList.get(0);
                                eventLocation = locationAddress.getLocality() + ", " + locationAddress.getAdminArea();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        eventContainers.add(new EventContainer(eventName,eventLocation));
                    }
                }
                EventListViewAdapter eventListViewAdapter = new EventListViewAdapter(eventContainers);
                listView.setAdapter(eventListViewAdapter);
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }

    public class EventContainer {
        public String eventName;
        public String eventLocation;

        public EventContainer(String eventName, String eventLocation) {
            this.eventName = eventName;
            this.eventLocation = eventLocation;
        }
    }

    public class EventListViewAdapter extends BaseAdapter {

        private ArrayList<EventContainer> eventContainers;

        public EventListViewAdapter(ArrayList<EventContainer> eventContainers) {
            this.eventContainers = eventContainers;
        }

        @Override
        public int getCount() {
            return eventContainers.size();
        }

        @Override
        public Object getItem(int arg0) {
            return eventContainers.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            if(arg1==null)
            {
                LayoutInflater inflater = (LayoutInflater) EventsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.eventlistitem, arg2,false);
            }

            TextView chapterName = (TextView)arg1.findViewById(R.id.textView1);
            TextView chapterDesc = (TextView)arg1.findViewById(R.id.textView2);

            EventContainer event = eventContainers.get(arg0);

            chapterName.setText(event.eventName);
            chapterDesc.setText(event.eventLocation);

            return arg1;
        }
    }

}
