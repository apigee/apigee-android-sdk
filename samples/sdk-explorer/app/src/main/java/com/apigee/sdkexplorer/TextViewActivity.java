package com.apigee.sdkexplorer;

import com.apigee.sdkexplorer.R;

import android.app.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.widget.*;


public class TextViewActivity extends Activity {
    
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textview_layout);
        
        final TextView textView = (TextView) findViewById(R.id.textView);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String textToDisplay = extras.getString("textToDisplay");
            String title = extras.getString("title");
            if( (textToDisplay != null) && (textView != null) ) {
            	textView.setMovementMethod(new ScrollingMovementMethod());
            	textView.setText(textToDisplay);
            }
            
            if( title != null ) {
            	setTitle(title);
            }
        }
    }
    
}
