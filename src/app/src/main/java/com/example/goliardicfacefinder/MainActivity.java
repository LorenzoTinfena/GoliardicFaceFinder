package com.example.goliardicfacefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Region;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//cloudmersive client apis
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.*;
import com.cloudmersive.client.FaceApi;
import com.cloudmersive.client.model.AgeDetectionResult;
import com.cloudmersive.client.model.GenderDetectionResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ImageView imageView1;
    private static final int IMAGE_PICK_CODE = 1000;
    int codeTmp = 0;
    private void checkPermission(String[] PERMISSIONS){
        codeTmp = codeTmp + 1;
        for (int i = 0; i < PERMISSIONS.length; i++){/*
            if (ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "You already granted this permission", Toast.LENGTH_LONG).show();
            }
            else{*/
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[i])){
                    final int j = i;
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because of this and that")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[] {PERMISSIONS[j]}, codeTmp);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{PERMISSIONS[i]}, codeTmp);
                }
            //}
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == codeTmp)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //WE WE METTI QUA RPOBE DA FARE IL  PERMESSO RUNTIME, ALTRIMENTI CANCELLALO CON LE DIPENDENZE
        String[] PERMISSIONS = {};
        checkPermission(PERMISSIONS);

        setContentView(R.layout.activity_main);
        imageView1 = (ImageView)findViewById(R.id.imageView1);


        findViewById(R.id.browserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putString("cameraImageUri", imageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            imageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //Uri uri = data.getData();
            imageView1.setImageURI(imageUri);
            apiemela(imageUri);
        }
    }

    private void apiemela(Uri uriFile){
        //String dd = FilePath.getPath(getApplicationContext(), uriFile);
        Thread thread = new Thread(){
            public void run(){
                ApiClient defaultClient = Configuration.getDefaultApiClient();

                ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
                Apikey.setApiKey(ApiKey.ApiKey);

                FaceApi apiInstance = new FaceApi();
                File imageFile = new File(FilePath.getPath(getApplicationContext(), uriFile)); // File | Image file to perform the operation on.  Common file formats such as PNG, JPEG are supported.
                try {
                    //https://api.cloudmersive.com/swagger/index.html?urls.primaryName=Image%20Recognition%20and%20Processing%20API
                    //AgeDetectionResult result = apiInstance.faceDetectAge(imageFile);
                    GenderDetectionResult result = apiInstance.faceDetectGender(imageFile);
                    System.out.println(result);
                } catch (ApiException e) {
                    System.err.println("____________________________________");
                    System.err.println(e.toString());
                    System.err.println("Exception when calling FaceApi#faceDetectAge");
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}