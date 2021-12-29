package com.example.mobilerecipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    int CAMERA_REQUEST_CODE = 1;
    int CAMERA_PERMISSION_CODE = 100;

    Uri selectedImage;

    String[] topClassifications;

    private void connectToServer(){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        byte[] byteArray = null;

        try{
            InputStream inputStream = getContentResolver().openInputStream(selectedImage);

            System.out.println(selectedImage.getPath());

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while((len = inputStream.read(buffer)) != -1){
                System.out.println("Iterating");
                byteBuffer.write(buffer, 0, len);
            }
            System.out.println("To byte array");
            byteArray = byteBuffer.toByteArray();

        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Add to body");
        assert byteArray != null;
        multipartBodyBuilder.addFormDataPart("image0", "input_img.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));

        RequestBody postBodyImage = multipartBodyBuilder.build();

        postRequest(postBodyImage);
    }

    private void postRequest(RequestBody postBody){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Helper.UPLOAD_URL)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    try{
                        String respNew = response.body().string();
                        respNew = respNew.replaceAll("[^.,a-zA-Z0-9]"," ");
                        topClassifications = respNew.split(",");

                        Intent changeToVerificationScreen = new Intent(MainActivity.this, IngredientVerificationActivity.class);
                        changeToVerificationScreen.putExtra("ClassificationResults", topClassifications);
                        startActivity(changeToVerificationScreen);
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                });
            }
        });
    }

    private Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);

        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button capture = findViewById(R.id.capture);
        Button quit = findViewById(R.id.quit);

        capture.setOnClickListener(v -> {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else{
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        quit.setOnClickListener(v -> {
            MainActivity.this.finish();
            System.exit(0);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("EXTERNAL STORAGE GRANTED");
            } else {
                System.out.println("EXTERNAL STORAGE NOT GRANTED");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if(data.getExtras().get("data") != null){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImage = getImageUri(getApplicationContext(), photo);

                Log.d("ImageDetails", "URI: " + selectedImage);
                connectToServer();
            }
        }
    }
}