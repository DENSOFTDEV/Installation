package com.densoftdevelopers.installation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.densoftdevelopers.installation.Fragments.SectorChoose;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Sim_serial extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_sim_serial);
        CardView camerashow = (CardView) findViewById(R.id.camera_layout);
        camerashow.addView(scannerView);
        scannerView.setSoundEffectsEnabled(true);
        scannerView.setAutoFocus(true);
        scannerView.setFlash(true);
    }

    @Override
    public void handleResult(Result result) {
        SectorChoose.scan_sim_serial.setText(result.getText());
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
