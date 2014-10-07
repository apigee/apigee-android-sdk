package com.apigee.assets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class AssetActivity extends Activity {
    private static final String APIGEE_ORG_NAME = "rwalsh"; // <-- Put your org name here!!!
    private static final String APIGEE_APP_NAME = "sandbox";
    private static final String ENTITY_TYPE = "pictures";
    private static final String ENTITY_NAME = "testAssetUpload";

    private static final String CONTENT_TYPE = "image/png";
    private static final String IMAGE_UPLOAD_NAME = "assets_example_image.png";

    private ApigeeClient apigeeClient = null;
    private ApigeeDataClient apigeeDataClient = null;
    private Entity pictureEntity = null;

    private static final int SELECT_PHOTO_REQUEST_CODE = 100;
    private Bitmap selectedBitMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_activity);

        this.apigeeClient = new ApigeeClient(APIGEE_ORG_NAME,APIGEE_APP_NAME,this);
        this.apigeeDataClient = this.apigeeClient.getDataClient();

        // First thing we want to do is grab the entity that we will be using to store the image data.
        this.apigeeDataClient.getEntitiesAsync(ENTITY_TYPE,"name='" + ENTITY_NAME + "'", new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {

                Entity pictureEntity = response.getFirstEntity();
                if( pictureEntity == null ) {

                    // If we didn't find the picture entity lets go ahead and attempt to create it.
                    Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put("type", ENTITY_TYPE);
                    properties.put("name", ENTITY_NAME);

                    apigeeDataClient.createEntityAsync(properties, new ApiResponseCallback() {
                        @Override
                        public void onException(Exception e) {
                        }
                        @Override
                        public void onResponse(ApiResponse response) {
                            AssetActivity.this.pictureEntity = response.getFirstEntity();
                        }
                    });

                } else {
                    AssetActivity.this.pictureEntity = pictureEntity;

                    // Once we have the picture entity we might as try and grab the asset data if it exists already;
                    AssetActivity.this.getUploadedImage(null);
                }
            }
            @Override
            public void onException(Exception e) {
            }
        });
    }

    public void pickImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        selectedBitMap = BitmapFactory.decodeStream(imageStream);
                        ((ImageView)findViewById(R.id.pickedImageView)).setImageBitmap(selectedBitMap);
                    } catch (Exception exception) {
                    }
                }
        }
    }

    public void uploadImage(View view) {
        if( this.selectedBitMap != null && this.apigeeDataClient != null && this.pictureEntity != null ) {
            try {
                // First turn the selectedBitMap into an InputStream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedBitMap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitMapData = bos.toByteArray();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bitMapData);

                // Now we call the apigeeDataClient to upload the bitmaps data.
                this.apigeeDataClient.attachAssetToEntityAsync(this.pictureEntity,byteArrayInputStream,IMAGE_UPLOAD_NAME,CONTENT_TYPE,new ApiResponseCallback() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if( response.completedSuccessfully() ) {
                            AssetActivity.this.getUploadedImage(null);
                        }
                    }
                    @Override
                    public void onException(Exception e) {
                    }
                });
            }  catch (Exception e) {
            }
        }
    }

    public void getUploadedImage(View view) {
        if( this.apigeeDataClient != null && this.pictureEntity != null ) {
            // Using the pictureEntity we grab the assets data that it is storing.
            this.apigeeDataClient.getAssetDataForEntityAsync(this.pictureEntity, CONTENT_TYPE, new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse response) {
                    byte[] entityAssetData = response.getEntityAssetData();
                    if( entityAssetData != null ) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(entityAssetData, 0, entityAssetData.length);
                        ((ImageView) findViewById(R.id.uploadedImageView)).setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onException(Exception e) {

                }
            });
        }
    }
}
