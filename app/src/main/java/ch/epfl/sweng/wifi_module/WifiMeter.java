package ch.epfl.sweng.wifi_module;

/**
 * Created by gregwar on 07.10.16.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiMeter {
    private WifiManager manager;

    public WifiMeter(Context context) {
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public List<AccessPointDescription> getCurrentSignature() {
        return getCurrentSignature(".*");
    }

    public List<AccessPointDescription> getCurrentSignature(String ssidFilter) {
        List<ScanResult> results = manager.getScanResults();
        List<AccessPointDescription> filtered = new ArrayList<>();
        for (ScanResult sr : results) {
            if (sr.SSID.matches(ssidFilter)) {
                filtered.add(new AccessPointDescription(sr));
            }
        }
        return filtered;
    }

}
