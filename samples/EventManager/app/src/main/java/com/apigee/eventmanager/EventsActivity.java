package com.apigee.eventmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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

    private EventListViewAdapter eventListViewAdapter = new EventListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        listView = (ListView) this.findViewById(R.id.listView);
        listView.setAdapter(eventListViewAdapter);

        logoutButton = (Button) this.findViewById(R.id.logoutButton);
        addEventButton = (Button) this.findViewById(R.id.addEventButton);
        searchEditText = (EditText) this.findViewById(R.id.searchEditText);
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
                Intent addEventsIntent = new Intent(EventsActivity.this,CreateEventActivity.class);
                EventsActivity.this.startActivity(addEventsIntent);
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEditText.clearFocus();
                    InputMethodManager in = (InputMethodManager)EventsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                    String queryString = null;
                    String searchText = searchEditText.getText().toString();
                    if( searchText != null && !searchText.isEmpty() ) {
                        if( locationBasedSearchSwitch.isChecked() ) {
                            if(Geocoder.isPresent()){
                                try {
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> addressList= geocoder.getFromLocationName(searchText, 1);
                                    if( addressList != null && addressList.size() > 0 ) {
                                        Address address = addressList.get(0);
                                        queryString = "location within 160000 of " + address.getLatitude() + "," + address.getLongitude();
                                    }
                                } catch (IOException e) {
                                }
                            }
                        } else {
                            queryString = "select * where eventName contains '" + searchText + "*'";
                        }
                    }

                    EventsActivity.this.getPublicEvents(queryString);
                    EventsActivity.this.getPrivateEvents();

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
                searchEditText.setText("");
                EventsActivity.this.getPublicEvents(null);
                EventsActivity.this.getPrivateEvents();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        InputMethodManager in = (InputMethodManager)EventsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        EventsActivity.this.getPublicEvents(null);
        EventsActivity.this.getPrivateEvents();
    }

    public void getPrivateEvents() {
        Client.sharedClient().getPrivateEvents("",new ClientEventCallback() {
            @Override
            public void onEventsGathered(List<Entity> events) {
                ArrayList<EventContainer> privateEventContainers = new ArrayList<EventContainer>();
                if( events != null ) {
                    for( Entity entity:events ) {
                        privateEventContainers.add(new EventContainer(entity));
                    }
                }
                eventListViewAdapter.privateEvents = privateEventContainers;
                eventListViewAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }

            @Override
            public void onFailed(String error) {
                if( error != null ) {
                    Log.d("EventsActivity","Failed getting private events.  Error: " + error);
                }
                eventListViewAdapter.privateEvents = null;
                eventListViewAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }
        });
    }

    public void getPublicEvents(String queryString) {
        HashMap<String,Object> query = new HashMap<String, Object>();
        if( queryString != null ) {
            query.put("ql",queryString);
        }

        Client.sharedClient().getPublicEvents(query, new ClientEventCallback() {
            @Override
            public void onEventsGathered(List<Entity> events) {
                ArrayList<EventContainer> publicEventContainers = new ArrayList<EventContainer>();
                if( events != null ) {
                    for( Entity entity:events ) {
                        publicEventContainers.add(new EventContainer(entity));
                    }
                }
                eventListViewAdapter.publicEvents = publicEventContainers;
                eventListViewAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }

            @Override
            public void onFailed(String error) {
                if( error != null ) {
                    Log.d("EventsActivity","Failed getting public events.  Error: " + error);
                }
                eventListViewAdapter.publicEvents = null;
                eventListViewAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }
        });
    }

    public class EventContainer {
        public String eventName = null;
        public String eventLocation = null;

        public EventContainer(Entity entity) {
            this.eventName = entity.getStringProperty("eventName");
            JsonNode locationObject= (JsonNode) entity.getProperties().get("location");
            if( locationObject != null && Geocoder.isPresent() ) {
                Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = myLocation.getFromLocation(locationObject.get("latitude").doubleValue(), locationObject.get("longitude").doubleValue(), 1);
                    if( addressList != null && addressList.size() > 0 ) {
                        Address locationAddress = addressList.get(0);
                        this.eventLocation = locationAddress.getLocality() + ", " + locationAddress.getAdminArea();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class EventListViewAdapter extends BaseAdapter {

        private ArrayList<EventContainer> publicEvents;
        private ArrayList<EventContainer> privateEvents;

        public void setPublicEvents(ArrayList<EventContainer> publicEvents) {
            this.publicEvents = publicEvents;
        }

        public void setPrivateEvents(ArrayList<EventContainer> privateEvents) {
            this.privateEvents = privateEvents;
        }

        public EventListViewAdapter() {

        }

        @Override
        public int getCount() {
            int count = 0;
            if( publicEvents != null ) {
                count += publicEvents.size();
            }
            if( privateEvents != null ) {
                count += privateEvents.size();
            }
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            EventContainer event = null;
            if( publicEvents.size() > arg0 ) {
                event = publicEvents.get(arg0);
            } else {
                event = privateEvents.get(arg0 - publicEvents.size());
            }
            return event;
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

            TextView eventNameTextView = (TextView)arg1.findViewById(R.id.textView1);
            TextView eventLocationTextView = (TextView)arg1.findViewById(R.id.textView2);

            EventContainer event = null;
            if( publicEvents.size() > arg0 ) {
                event = publicEvents.get(arg0);
            } else {
                event = privateEvents.get(arg0 - publicEvents.size());
            }

            eventNameTextView.setText(event.eventName);
            eventLocationTextView.setText(event.eventLocation);

            return arg1;
        }
    }
}