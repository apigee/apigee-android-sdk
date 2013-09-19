package com.apigee.sample.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.apigee.sample.books.R;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;


public class BooksListViewActivity extends Activity {

	private static final String ORGNAME = "<YOUR_ORG_NAME>"; // <-- Put your username here!!!
    private static final String APPNAME = "<YOUR_APP_NAME>";
	
    private BooksApplication bookApp;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_view);
        
    	bookApp = (BooksApplication) getApplication();
    	if( bookApp.getApigeeClient() == null ) {
            ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
    		bookApp.setApigeeClient(apigeeClient);
    	}

        getBooks();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	getBooks();
    }

	public void getBooks() {
		
		final ArrayList<String> titles = new ArrayList<String>();
    	final BooksArrayAdapter adapter = new BooksArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        adapter.setNotifyOnChange(true);
        final ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        
        DataClient client = bookApp.getDataClient();
        if (client != null) {
            client.getEntitiesAsync("books", "select *", new ApiResponseCallback(){
                	
            	@Override
                public void onException(Exception ex) {
                	Log.i("Error", ex.getMessage());
                }
                	
                @Override
                public void onResponse(ApiResponse response) {
                	if (response != null) {
                		List<Entity> books = response.getEntities();
                        
                		for (int j = 0; j < books.size(); j++) {
                			Entity book = books.get(j);
                			String bookTitle = book.getStringProperty("title");
                			adapter.add(bookTitle);
                		}
                		adapter.notifyDataSetChanged();
                	} else {
                    	adapter.add("Error: " + BooksApplication.apigeeNotInitializedLogError);
                    	adapter.notifyDataSetChanged();
                		apigeeInitializationError();
                	}
                }
            });
        } else {
        	adapter.add("Error: " + BooksApplication.apigeeNotInitializedLogError);
        	adapter.notifyDataSetChanged();
        	apigeeInitializationError();
        }
	}
	
	public void apigeeInitializationError() {
		Log.d("Books",BooksApplication.apigeeNotInitializedLogError);
		
		Context context = getApplicationContext();
		CharSequence text = "Apigee client is not initialized";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.action_add_book:
    		openBookForm();
    		return true;
    	default:
    		return false;
    	}
    }
    
    public void openBookForm(){
    	Intent intent = new Intent(this, NewBookActivity.class);
    	this.startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.books_list_view, menu);
        return true;
    }
    
    private class BooksArrayAdapter extends ArrayAdapter<String> {

        public BooksArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }
    }
}
