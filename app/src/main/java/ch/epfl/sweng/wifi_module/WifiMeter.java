package ch.epfl.sweng.wifi_module;

/**
 * Created by gregwar on 07.10.16.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class WifiMeter {
    private WifiManager manager;

    public WifiMeter(Context context) {
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public List<ScanResult> getCurrentSignature() {
        return getCurrentSignature(".*");
    }
    public List<ScanResult> getCurrentSignature(String ssidFilter) {
        List<ScanResult> results = manager.getScanResults();
        Log.v("BOUDIN","List size = " + results.size());
        List<ScanResult> filtered = new ArrayList<>();
        for(ScanResult sr : results) {
            if(sr.SSID.matches(ssidFilter)) {
                filtered.add(sr);
            }
        }
        Log.v("BOUDIN","List size = " + filtered.size());
        return filtered;
    }

}
