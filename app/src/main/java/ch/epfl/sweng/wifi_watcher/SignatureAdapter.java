package ch.epfl.sweng.wifi_watcher;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.wifi_module.AccessPointDescription;

/**
 * Created by gregwar on 07.10.16.
 */

public class SignatureAdapter extends BaseAdapter {
    private List<AccessPointDescription> data;
    public SignatureAdapter(List<AccessPointDescription> data){
        this.data  = data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        if(convertView != null) {
            return convertView;
        } else {
            View row;
            row = inflater.inflate(R.layout.list_entry, parent, false);
            TextView SSID, BSSID, level;
            SSID = (TextView) row.findViewById(R.id.ssid_view);
            BSSID = (TextView) row.findViewById(R.id.bssid_view);
            level = (TextView) row.findViewById(R.id.level_view);

            SSID.setText(data.get(pos).getSSID());
            BSSID.setText(data.get(pos).getBSSID());
            level.setText("Level : " + data.get(pos).getLevel());

            return (row);
        }
    }
}
