package ch.epfl.sweng.wifi_watcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.wifi_module.AccessPointDescription;
import ch.epfl.sweng.wifi_module.RoomSignature;
import ch.epfl.sweng.wifi_module.WifiMatcher;
import ch.epfl.sweng.wifi_module.WifiMeter;

public class NetworkList extends AppCompatActivity {
    private static final String TAG = "NetworkList";
    private final static int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;

    private WifiMatcher matcher = null;
    private WifiMeter meter;

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
                Toast.makeText(this, "The application needs access to location in order to scan wifi.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            meter = new WifiMeter(this);
        }

        try {
            Map<String, List<RoomSignature>> roomSignatureMapParsed;
            String path = this.getFilesDir().getAbsolutePath() + "/" + AddSignature.WIFI_MAP_JSON;
            File wifiMap = new File(path);

            if (wifiMap.createNewFile()) {
                roomSignatureMapParsed = new HashMap<>();
            } else {
                Gson gson = new Gson();
                FileInputStream fileInputStream = openFileInput(AddSignature.WIFI_MAP_JSON);
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = fileInputStream.read()) != -1) {
                    builder.append((char) ch);
                }
                fileInputStream.close();
                try {
                    roomSignatureMapParsed = gson.fromJson(builder.toString(), new TypeToken<Map<String, List<RoomSignature>>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Error reading JSON, deleting the file: "+ wifiMap.toString());
                    wifiMap.delete();
                    roomSignatureMapParsed = new HashMap<>();
                }
            }
            matcher = new WifiMatcher(roomSignatureMapParsed);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found with error: ", e);
        } catch (IOException e) {
            Log.e(TAG, "IO problem with error: ", e);
        }
    }

    public void onButtonRefreshClick(View v) {
        if (meter == null) {
            Log.e(TAG, "Meter wasn't initialized (certainly a permission problem)");
            return;
        }
        final ListView listView = (ListView) findViewById(R.id.list_view);
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
        final Context that = this;
        WifiMeter.Listener li = new WifiMeter.Listener() {
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

    public void onButtonMatch(View v) {
        if (meter == null) {
            Log.e(TAG, "Meter wasn't initialized (certainly a permission problem)");
            return;
        }
        if (matcher != null) {
            //List<AccessPointDescription> scanResults = meter.getCurrentSignature();

            final Context that = this;
            Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
            WifiMeter.Listener li = new WifiMeter.Listener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    meter = new WifiMeter(this);
                } else {
                    Log.e(TAG, "Permission to wifi refused");
                    Toast.makeText(this, "Closing...", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
