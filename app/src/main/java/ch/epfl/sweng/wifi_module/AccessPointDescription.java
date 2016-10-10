package ch.epfl.sweng.wifi_module;

import android.net.wifi.ScanResult;

/**
 * Created by raph on 10/10/16.
 */

public class AccessPointDescription {
    private String BSSID;
    private String SSID;
    private int level;
    private int frequency;


    public AccessPointDescription(String BSSID, String SSID, int level, int frequency) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.level = level;
        this.frequency = frequency;
    }

    public AccessPointDescription(ScanResult scanResult) {
        this.BSSID = scanResult.BSSID;
        this.SSID = scanResult.SSID;
        this.level = scanResult.level;
        this.frequency = scanResult.frequency;
    }


    public String getBSSID() {
        return BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public int getLevel() {
        return level;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "AccessPointDescription{" +
                "BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                ", level=" + level +
                ", frequency=" + frequency +
                '}';
    }
}
