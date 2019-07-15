package com.densoftdevelopers.installation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.densoftdevelopers.installation.Fragments.SectorChoose;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanDevice_id extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_scan_device_id);

        CardView camerashow = (CardView) findViewById(R.id.camera_layout);
        camerashow.addView(scannerView);
        scannerView.setSoundEffectsEnabled(true);
        scannerView.setAutoFocus(true);
        scannerView.setFlash(true);
    }

    @Override
    public void handleResult(Result result) {
        SectorChoose.sensor_scan.setText(result.getText());
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
