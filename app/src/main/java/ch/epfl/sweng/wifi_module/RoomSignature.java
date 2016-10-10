package ch.epfl.sweng.wifi_module;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raph on 10/10/16.
 */

public class RoomSignature {
    private String mRoomName;
    private String mUID;
    private long unixTimestamp;
    private List<AccessPointDescription> mAccessPointDescriptions;

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
