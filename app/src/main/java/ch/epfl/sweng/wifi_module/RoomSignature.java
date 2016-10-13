package ch.epfl.sweng.wifi_module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Class describing a room signature
 *
 */

public class RoomSignature {
    private final String mRoomName;
    private final String mUID;
    private final long unixTimestamp;
    private final List<AccessPointDescription> mAccessPointDescriptions;
    private final double SQ2P = Math.sqrt(2*Math.PI);

    /**
     * Default constructor of a new Room signature using the given parameters
     * @param roomName Name of the room described by the signature
     * @param UID   Unique ID of the android device creating the signature
     * @param unixTimestamp Unix timestamp of the signature creation
     * @param accessPointDescriptions List of description of the AP characterizing the signature
     */
    public RoomSignature(String roomName, String UID, long unixTimestamp, List<AccessPointDescription> accessPointDescriptions) {
        mRoomName = roomName;
        mUID = UID;
        this.unixTimestamp = unixTimestamp;
        mAccessPointDescriptions = new ArrayList<>(accessPointDescriptions);
    }

    /**
     * Alternative constructor which set the timestamp to the current time
     * @param roomName Name of the room described by the signature
     * @param UID   Unique ID of the android device creating the signature
     * @param accessPointDescriptions List of description of the AP characterizing the signature
     */
    public RoomSignature(String roomName, String UID, List<AccessPointDescription> accessPointDescriptions) {
        this(roomName,UID,System.currentTimeMillis() / 1000L,accessPointDescriptions);
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

    /**
     * Getter for the list of AP of the signature
     * @return The list of AP of the signature
     */
    public List<AccessPointDescription> getAccessPointDescriptions() {
        return Collections.unmodifiableList(mAccessPointDescriptions);
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
