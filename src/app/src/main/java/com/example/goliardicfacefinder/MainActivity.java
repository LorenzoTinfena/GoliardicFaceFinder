package com.example.goliardicfacefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//cloudmersive client apis
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.*;
import com.cloudmersive.client.FaceApi;
import com.cloudmersive.client.model.AgeDetectionResult;
import com.cloudmersive.client.model.GenderDetectionResult;
import com.cloudmersive.client.model.PersonWithAge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;

public class MainActivity extends AppCompatActivity {

    ImageView imageView1;
    private static final int IMAGE_PICK_CODE = 1000;
    int codeTmp = 0;
    private void checkPermission(String[] PERMISSIONS){
        codeTmp = codeTmp + 1;
        for (int i = 0; i < PERMISSIONS.length; i++){
            if (ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[i]) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{PERMISSIONS[i]}, codeTmp);
            }
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

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //WE WE METTI QUA RPOBE DA FARE IL  PERMESSO RUNTIME, ALTRIMENTI CANCELLALO CON LE DIPENDENZE
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
                ((TextView)(findViewById(R.id.errorTxtView))).setText("");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
        findViewById(R.id.browserBtnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null){/*
                    Drawable drawable = ((ImageView)findViewById(R.id.imageView1)).getDrawable();
                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();*/
                    try {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "MyPictureEdited");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        FileOutputStream str = new FileOutputStream(new File(FilePath.getPath(MainActivity.this, uri)));
                        tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, str);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(intent, "Share"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //Uri uri = data.getData();


            File imageFile = new File(FilePath.getPath(getApplicationContext(), imageUri));
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            double m = (double)bitmap.getHeight() / bitmap.getWidth();
            Bitmap bitmapRes = Bitmap.createScaledBitmap(bitmap, (int)Math.sqrt(787000 / m), (int)Math.sqrt(787000 * m), true);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmapRes.compress(Bitmap.CompressFormat.JPEG,100, outputStream); // this line will reduce the size , try changing the second argument to adjust to correct size , it ranges 0-100
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView1.setImageURI(imageUri);
            System.out.println(imageFile.length());
            apiemela(imageFile);
            loadingDialog.startLoadingDialog();
        }
    }
    Bitmap tempBitmap;
    protected void ApiResposeCallback(LinkedList<Face> res){
        loadingDialog.dismissDialog();
        if (res != null){

            Paint pai = new Paint();
            pai.setColor(Color.argb(255, 60, 105, 240));
            pai.setTextSize(40);

            BitmapDrawable drawable = (BitmapDrawable) imageView1.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(bitmap, 0, 0, null);
            for (Face f : res){
                tempCanvas.drawText("Age: " + Integer.toString((int)f.Age), (float) f.leftX + 10, (float)(f.topY + 50), pai);
                for (Rect re : GetBounds(f)){
                    tempCanvas.drawRect(re, pai);
                }
            }
            if (res.isEmpty()){
                tempBitmap = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)(findViewById(R.id.errorTxtView))).setText("no faces found");
                    }
                });
            }
            imageView1.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }
        else{
            tempBitmap = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView)(findViewById(R.id.errorTxtView))).setText("Error");
                }
            });
        }
    }

    protected List<Rect> GetBounds(Face res){
        Rect top = new Rect(), left = new Rect(), right = new Rect(), bottom = new Rect();

        top.left = res.leftX + 4;
        top.top = res.topY - 4;
        top.right = res.rightX - 4;
        top.bottom = res.topY + 4;

        bottom.left = res.leftX + 4;
        bottom.top = res.bottomY - 4;
        bottom.right = res.rightX - 4;
        bottom.bottom = res.bottomY + 4;

        left.left = res.leftX - 4;
        left.top = res.topY - 4;
        left.right = res.leftX + 4;
        left.bottom = res.bottomY + 4;

        right.left = res.rightX - 4;
        right.top = res.topY - 4;
        right.right = res.rightX + 4;
        right.bottom = res.bottomY + 4;

        LinkedList<Rect> li = new LinkedList<Rect>();
        li.add(left);
        li.add(top);
        li.add(right);
        li.add(bottom);
        return li;
    }

    private void apiemela(File imageFile){
        Thread thread = new Thread(){
            public void run(){
                ApiClient defaultClient = Configuration.getDefaultApiClient();

                ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
                Apikey.setApiKey(ApiKey.ApiKey);

                FaceApi apiInstance = new FaceApi();
                try {
                    //https://api.cloudmersive.com/swagger/index.html?urls.primaryName=Image%20Recognition%20and%20Processing%20API
                    AgeDetectionResult resultAge = apiInstance.faceDetectAge(imageFile);
                    if (resultAge.isSuccessful()){
                        LinkedList<Face> res = new LinkedList<>();
                        for (int i = 0; i < resultAge.getPeopleIdentified(); i++){
                            PersonWithAge p = resultAge.getPeopleWithAge().get(i);
                            com.cloudmersive.client.model.Face face = p.getFaceLocation();
                            res.add(new Face(face.getLeftX(), face.getTopY(), face.getRightX(), face.getBottomY(), p.getAgeClass(), p.getAge(), p.getAgeClassificationConfidence()));
                        }
                        ApiResposeCallback(res);
                    }
                    else{
                        ApiResposeCallback(null);
                    }
                    System.out.println("____________________________________");
                    System.out.println(resultAge);
                } catch (ApiException e) {
                    ApiResposeCallback(null);
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