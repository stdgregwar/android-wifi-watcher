package ch.epfl.sweng.wifi_module;

import android.net.wifi.ScanResult;

/**
 * Class describing an AP
 */

public class AccessPointDescription {
    private final String BSSID;
    private final String SSID;
    private final int level;
    private final int frequency;

    /**
     * Default constructor of an AP description
     * @param BSSID BSSID of the AP
     * @param SSID SSID of the AP
     * @param level level in dBm
     * @param frequency The primary 20 MHz frequency (in MHz) of the channel over which the client is communicating with the access point.
     */
    public AccessPointDescription(String BSSID, String SSID, int level, int frequency) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.level = level;
        this.frequency = frequency;
    }

    /**
     * Alternate constructor, construct from a ScanResult (https://developer.android.com/reference/android/net/wifi/ScanResult.html)
     * @param scanResult Result of scan
     */
    public AccessPointDescription(ScanResult scanResult) {
        this(scanResult.BSSID, scanResult.SSID, scanResult.level, scanResult.frequency);
    }

    /**
     * Getter for the BSSID
     * @return the BSSID
     */
    public String getBSSID() {
        return BSSID;
    }

    /**
     * Getter for the SSID
     * @return the SSID
     */
    public String getSSID() {
        return SSID;
    }

    /**
     * Getter for the level
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter for the frequency
     * @return the frequency
     */
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
