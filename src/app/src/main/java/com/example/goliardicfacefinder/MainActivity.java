package com.example.goliardicfacefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//cloudmersive client apis
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.*;
import com.cloudmersive.client.FaceApi;
import com.cloudmersive.client.model.AgeDetectionResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.browserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri uri = data.getData();
            apiemela(uri.getPath());
            //((ImageView)findViewById(R.id.imageView1)).setImageURI(uri);
        }
    }

    private void apiemela(String path){

        Thread thread = new Thread(){
            public void run(){
                ApiClient defaultClient = Configuration.getDefaultApiClient();

                ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
                Apikey.setApiKey(ApiKey.ApiKey);

                FaceApi apiInstance = new FaceApi();
                File imageFile = new File(path); // File | Image file to perform the operation on.  Common file formats such as PNG, JPEG are supported.
                try {
                    boolean dd = imageFile.exists();
                    AgeDetectionResult result = apiInstance.faceDetectAge(imageFile);
                    System.out.println(result);
                } catch (ApiException e) {
                    System.err.println("Exception when calling FaceApi#faceDetectAge");
                    e.printStackTrace();
                }
            }
        };
        thread.start();


    }
}