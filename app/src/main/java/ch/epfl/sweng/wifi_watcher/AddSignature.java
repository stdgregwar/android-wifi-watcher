package ch.epfl.sweng.wifi_watcher;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.wifi_module.WifiMeter;

public class AddSignature extends AppCompatActivity {

    private WifiMeter meter;
    private WifiManager mWifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_signature);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        meter  = new WifiMeter(this);
    }

    public void onButtonRefreshWifiClick(View v) {
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return -WifiManager.compareSignalLevel(lhs.level,rhs.level);
            }
        };
        List<ScanResult> mWifiManagerScanResults = meter.getCurrentSignature();
        Collections.sort(mWifiManagerScanResults, comparator);
        List<String> mWifiSSID = new ArrayList<>();
        for(ScanResult scanResult : mWifiManagerScanResults) {
            if(!mWifiSSID.contains(scanResult.SSID)){
                mWifiSSID.add(scanResult.SSID);
            }
        }

        mWifiSSID.add(0,"None");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_filter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,mWifiSSID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void onButtonAddSignature(View v) {
        String roomName = ((EditText) findViewById(R.id.room_name)).getText().toString();
        if(!isValidRoomName(roomName)) {
            Toast.makeText(this,"Invalid room name, please retry", Toast.LENGTH_SHORT).show();
            return;
        }
        String filter = String.valueOf(((Spinner) findViewById(R.id.spinner_filter)).getSelectedItem());
        List<ScanResult> scanResults;
        if(filter.equals("None")) {
            scanResults = meter.getCurrentSignature();
        } else {
            scanResults = meter.getCurrentSignature(filter);
        }
        Toast.makeText(this,"There were " + scanResults.size() + " AP for the signature of room " + roomName,Toast.LENGTH_LONG).show();
    }

    private boolean isValidRoomName(String name) {
        if(name.length() < 1){
            return false;
        }
        return true;
    }
}