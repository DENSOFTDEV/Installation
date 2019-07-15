package com.densoftdevelopers.installation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.R.layout.simple_spinner_item;

public class SiteChooseActivity extends AppCompatActivity implements LocationListener {
    private SearchableSpinner select_site_name, select_site_id;
    private Button Valiadate_btn;


    private final int REQUEST_LOCATION = 1;
    private ArrayList<SiteNameModel> siteNameModels;
    private  ArrayList<String> SiteNames = new ArrayList<>();

    private ArrayList<SiteIdModel> siteIdModels;
    private  ArrayList<String> SiteId = new ArrayList<>();
    public static final String FETCHING_SITEID_URL = "http://178.128.114.85/app_files/installation/fetchsite_details_by_name.php";
    ProgressDialog loadingdialog;


   LocationManager locationManager;
   String user_latitude, user_longitude;
   TextView account;
   String name = Paper.book().read(UserDetails.Username);







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_choose);



        fetchSITENAMES();
        fetchSITEID();

        Paper.init(SiteChooseActivity.this);


        account = (TextView) findViewById(R.id.account_txt);

        account.setText("Welcome: "+name);
        select_site_name = (SearchableSpinner) findViewById(R.id.site_name);
        select_site_name.setTitle("select site name");

        select_site_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String sitename = select_site_name.getSelectedItem().toString();

               if (sitename.equals("select site name"))
               {
                   Toast.makeText(SiteChooseActivity.this, "please choose site name", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Toast.makeText(SiteChooseActivity.this, "item selected", Toast.LENGTH_SHORT).show();
                   getSiteID(sitename);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SiteChooseActivity.this, "please choose site name", Toast.LENGTH_SHORT).show();
            }
        });

        select_site_id = (SearchableSpinner) findViewById(R.id.site_id);
        select_site_id.setTitle("select site id");

        Valiadate_btn = (Button) findViewById(R.id.validate_location_btn);


        Valiadate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SiteChooseActivity.this, "validate clicked", Toast.LENGTH_SHORT).show();
            }
        });




    }




    private void getSiteID(String sitename)
    {


        final ProgressDialog waitingDialog = new ProgressDialog(SiteChooseActivity.this);

         final String site_name = sitename;

        class GetSiteId extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                waitingDialog.setTitle("Fetching Site Id");
                waitingDialog.setMessage("Please wait as we get site id for the selected site name");
                waitingDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating a request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating Requesting parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("site_name", site_name );


                //return the response

                return requestHandler.sendPostRequest(FETCHING_SITEID_URL,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                waitingDialog.dismiss();

                try
                {
                    //converting response to json
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(SiteChooseActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        String Site_id = obj.getString("site_id");
                        final String Latitude = obj.getString("latitude");
                        final String Longitude = obj.getString("longitude");
                        String SiteName = obj.getString("site_name");

                        Paper.book().write(SiteDetails.SiteName,SiteName);
                        Paper.book().write(SiteDetails.Site_ID,Site_id);


                        final AlertDialog.Builder SiteDetails = new AlertDialog.Builder(SiteChooseActivity.this);

                        LinearLayout layout = new LinearLayout(SiteChooseActivity.this);
                        TextView tvSiteName = new TextView(SiteChooseActivity.this);
                        TextView tvSiteID = new TextView(SiteChooseActivity.this);
                        final TextView tvSiteLong = new TextView(SiteChooseActivity.this);
                        final TextView tvSiteLat = new TextView(SiteChooseActivity.this);

                        tvSiteName.setText("SiteName: "+SiteName);
                        tvSiteName.setSingleLine(false);
                        //tvSiteName.setGravity(Gravity.CENTER);
                        tvSiteName.setPadding(5,10,5,10);
                        tvSiteName.setTextColor(Color.BLACK);
                        tvSiteID.setText("SiteID: "+Site_id);
                        tvSiteID.setPadding(5,10,5,10);
                        tvSiteID.setTextColor(Color.BLACK);
                        tvSiteLong.setText("Longitude: "+Longitude);
                        tvSiteLong.setPadding(5,10,5,10);
                        tvSiteLong.setTextColor(Color.BLACK);
                        tvSiteLat.setText("Latitude: "+Latitude);
                        tvSiteLat.setPadding(5,10,5,10);
                        tvSiteLat.setTextColor(Color.BLACK);

                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.addView(tvSiteName);
                        layout.addView(tvSiteID);
                        layout.addView(tvSiteLong);
                        layout.addView(tvSiteLat);
                        layout.setPadding(50,40,50,10);

                        SiteDetails.setView(layout);
                        SiteDetails.setTitle("SITE DETAILS FOR THE SELECTED SITE NAME");
                        SiteDetails.setMessage("Are you in the selected station?");
                        SiteDetails.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SiteChooseActivity.this, "confirmed", Toast.LENGTH_SHORT).show();

                                Double latitude  =  Double.parseDouble(Latitude);
                                Double longitude  =  Double.parseDouble(Longitude);

                                getUserLocation(latitude,longitude);

                            }
                        });
                        SiteDetails.setNegativeButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SiteChooseActivity.this, "try again", Toast.LENGTH_SHORT).show();
                            }
                        });

                        SiteDetails.create().show();


                    }
                    else
                    {
                        Toast.makeText(SiteChooseActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SiteChooseActivity.this, "could not connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        GetSiteId getSiteId = new GetSiteId();
        getSiteId.execute();
    }




    private void showProgressDialog() {

        loadingdialog = new ProgressDialog(SiteChooseActivity.this);
        loadingdialog.setTitle("validating Location");
        loadingdialog.setMessage("please wait as we confirm your location");
        loadingdialog.setCancelable(false);
        loadingdialog.show();
    }

    private void getUserLocation(Double latitude, Double longitude) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //write function to enable gps
            onGps();
        }
        else
        {
            //Gps is already on then
            getLocation(latitude,longitude);
        }
    }

    private void getLocation(Double latitude, Double longitude) {

        String site_lati = Double.toString(latitude);
        String site_longi = Double.toString(longitude);


        //check permissions again

        if (ActivityCompat.checkSelfPermission(SiteChooseActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(SiteChooseActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();

                user_latitude=String.valueOf(lat);
                user_longitude=String.valueOf(longi);

                userLocation(user_latitude,user_longitude,site_lati,site_longi);
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                user_latitude=String.valueOf(lat);
                user_longitude=String.valueOf(longi);

                userLocation(user_latitude,user_longitude,site_lati,site_longi);
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                user_latitude=String.valueOf(lat);
                user_longitude=String.valueOf(longi);

                userLocation(user_latitude,user_longitude,site_lati,site_longi);
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void userLocation(String user_latitude, String user_longitude,String site_latitude, String site_longitude) {

        String user_lat = user_latitude;
        String user_long = user_longitude;
        String site_lat = site_latitude;
        String site_long = site_longitude;

        ValidateLocation(user_lat,user_long,site_lat,site_long);
    }

    private void onGps() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),1);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public void ValidateLocation(String user_lat, String user_long, String site_lat, String site_long) {

       // Double site_lati = latitude;
       // Double site_longi = longitude;
       // String site_latitude = site

        //String user_lati = user_latitude;
        //String user_longi = user_longitude;

        Location userLocation = new Location("");
        userLocation.setLatitude(Double.parseDouble(user_lat));
        userLocation.setLongitude(Double.parseDouble(user_long));

        Location siteLocation = new Location("");
        siteLocation.setLatitude(Double.parseDouble(site_lat));
        siteLocation.setLongitude(Double.parseDouble(site_long));

        float distanceInMeters = userLocation.distanceTo(siteLocation);

        if (distanceInMeters < 50)
        {

            final AlertDialog.Builder builder = new AlertDialog.Builder(SiteChooseActivity.this);
            builder.setTitle("ERROR");
            builder.setMessage("you are not within the selected site location please select a new site");
            builder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SiteChooseActivity.this);
            builder.setTitle("SUCCESS");
            builder.setMessage(" site location confirmed!! you can now continue with the installation");
            builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(SiteChooseActivity.this, HomeActivity.class));
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Toast.makeText(this, "congrats location validation successful", Toast.LENGTH_SHORT).show();
        }




    }

    private void fetchSITEID() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Spinner_name_Interface.JSONURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Spinner_name_Interface api = retrofit.create(Spinner_name_Interface.class);

        Call<String> call = api.getSITEID();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("ResponseString",response.body().toString());

                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        Log.i("onSuccess",response.body().toString());

                        String jsonresponse = response.body().toString();
                        spinSiteIdJSON(jsonresponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void spinSiteIdJSON(String response) {

        try {
            JSONObject obj = new JSONObject(response);

            if (obj.optString("status").equals("true"))
            {
                siteIdModels = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");

                SiteId.add(0,"select site id");

                for (int i =0; i< dataArray.length();i++)
                {
                    SiteIdModel siteIdModel = new SiteIdModel();
                    JSONObject dataobj = dataArray.getJSONObject(i);

                    siteIdModel.setSiteID(dataobj.getString("SiteID"));
                    siteIdModels.add(siteIdModel);
                }

                for (int i = 0; i < siteIdModels.size();i++)
                {
                    SiteId.add(siteIdModels.get(i).getSiteID());
                }

                //siteIdModels.add(new SiteIdModel(0,"select one"));
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(SiteChooseActivity.this,simple_spinner_item,SiteId);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                select_site_id.setAdapter(spinnerArrayAdapter);
               // select_site_id.setPrompt("select site id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchSITENAMES() {

        final ProgressDialog dialog = new ProgressDialog(SiteChooseActivity.this);
        dialog.setTitle("Fetching Site Names");
        dialog.setMessage("Please: "+name+" wait as we fetch site names");
        dialog.setCancelable(false);
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Spinner_name_Interface.JSONURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Spinner_name_Interface api = retrofit.create(Spinner_name_Interface.class);

        Call<String> call = api.getSITENAMES();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dialog.dismiss();

                Log.i("ResponseString", response.body().toString());

                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        Log.i("onSuccess",response.body().toString());

                        String jsonresponse = response.body().toString();
                        spinSiteNameJSON(jsonresponse);
                    }
                    else
                    {
                        Log.i("onEmptyResponse","Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(SiteChooseActivity.this);
                builder.setTitle("Network Error!!");
                builder.setMessage("Sorry we could not get the site names, Please check your internet connection to continue.");
                builder.setCancelable(false);
                builder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fetchSITENAMES();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(SiteChooseActivity.this, "Closing Application", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                AlertDialog dialog =builder.create();
                dialog.show();

            }
        });

    }

    private void spinSiteNameJSON(String response) {
        try {
            JSONObject obj = new JSONObject(response);

            if (obj.optString("status").equals("true"))
            {
                siteNameModels = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");

                SiteNames.add(0,"select site name");

                for (int i =0; i< dataArray.length();i++)
                {
                    SiteNameModel siteNameModel = new SiteNameModel();
                    JSONObject dataobj = dataArray.getJSONObject(i);

                    siteNameModel.setSiteName(dataobj.getString("SiteName"));
                    siteNameModels.add(siteNameModel);
                }

                for (int i = 0; i < siteNameModels.size();i++)
                {
                    SiteNames.add(siteNameModels.get(i).getSiteName());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(SiteChooseActivity.this,simple_spinner_item,SiteNames);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                select_site_name.setAdapter(spinnerArrayAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION)
        {
            if (grantResults.length ==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
        }
        else
        {
            Toast.makeText(this, "Permission was not granted this app will not  work", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            Toast.makeText(this, "Select site name again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
