package com.densoftdevelopers.installation.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.densoftdevelopers.installation.DatabaseAccess;
import com.densoftdevelopers.installation.PhotoUpload;
import com.densoftdevelopers.installation.R;
import com.densoftdevelopers.installation.RequestHandler;
import com.densoftdevelopers.installation.ScanDevice_id;
import com.densoftdevelopers.installation.Sim_serial;
import com.densoftdevelopers.installation.SiteDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import static android.R.layout.simple_spinner_item;


public class SectorChoose extends Fragment {

    public static TextView site_name,site_id, sensor_scan, scan_sim_serial,account;
    private Spinner sector_choose;
    private Button  getset_values_btn,validate_values_btn, take_photos_btn;
    public static final String SENSOR_PARAMETERS_FETCH = "http://178.128.114.85/app_files/installation/configValues.php";
    public static final String ENGINEER_SET_PARAMS = "http://178.128.114.85/app_files/installation/fetchsetvalues.php";
    String overall_thresh_msg = null;
    String overall_setvalues_msg = null;
    String techtype;
    String sector_number;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sector_choose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Paper.init(getActivity());



        site_name = (TextView) getActivity().findViewById(R.id.choose_sector_site_name);
        site_id = (TextView) getActivity().findViewById(R.id.choose_sector_site_id);
        sensor_scan = (TextView) getActivity().findViewById(R.id.scan_sensor_qr);
        scan_sim_serial = (TextView) getActivity().findViewById(R.id.scan_sim_serial);
        sector_choose = (Spinner) getActivity().findViewById(R.id.choose_sector_spinner);
        getset_values_btn = (Button) getActivity().findViewById(R.id.get_set_values);
        validate_values_btn = (Button) getActivity().findViewById(R.id.validate_btn);
        take_photos_btn = (Button) getActivity().findViewById(R.id.take_photos_btn);




        take_photos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PhotoUpload.class));
            }
        });

        sensor_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanDevice_id.class));
            }
        });

        scan_sim_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Sim_serial.class));
            }
        });

        getset_values_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEnginnerValues();
            }
        });

         String nameofsite = Paper.book().read(SiteDetails.SiteName.toString());
         site_name.setText(nameofsite);
         String Id = Paper.book().read(SiteDetails.Site_ID.toString());
         site_id.setText(Id);


         validate_values_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 String sensor_value = sensor_scan.getText().toString();
                 String sim_serial = scan_sim_serial.getText().toString();

                 if (sensor_value.equals("Scan Sensor Qr"))
                 {
                     Toast.makeText(getActivity(), "Please scan sensor Qr to continue", Toast.LENGTH_SHORT).show();
                 }
                 else if (sim_serial.equals("Scan Sim Serial"))
                 {
                     Toast.makeText(getActivity(), "Please sim id to continue", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     showAlertDialog();
                 }
             }
         });

         sector_number = Paper.book().read(SiteDetails.SECTOR);

         techtype = Paper.book().read(SiteDetails.Site_INSTALLATION_TECH);

         if (techtype.equals("GSM 900"))
         {
             String table_name = "GSM900";
             populateSpinner(table_name);
    }
         else if(techtype.equals("UMTS 900"))
         {
             String table_name = "UMTS900";
             populateSpinner(table_name);
         }
         else if(techtype.equals("GSM 1800"))
         {
             String table_name = "GSM1800";
             populateSpinner(table_name);
         }
         else if(techtype.equals("UMTS 2100"))
         {
             String table_name = "UMTS2100";
             populateSpinner(table_name);
         }
         else if (techtype.equals("LTE 800"))
         {
             String table_name = "LTE800";
             populateSpinner(table_name);
         }
         else if (techtype.equals("LTE 1800"))
         {
             String table_name = "LTE1800";
             populateSpinner(table_name);
         }
         else  if (techtype.equals("NBIOT 800"))
         {
             String table_name = "NBIOT800";
             populateSpinner(table_name);
         }
         else
         {
             Toast.makeText(getActivity(), "technology not found", Toast.LENGTH_SHORT).show();
         }



    }

    private void getEnginnerValues() {

        class getEnginnersValues extends AsyncTask<Void,Void,String>
        {
            ProgressDialog waitingdalog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                waitingdalog.setTitle("Validating Parameters");
                waitingdalog.setMessage("Please wait as we validate the device configuration parameters");
                waitingdalog.setCancelable(false);
                waitingdalog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating a request handler
                RequestHandler requestHandler = new RequestHandler();

                //creating requesting parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("site_name",site_name.getText().toString());
                params.put("site_id",site_id.getText().toString());

                //return response
                return requestHandler.sendPostRequest(ENGINEER_SET_PARAMS,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                waitingdalog.dismiss();

                try
                {
                    //converting response to json
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        String sector = Paper.book().read(SiteDetails.SECTOR);

                        String Enginner_heading = obj.getString("heading");
                        String Enginner_roll  = obj.getString("roll");
                        String Enginner_pitch = obj.getString("pitch");


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle("INSTALLATION SET VALUES")
                                .setCancelable(false)
                                .setMessage("The installation set values per the site selected");
                        View result_layout = getLayoutInflater().inflate(R.layout.set_installation_values,null);
                        builder.setView(result_layout);

                        TextView headingvalue = (TextView) result_layout.findViewById(R.id.set_heading_value);
                        headingvalue.setText(Enginner_heading);
                        TextView pitchvalue = (TextView) result_layout.findViewById(R.id.set_pitch_value);
                        pitchvalue.setText(Enginner_roll);
                        TextView rollvalue = (TextView) result_layout.findViewById(R.id.set_roll_value);
                        rollvalue.setText(Enginner_pitch);



                        builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Configure the Sector as per the set values and configure it to communicate with the serve then click on validate to validate your installation", Toast.LENGTH_LONG).show();
                            }
                        });

                        builder.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        getEnginnersValues enginnersValues = new getEnginnersValues();
        enginnersValues.execute();

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Attention! Have you configured the sensor?")
                .setMessage("By pressing the continue button we assume you have already configured the sensor to send data to the server")
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if (TextUtils.isEmpty(sensor_scan.getText().toString()))
                       {
                           Toast.makeText(getActivity(), "Please scan device id!!", Toast.LENGTH_SHORT).show();
                       }
                       else if (TextUtils.isEmpty(scan_sim_serial.getText().toString()))
                       {
                           Toast.makeText(getActivity(), "Please scan sim serial!!", Toast.LENGTH_SHORT).show();
                       }
                       else
                       {
                           getConfiguredSensorValues();
                       }
                    }
                }).setNegativeButton("CONFIGURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Please make sure the sensor is configured to send data to the server!!", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void getConfiguredSensorValues() {

        class GetSensorvalues extends AsyncTask<Void, Void,String>
        {

            ProgressDialog waitingDialog = new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                waitingDialog.setTitle("Checking sensor configurations on server");
                waitingDialog.setMessage("please wait as we check sensor parameters on server");
                waitingDialog.setCancelable(false);
                waitingDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating requesting parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("qr_number",sensor_scan.getText().toString());
                params.put("serial_num",scan_sim_serial.getText().toString());

                //return response
                return requestHandler.sendPostRequest(SENSOR_PARAMETERS_FETCH,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                waitingDialog.dismiss();
                try {
                    //converting response to json
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        final String device_id = obj.getString("qr_number");
                        final String sim_serial = obj.getString("imsi");
                        final String heading = obj.getString("heading");
                        final String pitch = obj.getString("pitch");
                        final String roll = obj.getString("roll");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Device configurations");

                        final View layout = getLayoutInflater().inflate(R.layout.sensor_result_get_success,null);
                        builder.setView(layout);
                        builder.setMessage("Click validate to continue with validation");

                        TextView device_id_tv = layout.findViewById(R.id.device_id);
                        device_id_tv.setText(device_id);
                        TextView sim_serial_tv = layout.findViewById(R.id.sim_serial);
                        sim_serial_tv.setText(sim_serial);
                        TextView heading_tv = layout.findViewById(R.id.heading);
                        heading_tv.setText(heading);
                        TextView pitch_tv = layout.findViewById(R.id.pitch);
                        pitch_tv.setText(pitch);
                        TextView roll_tv = layout.findViewById(R.id.roll);
                        roll_tv.setText(roll);

                        builder.setPositiveButton("VALIDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                validateParameters(device_id,sim_serial,heading,pitch,roll);
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();

                    }
                    else
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Error!!");
                        builder.setMessage("no parameters for the scanned device found on server\nPlease ensure the device is configured correctly to send data to the server!!");
                        builder.setPositiveButton("RESCAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Could not connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        GetSensorvalues sensorvalues = new GetSensorvalues();
        sensorvalues.execute();


    }

    private void validateParameters(final String device_id, String sim_serial, final String heading, final String pitch, final String roll) {

        class getEnginnersValues extends AsyncTask<Void,Void,String>
        {
            ProgressDialog waitingdalog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                waitingdalog.setTitle("Getting  Parameters");
                waitingdalog.setMessage("Please wait as we get the Device configuration set details");
                waitingdalog.setCancelable(false);
                waitingdalog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

               //creating a request handler
               RequestHandler requestHandler = new RequestHandler();

               //creating requesting parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("site_name",site_name.getText().toString());
                params.put("site_id",site_id.getText().toString());

                //return response
                return requestHandler.sendPostRequest(ENGINEER_SET_PARAMS,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                waitingdalog.dismiss();

                try
                {
                    //converting response to json
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        String sector = Paper.book().read(SiteDetails.SECTOR);

                        String Enginner_heading = obj.getString("heading");
                        String Enginner_roll  = obj.getString("roll");
                        String Enginner_pitch = obj.getString("pitch");

                        int sector_value = Integer.parseInt(sector);
                        double heading_value = Double.parseDouble(heading);
                        double pitch_value = Double.parseDouble(pitch);
                        double roll_value = Double.parseDouble(roll);



                        int Enginner_heading_value = Integer.parseInt(Enginner_heading);
                        int Enginner_roll_value = Integer.parseInt(Enginner_roll);
                        int Enginner_pitch_value = Integer.parseInt(Enginner_pitch);

                        String heading_threashhold_status = null;
                        String heading_engineer_status;
                        String pitch_threshhold_status = null;
                        String pitch_enginner_status;
                        String roll_threshhold_status = null;
                        String roll_engineer_status;




                        if (sector_value == 0 || sector_value == 4 || sector_value == 7)
                        {

                            if (0<heading_value && heading_value>120)
                            {
                                heading_threashhold_status = "pass";

                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                            else
                            {
                                heading_threashhold_status = "fail";

                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                        }
                        else if (sector_value == 1 || sector_value == 5 || sector_value == 8)
                        {
                            if (120<heading_value && heading_value>240)
                            {
                                heading_threashhold_status = "pass";

                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                            else
                            {
                                heading_threashhold_status = "fail";

                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                        }
                        else if (sector_value == 2 || sector_value == 6 || sector_value == 9)
                        {
                            if (240<heading_value && heading_value>360)
                            {
                                heading_threashhold_status = "pass";
                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                            else
                            {
                                heading_threashhold_status = "fail";
                                if (-90<roll_value && roll_value<90)
                                {
                                    roll_threshhold_status = "pass";

                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }

                                }
                                else
                                {
                                    roll_threshhold_status = "fail";
                                    if (-90<pitch_value && pitch_value<90 )
                                    {
                                        pitch_threshhold_status = "pass";
                                    }
                                    else
                                    {
                                        pitch_threshhold_status = "fail";
                                    }
                                }
                            }
                        }


                        if (Enginner_heading_value == heading_value)
                        {
                            heading_engineer_status = "pass";

                            if (Enginner_pitch_value == pitch_value)
                            {
                                pitch_enginner_status = "pass";

                                if (Enginner_roll_value == roll_value)
                                {
                                    roll_engineer_status = "pass";
                                }
                                else
                                {
                                    roll_engineer_status = "fail";
                                }
                            }
                            else
                            {
                                pitch_enginner_status = "fail";

                                if (Enginner_roll_value == roll_value)
                                {
                                    roll_engineer_status = "pass";
                                }
                                else
                                {
                                    roll_engineer_status = "pass";
                                }
                            }

                        }
                        else
                        {
                            heading_engineer_status = "fail";

                            if (Enginner_pitch_value == pitch_value)
                            {
                                pitch_enginner_status = "pass";

                                if (Enginner_roll_value == roll_value)
                                {
                                    roll_engineer_status = "pass";
                                }
                                else
                                {
                                    roll_engineer_status = "fail";
                                }
                            }
                            else
                            {
                                pitch_enginner_status = "fail";

                                if (Enginner_roll_value == roll_value)
                                {
                                    roll_engineer_status = "pass";
                                }
                                else
                                {
                                    roll_engineer_status = "fail";
                                }
                            }
                        }


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle("VALIDATION RESULTS ");
                        View result_layout = getLayoutInflater().inflate(R.layout.validation_results_layout2,null);
                        builder.setView(result_layout);

                        TextView technology = result_layout.findViewById(R.id.tech_type);
                        technology.setText(techtype);
                        String se;
                        se = Paper.book().read(SiteDetails.SECTOR);
                        TextView sector_no = result_layout.findViewById(R.id.sector);
                        sector_no.setText(se);


                        TextView config_heading = result_layout.findViewById(R.id.configured_heading_value);
                        config_heading.setText(heading);

                        TextView config_roll = result_layout.findViewById(R.id.configured_row_value);
                        config_roll.setText(roll);

                        TextView config_pitch = result_layout.findViewById(R.id.configured_pitch_value);
                        config_pitch.setText(pitch);


                        TextView thresh_status_heading = result_layout.findViewById(R.id.heading_thresh_status);
                        thresh_status_heading.setText(heading_threashhold_status);

                        TextView thresh_status_roll  = result_layout.findViewById(R.id.roll_thresh_status);
                        thresh_status_roll.setText(roll_threshhold_status);

                        TextView thresh_status_pitch  = result_layout.findViewById(R.id.pitch_thresh_status);
                        thresh_status_pitch.setText(pitch_threshhold_status);


                        TextView set_status_heading = result_layout.findViewById(R.id.heading_set_status);
                        set_status_heading.setText(heading_engineer_status);

                        TextView set_status_roll  = result_layout.findViewById(R.id.roll_set_status);
                        set_status_roll.setText(roll_engineer_status);

                        TextView set_status_pitch  = result_layout.findViewById(R.id.pitch_set_status);
                        set_status_pitch.setText(pitch_enginner_status);




                        TextView thresh_overall = result_layout.findViewById(R.id.overall_message_threshold);
                        TextView status_overall = result_layout.findViewById(R.id.overall_message_status);






                        builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                nextStep(heading,roll,pitch);
                            }
                        });

                        builder.setNegativeButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });



                        AlertDialog dialog = builder.create();
                        builder.show();



                        if (heading_threashhold_status.equals("fail") && roll_threshhold_status.equals("fail") && pitch_threshhold_status.equals("fail"))
                        {
                            overall_thresh_msg = "All the values did not meet the threshold limits";
                        }
                        else if (heading_threashhold_status.equals("fail") && roll_threshhold_status.equals("fail") && pitch_threshhold_status.equals("pass"))
                        {
                            overall_thresh_msg = "Pitch value meet threshold limits but  Heading and Roll did not";
                        }
                        else if (heading_threashhold_status.equals("fail") && roll_threshhold_status.equals("pass") && pitch_threshhold_status.equals("fail"))
                        {
                            overall_thresh_msg = "Roll value meet threshold limits but  Heading and Pitch did not";
                        }
                        else if (heading_threashhold_status.equals("pass") && roll_threshhold_status.equals("fail") && pitch_threshhold_status.equals("fail"))
                        {
                            overall_thresh_msg = "Heading value meet threshold limits but  Roll and Pitch did not";
                        }
                        else if (heading_threashhold_status.equals("pass") && roll_threshhold_status.equals("pass") && pitch_threshhold_status.equals("fail"))
                        {
                            overall_thresh_msg = "Heading and Roll value meet threshold limits but Pitch did not";
                        }
                        else if (heading_threashhold_status.equals("fail") && roll_threshhold_status.equals("pass") && pitch_threshhold_status.equals("pass"))
                        {
                            overall_thresh_msg = "Roll and Pitch value meet threshold limits but heading did not";
                        }
                        else if (heading_threashhold_status.equals("pass") && roll_threshhold_status.equals("fail") && pitch_threshhold_status.equals("pass"))
                        {
                            overall_thresh_msg = "Heading and Pitch value meet threshold limits but Roll did not";
                        }
                        else if (heading_threashhold_status.equals("pass") && roll_threshhold_status.equals("pass") && pitch_threshhold_status.equals("pass"))
                        {
                            overall_thresh_msg = "All the values meet the threshold limits";
                        }



                        if (heading_engineer_status.equals("fail") && roll_engineer_status.equals("fail") && pitch_enginner_status.equals("fail"))
                        {
                            overall_setvalues_msg = "All the values did not match the  installation set values";
                        }
                        else if (heading_engineer_status.equals("fail") && roll_engineer_status.equals("fail") && pitch_enginner_status.equals("pass"))
                        {
                            overall_setvalues_msg = "Pitch value match set installation values but  Heading and Roll did not";
                        }
                        else if (heading_engineer_status.equals("fail") && roll_engineer_status.equals("pass") && pitch_enginner_status.equals("fail"))
                        {
                            overall_setvalues_msg = "Roll value match set installation values but  Heading and Pitch did not";
                        }
                        else if (heading_engineer_status.equals("pass") && roll_engineer_status.equals("fail") && pitch_enginner_status.equals("fail"))
                        {
                            overall_setvalues_msg = "Heading value match set installation values but  Roll and Pitch did not";
                        }
                        else if (heading_engineer_status.equals("pass") && roll_engineer_status.equals("pass") && pitch_enginner_status.equals("fail"))
                        {
                            overall_setvalues_msg = "Heading and Roll value match set installation values but Pitch did not";
                        }
                        else if (heading_engineer_status.equals("fail") && roll_engineer_status.equals("pass") && pitch_enginner_status.equals("pass"))
                        {
                            overall_setvalues_msg = "Roll and Pitch value match set installation values but heading did not";
                        }
                        else if (heading_engineer_status.equals("pass") && roll_engineer_status.equals("fail") && pitch_enginner_status.equals("pass"))
                        {
                            overall_setvalues_msg = "Heading and Pitch value match set installation values but Roll did not";
                        }
                        else if (heading_engineer_status.equals("pass") && roll_engineer_status.equals("pass") && pitch_enginner_status.equals("pass"))
                        {
                            overall_setvalues_msg = "All the values match set installation values";
                        }


                        thresh_overall.setText(overall_thresh_msg);
                        status_overall.setText(overall_setvalues_msg);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        getEnginnersValues enginnersValues = new getEnginnersValues();
        enginnersValues.execute();
    }

    private void nextStep(final String heading, final String roll, final String pitch) {

        if ((overall_thresh_msg.equals("All the values meet the threshold limits") || (overall_setvalues_msg.equals("All the values match set installation values"))))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Validation Failed")
                    .setMessage("please set the sector to be within the set installation parameters and the threshold limits")
                    .setCancelable(false)
                    .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            sector_number = Paper.book().read(SiteDetails.SECTOR);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Success Validation Passed")
                    .setMessage(techtype+" sector: "+sector_number+ " was successfully configured.\n Proceed to configure the next sector")
                    .setCancelable(false)
                    .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           scanNextSector(sector_number,heading,roll,pitch);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void scanNextSector(String sector_number,String heading, String roll,String pitch) {
        clearvalues();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        String table_name = techtype;
        String sector_no = Paper.book().read(SiteDetails.SECTOR);
        String database_table ;

        if (techtype.equals("GSM 900"))
        {
            database_table= "GSM900";
            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("UMTS 900"))
        {
            database_table= "UMTS900";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("GSM 1800"))
        {
            database_table= "GSM1800";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("UMTS 2100"))
        {
            database_table= "UMTS2100";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("LTE 800"))
        {
            database_table= "LTE800";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("LTE 1800"))
        {
            database_table= "LTE1800";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if (techtype.equals("NBIOT 800"))
        {
            database_table= "NBIOT800";

            String confirm_update = databaseAccess.updateSector_table(database_table, sector_no);

            if (confirm_update.equals("updated"))
            {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                writeProgress(sector_number,database_table,heading,roll,pitch);
            }
            else
            {
                Toast.makeText(getActivity(), "not updated", Toast.LENGTH_SHORT).show();
            }
        }

       // Toast.makeText(getActivity(), table_name+"hhh"+sector_no, Toast.LENGTH_SHORT).show();


    }

    private void writeProgress(String sector_number,String database_table,String heading,String roll, String pitch) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();

       // String sector_no = Paper.book().read(SiteDetails.SECTOR);


        String confirm_insert = databaseAccess.WriteProgress(sector_number,heading, roll,pitch);

        if (confirm_insert.equals("inserted"))
        {
            Toast.makeText(getActivity(), "inserted into progess table", Toast.LENGTH_SHORT).show();
            checkProgress(database_table);

            if (techtype.equals("GSM 900"))
            {
                String table_name = "GSM900";
                populateSpinner(table_name);
            }
            else if(techtype.equals("UMTS 900"))
            {
                String table_name = "UMTS900";
                populateSpinner(table_name);
            }
            else if(techtype.equals("GSM 1800"))
            {
                String table_name = "GSM1800";
                populateSpinner(table_name);
            }
            else if(techtype.equals("UMTS 2100"))
            {
                String table_name = "UMTS2100";
                populateSpinner(table_name);
            }
            else if (techtype.equals("LTE 800"))
            {
                String table_name = "LTE800";
                populateSpinner(table_name);
            }
            else if (techtype.equals("LTE 1800"))
            {
                String table_name = "LTE1800";
                populateSpinner(table_name);
            }
            else  if (techtype.equals("NBIOT 800"))
            {
                String table_name = "NBIOT800";
                populateSpinner(table_name);
            }
            else
            {
                Toast.makeText(getActivity(), "technology not found", Toast.LENGTH_SHORT).show();
            }



        }
        else
        {
            Toast.makeText(getActivity(), "not inserted", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkProgress(String database_table) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();

        int sectortable_column_count = databaseAccess.CountRowsSectordb(database_table);
        int progresstable_column_count = databaseAccess.CountRowsProgressDb();

        String sectortable = techtype;
        int sectors;

        if(database_table.equals("GSM900") || database_table.equals("GSM1800") || database_table.equals("LTE800") || database_table.equals("LTE1800") || database_table.equals("NBIOT800"))
        {
            sectors = 6;

            if (sectortable_column_count == sectors && progresstable_column_count == sectors)
            {

                Fragment fragment = new ReportGeneration();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
            }
            else
            {
                Toast.makeText(getActivity(), "Configure the other sectors", Toast.LENGTH_SHORT).show();
            }
        }
        else if (database_table.equals("UMTS900") || database_table.equals("UMTS2100"))
        {
            sectors = 3;

            if (sectortable_column_count == sectors && progresstable_column_count == sectors)
            {

                Fragment fragment = new ReportGeneration();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
            }
            else
            {
                Toast.makeText(getActivity(), "Configure the other sectors", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void clearvalues() {
        scan_sim_serial.setText("Scan Sim Serial");
        sensor_scan.setText("Scan Sensor id");
    }


    private void populateSpinner(String table_name) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();

        List<String> sectors = databaseAccess.getAllSectors(table_name);
        sectors.add(0,"Select a sector");
        //creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),simple_spinner_item,sectors);

        //Drop down layout style list view
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        //attaching data to spinner
        sector_choose.setAdapter(dataAdapter);
        sector_choose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sectors = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "selected: "+sectors, Toast.LENGTH_SHORT).show();
                Paper.book().write(SiteDetails.SECTOR,sectors);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }


}
