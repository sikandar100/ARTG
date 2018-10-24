package com.example.dell.vrtg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class Main_POI extends AppCompatActivity {

    private ViewMain Content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__poi);

        this.setTitle("ARTG");

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showSettingAlert();
        }

        FrameLayout ViewPane2 = (FrameLayout) findViewById(R.id.ar_view_pane_2);
        FrameLayout ViewPane3 = (FrameLayout) findViewById(R.id.ar_view_pane_3);

        DisplayView displayView1 = new DisplayView(this);
        ViewPane3.addView(displayView1);

        Content = new ViewMain(getApplicationContext());
        ViewPane2.addView(Content);



    }
    @Override
    protected void onPause() {
        Content.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Content.onResume();
        super.onResume();
    }

    public void showSettingAlert(){


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS Not Enabled");

        alertDialog.setMessage("Do you want to turn On GPS");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();

    }

}

