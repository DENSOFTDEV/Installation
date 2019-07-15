package com.densoftdevelopers.installation;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import com.densoftdevelopers.installation.Fragments.ReportGeneration;
import com.densoftdevelopers.installation.Fragments.SectorChoose;
import com.densoftdevelopers.installation.Fragments.TechnologySelect;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    Toolbar toolbar;
    TextView account_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(HomeActivity.this);

        String name = Paper.book().read(UserDetails.Username);



        account_details = (TextView) findViewById(R.id.user_profile);
        account_details.setText("Welcome: "+name);
        toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TechnologySelect(),"select_technology").commit();

        SectorChoose sectorChoose = (SectorChoose) getSupportFragmentManager().findFragmentByTag("sector_choose");

        SectorChoose reportgen = (SectorChoose) getSupportFragmentManager().findFragmentByTag("report_gen");


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId())
                    {
                        case R.id.home:
                            selectedFragment = new TechnologySelect();
                            break;
                        case R.id.refresh:
                           // Toast.makeText(HomeActivity.this, "Installation process to be restarted", Toast.LENGTH_SHORT).show();
                            RefreshAll();
                            break;
                        case R.id.logout:

                            logoutAlert();

                            break;
                    }
                    if (selectedFragment != null)
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };

    private void logoutAlert() {

        String name = Paper.book().read(UserDetails.Username);
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle("Logging Out!!");
        builder.setCancelable(false);
        builder.setMessage("Dear "+name+" you are shutting down the application, you will be required to login to use the app again");
        builder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void logout() {
    }

    private void RefreshAll() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
         builder.setTitle("Process Restart!!");
         builder.setCancelable(false);
         builder.setMessage("You are about to start the device installation process, earlier configured sectors data will be deleted.");
         builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 DodbRefresh();
             }
         }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
             }
         });
         builder.create().show();


    }

    private void DodbRefresh() {

    }
}
