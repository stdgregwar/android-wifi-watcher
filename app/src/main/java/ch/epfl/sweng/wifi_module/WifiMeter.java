package ch.epfl.sweng.wifi_module;

/**
 * Created by gregwar on 07.10.16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiMeter {
    private WifiManager manager;
    final private Context context;

    public interface Listener {
        public void onScanResultsReady(List<AccessPointDescription> results);
    }

    public WifiMeter(Context context) {
        this.context = context;
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

    public void scheduleGetSignature(Listener li) {
        scheduleGetSignature(li,".*");
    }
    public void scheduleGetSignature(Listener li,String filter) {
        final Listener fli = li;
        final String ffilter = filter;
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fli.onScanResultsReady(getCurrentSignature(ffilter));
                context.unregisterReceiver(this);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // You can add multiple actions...
        context.registerReceiver(br, intentFilter);
        manager.startScan();
    }

}
