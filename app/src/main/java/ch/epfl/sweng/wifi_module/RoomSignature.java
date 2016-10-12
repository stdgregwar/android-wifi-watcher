package ch.epfl.sweng.wifi_module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raph on 10/10/16.
 */

public class RoomSignature {
    private String mRoomName;
    private String mUID;
    private long unixTimestamp;
    private List<AccessPointDescription> mAccessPointDescriptions;
    private final double SQ2P = Math.sqrt(2*Math.PI);

    public RoomSignature(String roomName, String UID, long unixTimestamp, List<AccessPointDescription> accessPointDescriptions) {
        mRoomName = roomName;
        mUID = UID;
        this.unixTimestamp = unixTimestamp;
        mAccessPointDescriptions = accessPointDescriptions;
    }

    public RoomSignature(String roomName, String UID, List<AccessPointDescription> accessPointDescriptions) {
        mRoomName = roomName;
        mUID = UID;
        unixTimestamp = System.currentTimeMillis() / 1000L;
        mAccessPointDescriptions = accessPointDescriptions;
    }

    /**
     * Map BSSIDS to normalized ap levels of the room
     *
     * @return a map of levels by BSSID
     */
    private Map<String,Double> normalizedLevels() {
        Map<String,Double> levels = new HashMap<>();
        double allLevels = 0;
        for(AccessPointDescription apd : mAccessPointDescriptions) {
            levels.put(apd.getBSSID(),(double)apd.getLevel());
            allLevels += apd.getLevel();
        }
        //Normalize levels
        for(Map.Entry<String,Double> e : levels.entrySet()) {
            e.setValue(e.getValue()/allLevels);
        }
        return levels;
    }

    /**
     *
     * @param center top of gauss curve
     * @param var width of curve
     * @param x sampling point
     * @return sample
     */
    private double gauss(double center, double var, double x) {
        return (1/(var*SQ2P))*Math.exp(-Math.pow(x-center,2)/(2*var*var));
    }

    /**
     * Return the match rank of another RoomSignature with this one
     *
     * @param other another RoomSignature to compare
     * @return the rank as a double, higher means better match
     */
    public double matchRank(RoomSignature other) {
        double rank = 0;
        final double VAR = 0.3;
        Map<String,Double> mnLevels = normalizedLevels();
        Map<String,Double> tnLevels = other.normalizedLevels();

        for(Map.Entry<String,Double> me : mnLevels.entrySet()) {
            if(tnLevels.containsKey(me.getKey())) {
                Double mlevel = me.getValue();
                Double tlevel = tnLevels.get(me.getKey());
                rank += gauss(mlevel, VAR, tlevel);
            } //If the other RoomSignature has not the key just add 0
        }

        return rank;
    }

    public List<AccessPointDescription> getAccessPointDescriptions() {
        return new ArrayList<>(mAccessPointDescriptions);
    }

    @Override
    public String toString() {
        return "RoomSignature{" +
                "mRoomName='" + mRoomName + '\'' +
                ", mUID='" + mUID + '\'' +
                ", unixTimestamp=" + unixTimestamp +
                ", mAccessPointDescriptions=" + mAccessPointDescriptions +
                '}';
    }
}
