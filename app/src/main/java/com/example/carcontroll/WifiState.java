package com.example.carcontroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiState {
    Context context;
    WifiManager wifi;
    WifiConfiguration wc;
    WifiInfo wifiInfo;
    Activity activity;
    public WifiState(Context _context,Activity _activity) {
        context = _context;
        activity = _activity;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wc = new WifiConfiguration();
        wifiInfo = wifi.getConnectionInfo();
    }

    public boolean isItWifiEnabled(){
        if(wifi.isWifiEnabled()){
            return true;
        }else{
            return false;
        }
    }
    public boolean isItWifiConnected(){
        TextView txtWarning = (TextView) activity.findViewById(R.id.TXT_WARNING);
        if(isItWifiEnabled()){
            // cut the comas from SSID string
            String s = wifiInfo.getSSID();
            String r = s.substring(1,s.length() -1);
            //Log.w("WARNING"," -> WIFI NAME " + r);
            if(r.equals(Config.SSID)){
                txtWarning.setVisibility(View.GONE);
                return true;
            }else{
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Wifi is not connected to the car!");
                return false;
            }
        }else{
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Wifi is not connected to the car!");
            return false;
        }
    }
}
