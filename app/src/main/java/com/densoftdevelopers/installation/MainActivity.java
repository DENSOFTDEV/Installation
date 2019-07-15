package com.densoftdevelopers.installation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.RequiresApi;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
     TextView edtEmail, edtPassword;
    private Button signIn;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final String LOGIN_URL = "http://178.128.114.85/app_files/Backend/login.php";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(MainActivity.this);
        UserDetails userDetails = new UserDetails();


       /* if (Paper.book().read(UserDetails.Username).equals("username") && Paper.book().read(UserDetails.UserEmail).equals("useremail"))
        {
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
        }
        else
        {
            startActivity(new Intent(this, SiteChooseActivity.class));
        }*/


        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }

        edtEmail = (TextView) findViewById(R.id.sign_in_edtemail);
        edtPassword = (TextView) findViewById(R.id.sign_in_edtPassword);

        signIn = (Button) findViewById(R.id.sign_in_btn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

               /* if (Paper.book().read(UserDetails.Username).equals("username") && Paper.book().read(UserDetails.UserEmail).equals("useremail"))
                {

                }
                else
                {
                    opennext();
                }
*/

                if (TextUtils.isEmpty(edtEmail.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please input your login email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please input your password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    login();
                }

            }
        });
    }

    private void opennext() {

        startActivity(new Intent(this, SiteChooseActivity.class));
    }

    private void login() {


        class UserLogin extends AsyncTask<Void , Void, String>
        {
            ProgressDialog loadingBar = new ProgressDialog(MainActivity.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingBar.setTitle("Account verification");
                loadingBar.setMessage("please wait as we verify your account");
                loadingBar.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                //Creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("email",edtEmail.getText().toString());
                params.put("password",edtPassword.getText().toString());

                //returning the response
                return requestHandler.sendPostRequest(LOGIN_URL,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loadingBar.dismiss();

                try {
                    //converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the new user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating the new user object
                        String user_name = userJson.getString("name");
                        String user_email = userJson.getString("email");


                        //storing the user in shared preferences
                        Paper.book().write(UserDetails.Username,user_name);
                        Paper.book().write(UserDetails.UserEmail,user_email);


                        //Start then Home Activity
                        startActivity(new Intent(getApplicationContext(),SiteChooseActivity.class));

                    }
                    else if (obj.getBoolean("error"))
                    {
                        Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "failed could not connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        UserLogin userLogin = new UserLogin();
        userLogin.execute();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Camera permission denied, Some functions of this app may not work correctly!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
