package ch.epfl.sweng.wifi_watcher;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.wifi_module.AccessPointDescription;
import ch.epfl.sweng.wifi_module.RoomSignature;
import ch.epfl.sweng.wifi_module.WifiMeter;

public class AddSignature extends AppCompatActivity {
    private final static String WIFI_MAP_JSON = "wifiMap.json";
    private String uid;
    private WifiMeter meter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = Settings.Secure.getString(this.getContentResolver()
                , Settings.Secure.ANDROID_ID);
        setContentView(R.layout.activity_add_signature);
        meter = new WifiMeter(this);
    }

    public void onButtonRefreshWifiClick(View v) {
        List<AccessPointDescription> mWifiManagerScanResults = meter.getCurrentSignature();
        Collections.sort(mWifiManagerScanResults, new Comparator<AccessPointDescription>() {
            @Override
            public int compare(AccessPointDescription lhs, AccessPointDescription rhs) {
                return -WifiManager.compareSignalLevel(lhs.getLevel(), rhs.getLevel());
            }
        });
        List<String> mWifiSSID = new ArrayList<>();
        for (AccessPointDescription scanResult : mWifiManagerScanResults) {
            if (!mWifiSSID.contains(scanResult.getSSID())) {
                mWifiSSID.add(scanResult.getSSID());
            }
        }

        mWifiSSID.add(0, "None");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_filter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mWifiSSID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void onButtonAddSignature(View v) {
        String roomName = ((EditText) findViewById(R.id.room_name)).getText().toString();
        if (!isValidRoomName(roomName)) {
            Toast.makeText(this, "Invalid room name, please retry", Toast.LENGTH_SHORT).show();
            return;
        }
        String filter = String.valueOf(((Spinner) findViewById(R.id.spinner_filter)).getSelectedItem());
        List<AccessPointDescription> scanResults;
        if (filter.equals("None")) {
            scanResults = meter.getCurrentSignature();
        } else {
            scanResults = meter.getCurrentSignature(filter);
        }

        long time = System.currentTimeMillis() / 1000L;
        RoomSignature roomSignature = new RoomSignature(roomName, uid, time, scanResults);

        Map<String, List<RoomSignature>> roomSignatureMap = new HashMap<>();
        roomSignatureMap.put(roomName, Arrays.asList(roomSignature));

        Gson gson = new Gson();

        try {
            if (!isWifiMapPresent()) {
                FileOutputStream fileOutputStream = openFileOutput(WIFI_MAP_JSON, Context.MODE_PRIVATE);
                fileOutputStream.write(gson.toJson(roomSignatureMap).getBytes());
                fileOutputStream.close();
            } else {
                FileInputStream fileInputStream = openFileInput(WIFI_MAP_JSON);
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = fileInputStream.read()) != -1) {
                    builder.append((char) ch);
                }
                fileInputStream.close();
                Map<String, List<RoomSignature>> roomSignatureMapParsed = gson.fromJson(builder.toString(), new TypeToken<Map<String, List<RoomSignature>>>() {
                }.getType());
                if (roomSignatureMapParsed.containsKey(roomName)) {
                    List<RoomSignature> list = roomSignatureMapParsed.get(roomName);
                    list.add(roomSignature);
                    roomSignatureMapParsed.put(roomName, list);
                } else {
                    roomSignatureMapParsed.put(roomName, Arrays.asList(roomSignature));
                }
                FileOutputStream fileOutputStream = openFileOutput(WIFI_MAP_JSON, Context.MODE_PRIVATE);
                fileOutputStream.write(gson.toJson(roomSignatureMapParsed).getBytes());
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        Toast.makeText(this, "There were " + scanResults.size() + " AP for the signature of room " + roomName, Toast.LENGTH_LONG).show();
    }

    private boolean isValidRoomName(String name) {
        return name.length() >= 1;
    }

    public boolean isWifiMapPresent() {
        String path = this.getFilesDir().getAbsolutePath() + "/" + WIFI_MAP_JSON;
        File file = new File(path);
        return file.exists();
    }
}