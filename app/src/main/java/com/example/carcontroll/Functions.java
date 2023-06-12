package com.example.carcontroll;

import android.content.SharedPreferences;
import android.util.Log;

public class Functions {
    SharedPreferences prefs;
    public Functions(SharedPreferences _prefs){
        prefs = _prefs;
    }

    public String switchValue(String value,String prefAddress){
        String retData;
        if(value.equals("ON")){
            retData = "ON"; // fix errors ?!
        }else{
            retData = "OFF"; // fix errors ?!
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefAddress, retData);
        editor.apply();
        return retData;
    }


}
