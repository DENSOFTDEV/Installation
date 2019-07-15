package com.densoftdevelopers.installation.Fragments;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.densoftdevelopers.installation.DatabaseAccess;
import com.densoftdevelopers.installation.R;
import com.densoftdevelopers.installation.SiteDetails;

import io.paperdb.Paper;

public class ReportGeneration extends Fragment {

    TextView sitename, siteid, technology, sectors;
    TableLayout tableLayout;
    Button generateCert;
    String installation_tech;
    String site_id;
    String site_name;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_generation, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Paper.init(getActivity());

        tableLayout = (TableLayout) getActivity().findViewById(R.id.results_table);

        site_name = Paper.book().read(SiteDetails.SiteName.toString());
        site_id = Paper.book().read(SiteDetails.Site_ID.toString());
        installation_tech = Paper.book().read(SiteDetails.Site_INSTALLATION_TECH.toString());
        sitename = (TextView) getActivity().findViewById(R.id.report_site_name);
        sitename.setText(site_name);

        siteid = (TextView) getActivity().findViewById(R.id.report_site_id);
        siteid.setText(site_id);

        technology = (TextView) getActivity().findViewById(R.id.report_technology);
        technology.setText(installation_tech);

        sectors = (TextView) getActivity().findViewById(R.id.report_sectors);

        int sector_no;

        if (installation_tech.equals("GSM 900") || installation_tech.equals("GSM 1800") || installation_tech.equals("LTE 800") || installation_tech.equals("LTE 1800") || installation_tech.equals("NBIOT 800"))
        {
            sector_no = 3;
            sectors.setText(Integer.toString(sector_no));
        }
        else if (installation_tech.equals("UMTS 900") ||installation_tech.equals("UMTS 2100"))
        {
            sector_no = 6;
            sectors.setText(Integer.toString(sector_no));
        }


        populateData();

        generateCert = (Button) getActivity().findViewById(R.id.generate_certificate_btn);

        generateCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReportToAdmin();
            }
        });
    }

    private void sendReportToAdmin() {
        Toast.makeText(getActivity(), "Method not yet build!!", Toast.LENGTH_SHORT).show();
    }

    private void populateData() {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        SQLiteDatabase db = databaseAccess.db;
        databaseAccess.open();
        db.beginTransaction();


        Cursor c = databaseAccess.c;

        try
        {
           String selectQuery = "SELECT * FROM progress";

           c = db.rawQuery(selectQuery,null);

           if (c.getCount() >0)
           {
               while (c.moveToNext())
               {

                   //read columns data
                   String sector = c.getString(c.getColumnIndex("sector"));
                   String heading = c.getString(c.getColumnIndex("heading"));
                   String roll = c.getString(c.getColumnIndex("roll"));
                   String pitch = c.getString(c.getColumnIndex("pitch"));


                   //data rows
                   TableRow row = new TableRow(getActivity());
                   row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                           TableLayout.LayoutParams.MATCH_PARENT));
                   row.setBackgroundResource(R.drawable.table_row_bg);
                   row.setPadding(5,5,5,5);

                   String [] colText = {sector,heading,roll,pitch};
                   for (String text:colText)
                   {
                       TextView tv = new TextView(getActivity());
                       tv.setLayoutParams(new TableRow.LayoutParams(0,
                               TableRow.LayoutParams.WRAP_CONTENT,1));
                       tv.setGravity(Gravity.CENTER);
                       tv.setTextSize(16);
                       tv.setWidth(0);

                       if (text.equals(sector) || text.equals(heading) || text.equals(roll) )
                       {
                           tv.setBackgroundResource(R.drawable.table_cell_bg);
                       }

                       tv.setText(text);
                       row.addView(tv);
                   }
                  tableLayout.addView(row);

               }
           }

           db.setTransactionSuccessful();

        }catch (SQLException e)
        {
            String message = e.getMessage();
            Toast.makeText(getActivity(), "error while fetching data: "+ message, Toast.LENGTH_SHORT).show();
        }
        finally
        {
            db.endTransaction();
            //end transaction
            db.close();
            //close db
        }





    }


}
