package com.apigee.sample.books;

import java.util.HashMap;
import java.util.Map;

import com.apigee.sample.books.R;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.CounterIncrement;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.response.ApiResponse;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewBookActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_book, menu);
		return true;
	}

	public void createBook(View view){
		BooksApplication bookApp = (BooksApplication) getApplication();
		final DataClient client = bookApp.getDataClient();
		
		if (client != null) {
			EditText title = (EditText)findViewById(R.id.title);
			String bookTitle = title.getText().toString();
		
			EditText author = (EditText)findViewById(R.id.author);
			String bookAuthor = author.getText().toString();
		
			Map<String, Object> entity = new HashMap<String,Object>();
			entity.put("type", "books");
			entity.put("author", bookAuthor);
			entity.put("title", bookTitle);
		
			client.createEntityAsync(entity, new ApiResponseCallback(){
				@Override
				public void onException(Exception ex) {
					Log.i("NewBook", ex.getMessage());
				}
			
				@Override
				public void onResponse(ApiResponse response) {
					CounterIncrement counterIncrement = new CounterIncrement();
					counterIncrement.setCounterName("book_add");
					client.createEventAsync(null, counterIncrement, new ApiResponseCallback(){
						@Override
						public void onException(Exception ex) {
							Log.i("book_add", ex.getMessage());
						}
						
						@Override
						public void onResponse(ApiResponse counterResponse) {
							Log.i("book_add", "counter incremented");
						}
					});
					finish();		
				}
			});
		
		} else {
			Log.d("Books",BooksApplication.apigeeNotInitializedLogError);
			
			Context context = getApplicationContext();
			CharSequence text = "Apigee client is not initialized";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
	}
}
