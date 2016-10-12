package ch.epfl.sweng.wifi_watcher;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.wifi_module.AccessPointDescription;
import ch.epfl.sweng.wifi_module.RoomSignature;
import ch.epfl.sweng.wifi_module.WifiMatcher;
import ch.epfl.sweng.wifi_module.WifiMeter;

public class NetworkList extends AppCompatActivity {
    private final static int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;

    private WifiMatcher matcher = null;
    private WifiMeter meter;
    private WifiManager mWifiManager;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Permission checking
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        } else {
            meter = new WifiMeter(this);
        }

        Gson gson = new Gson();
        try {
            FileInputStream fileInputStream = openFileInput(AddSignature.WIFI_MAP_JSON);
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                builder.append((char) ch);
            }
            fileInputStream.close();
            Map<String, List<RoomSignature>> roomSignatureMapParsed = gson.fromJson(builder.toString(), new TypeToken<Map<String, List<RoomSignature>>>() {
            }.getType());


            matcher = new WifiMatcher(roomSignatureMapParsed);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

    }

    public void onButtonRefreshClick(View v) {
        final ListView listView = (ListView) findViewById(R.id.list_view);
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
        final Context that = this;
        WifiMeter.Listener li = new WifiMeter.Listener(){
            @Override
            public void onScanResultsReady(List<AccessPointDescription> results) {
                listView.setAdapter(new SignatureAdapter(results));
                Toast.makeText(that, "Done!", Toast.LENGTH_SHORT).show();
            }
        };
        meter.scheduleGetSignature(li);
    }

    public void onButtonSwitchActivity(View v) {
        Intent intent = new Intent(this, AddSignature.class);
        this.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    meter = new WifiMeter(this);
                    Log.v("Request", "acess");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onButtonMatch(View v) {
        if (matcher != null) {
            //List<AccessPointDescription> scanResults = meter.getCurrentSignature();

            final Context that = this;
            Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
            WifiMeter.Listener li = new WifiMeter.Listener(){
                @Override
                public void onScanResultsReady(List<AccessPointDescription> results) {
                    RoomSignature tmpSign = new RoomSignature("Unknow", "", 0, results);
                    String found = matcher.roomForSignature(tmpSign);
                    Toast.makeText(that, "The current signature was correlated to " + found + ".", Toast.LENGTH_SHORT).show();
                }
            };
            meter.scheduleGetSignature(li);
        } else {
            Toast.makeText(this, "Room database is not loaded.", Toast.LENGTH_LONG).show();
        }
    }


}
