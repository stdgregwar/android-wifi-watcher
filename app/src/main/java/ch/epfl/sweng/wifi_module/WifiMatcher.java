package ch.epfl.sweng.wifi_module;

import java.util.List;
import java.util.Map;

/**
 * Created by gregwar on 11/10/16.
 */

public class WifiMatcher {
    private Map<String, List<RoomSignature>> roomdb;
    private double threshold = 1.0;

    public WifiMatcher(Map<String, List<RoomSignature>> roomdb) {
        this.roomdb = roomdb;
    }

    public String roomForSignature(RoomSignature sign) {
        double rank = 0;
        String name = "None";
        for(Map.Entry<String,List<RoomSignature>> e : roomdb.entrySet()) {
            List<RoomSignature> rsigns = e.getValue();
            for(RoomSignature rsign : rsigns) {
                double nrank = sign.matchRank(rsign);
                if (nrank > rank) {
                    rank = nrank;
                    name = e.getKey();
                }
            }
        }
        return name;
    }
}
