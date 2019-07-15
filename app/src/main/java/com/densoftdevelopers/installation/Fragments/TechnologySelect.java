package com.densoftdevelopers.installation.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.layout.simple_spinner_item;

import com.densoftdevelopers.installation.DatabaseHandler;
import com.densoftdevelopers.installation.R;
import com.densoftdevelopers.installation.SiteDetails;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;


public class TechnologySelect extends Fragment {


    DatabaseHandler mydb;
    public static TextView sitename,siteid;
    public static Button choosesector;
    public static Spinner select_Tech;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_technology_select, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Paper.init(getActivity());

       // mydb = new DatabaseHandler(getActivity());

        String[] tech = {"GSM 900","UMTS","GSM 1800","LTE 800","LTE 1800","NBIOT 800"};


        sitename = (TextView)getView().findViewById(R.id.select_tech_sitetitle);
        siteid = (TextView) getView().findViewById(R.id.select_tech_siteId);
        select_Tech = (Spinner) getView().findViewById(R.id.select_tech_spinner);


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Select a technology");
        arrayList.add("GSM 900");
        arrayList.add("UMTS 900");
        arrayList.add("GSM 1800");
        arrayList.add("UMTS 2100");
        arrayList.add("LTE 800");
        arrayList.add("LTE 1800");
        arrayList.add("NBIOT 800");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_Tech.setAdapter(arrayAdapter);
        select_Tech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String technames = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + technames,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String name = Paper.book().read(SiteDetails.SiteName);
        sitename.setText(name);
        String id = Paper.book().read(SiteDetails.Site_ID);
        siteid.setText(id);

        choosesector = (Button) getView().findViewById(R.id.choose_sector_btn);

        choosesector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_Tech.getSelectedItem().toString().equals("Select a technology"))
                {
                    Toast.makeText(getActivity(), "Please select a technology in the list to continue", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Paper.book().write(SiteDetails.Site_INSTALLATION_TECH,select_Tech.getSelectedItem().toString());

                    populateProgressDb();

                    Fragment fragment = new SectorChoose();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    private void populateProgressDb() {

    }


}

