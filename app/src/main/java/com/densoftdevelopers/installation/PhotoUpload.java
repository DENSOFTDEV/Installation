package com.densoftdevelopers.installation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

public class PhotoUpload extends AppCompatActivity {

    Button captureImage, UploadImage;
    ImageView imageViewHolder;
    EditText imageName;
    ProgressDialog progressDialog;
    Intent intent;
    public static final int RequestPermissionCode = 1;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap bitmap;
    boolean check = true;
    String GetImageNameFromEditText;
    String IMAGE_UPLOAD_URL = "http://178.128.114.85/app_files/installation/image_upload.php";
    String sitename;
    String sector_name;
    String installation_tech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        captureImage = (Button) findViewById(R.id.capture_btn);
        imageViewHolder = (ImageView) findViewById(R.id.imageView1);
        UploadImage = (Button) findViewById(R.id.upload_btn);
        imageName = (EditText) findViewById(R.id.edit_text);

        EnableRuntimePermissionToAccessCamera();

        Paper.init(PhotoUpload.this);

        installation_tech = Paper.book().read(SiteDetails.Site_INSTALLATION_TECH);
        sector_name = Paper.book().read(SiteDetails.SECTOR);
        sitename = Paper.book().read(SiteDetails.SiteName);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImageNameFromEditText = imageName.getText().toString();
                ImageUploadToServerFunction();
            }
        });
    }

    // Star activity for result method to Set captured image on image view after click.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageViewHolder.setImageBitmap(bitmap);
        }
    }

    private void ImageUploadToServerFunction() {


        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);


        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showing progress dialog at image upload time
                progressDialog = ProgressDialog.show(PhotoUpload.this, "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                 String image_name = GetImageNameFromEditText+"_SiteName: "+sitename+"_sector: _"+sector_name;
                 String server_url = "http://178.128.114.85/app_files/installation/siteImages/"+GetImageNameFromEditText+"_SiteName: "+sitename+"_sector: _"+sector_name;
                 String image_path = "siteImages/"+image_name;
                HashMapParams.put("image_name", image_name);
                HashMapParams.put("image_path", image_path);
                HashMapParams.put("server_url", server_url);
                HashMapParams.put("image_data", ConvertImage);

                String FinalData = requestHandler.sendPostRequest(IMAGE_UPLOAD_URL, HashMapParams);

                return FinalData;
            }


            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(PhotoUpload.this, string1, Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                imageViewHolder.setImageResource(android.R.color.transparent);
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    private void EnableRuntimePermissionToAccessCamera() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(PhotoUpload.this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(PhotoUpload.this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }

    }




    @Override
    public void onRequestPermissionsResult(int RC, @NonNull String[] per, @NonNull int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(PhotoUpload.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(PhotoUpload.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}

