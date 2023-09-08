package com.example.carcontroll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //region variables and constants
    public static Context contextOfApplication;
    public static Context getContextOfApplication(){return contextOfApplication;}
    public static SharedPreferences prefs;

    public static String defDrlState = "OFF",defInterState= "OFF",defAmpState= "OFF",defDvrState = "OFF"; // use it to initialize interfaces at begin
    public static String drlDelay = "5",interDelay= "5",ampDelay= "5",dvrDelay= "5";
    private String drlState,interState,ampState,dvrState; // actual state to show on UI and send over internet

    Spinner spnDrlDelay,spnAmpDelay,spnDvrDelay,spnIntDelay;
    SwitchCompat tglDrl,tglAmp,tglDvr,tglInt;
    ImageButton btnDrl,btnAmp,btnDvr,btnInt;
    Button btnSaveSettings;
    TextView debugText;
    LinearLayout saveBtnLO,notificationsLO;

    private boolean fRun = true;

    VolleyRequest volleyRequest;
    UI ui;
    Functions func;
    SharedPreferences.Editor editor;
    permissionManager permManager;
    WifiState wifiState;


    //endregion
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();
        prefs  = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
        ui = new UI(prefs);
        func = new Functions(prefs);
        volleyRequest = new VolleyRequest(contextOfApplication,Config.URL);
        permManager = new permissionManager(MainActivity.this,getContextOfApplication());


        wifiState = new WifiState(contextOfApplication,this);

        Log.w(Config.W_TAG,"Connected ? " + wifiState.isItWifiConnected());

        //region get UI elements
        btnDrl = (ImageButton) findViewById(R.id.BTN_DRL);
        btnAmp = (ImageButton) findViewById(R.id.BTN_AMP);
        btnDvr = (ImageButton) findViewById(R.id.BTN_DVR);
        btnInt = (ImageButton) findViewById(R.id.BTN_INT);

        btnSaveSettings = (Button) findViewById(R.id.BTN_SAVE_SETTINGS);

        tglDrl = (SwitchCompat) findViewById(R.id.TGL_DRL_DEF_STATE);
        tglAmp = (SwitchCompat) findViewById(R.id.TGL_AMP_DEF_STATE);
        tglDvr = (SwitchCompat) findViewById(R.id.TGL_DVR_DEF_STATE);
        tglInt = (SwitchCompat) findViewById(R.id.TGL_INT_DEF_STATE);

        spnDrlDelay = (Spinner) findViewById(R.id.SPINNER_DRL_DELAY);
        spnAmpDelay = (Spinner) findViewById(R.id.SPINNER_AMP_DELAY);
        spnDvrDelay = (Spinner) findViewById(R.id.SPINNER_DVR_DELAY);
        spnIntDelay = (Spinner) findViewById(R.id.SPINNER_INT_DELAY);

        saveBtnLO = (LinearLayout) findViewById(R.id.LO_BTN_SAVE_REGION);
        notificationsLO = (LinearLayout) findViewById(R.id.LO_NOTIFICATIONS);

        //endregion
        //region initialize data
        initializeData();
        // endregion
        //region click listeners
        btnDrl.setOnClickListener(view -> {
            String tmp;
            tmp = func.switchValue(drlState,"drlState");
            drlState = tmp;
            String resp = volleyRequest.SendRequest(String.format("switch?element=%s&val=%s","drl",drlState.toLowerCase()));
            //Toast.makeText(contextOfApplication,resp,Toast.LENGTH_SHORT).show();
            if(resp.equals("true")){
                ui.update(btnDrl,"drlState");
                Toast toast = Toast.makeText(contextOfApplication, "DRL SWITCHED TO: " + tmp, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //reverse the state when request is not done
                tmp = func.switchValue(drlState,"drlState");
                drlState = tmp;
            }
        });
        btnAmp.setOnClickListener(view -> {
            String tmp;
            tmp = func.switchValue(ampState,"ampState");
            ampState = tmp;
            String resp = volleyRequest.SendRequest(String.format("switch?element=%s&val=%s","amp",ampState.toLowerCase()));
            if(resp.equals("true")){
                ui.update(btnAmp,"ampState");
                Toast toast = Toast.makeText(contextOfApplication, "AMP SWITCHED TO: " + tmp, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //reverse the state when request is not done
                tmp = func.switchValue(ampState,"ampState");
                ampState = tmp;
            }
            //ui.update(btnAmp,"ampState");
        });
        btnDvr.setOnClickListener(view -> {
            String tmp;
            tmp = func.switchValue(dvrState,"dvrState");
            dvrState = tmp;
            String resp = volleyRequest.SendRequest(String.format("switch?element=%s&val=%s","dvr",dvrState.toLowerCase()));
            if(resp.equals("true")){
                ui.update(btnDvr,"dvrState");
                Toast toast = Toast.makeText(contextOfApplication, "DVR SWITCHED TO: " + tmp, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //reverse the state when request is not done
                tmp = func.switchValue(dvrState,"dvrState");
                dvrState = tmp;
            }
            //ui.update(btnDvr,"dvrState");
        });
        btnInt.setOnClickListener(view -> {
            String tmp;
            tmp = func.switchValue(interState,"interState");
            interState = tmp;
            String resp = volleyRequest.SendRequest(String.format("switch?element=%s&val=%s","int",interState.toLowerCase()));
            if(resp.equals("true")){
                ui.update(btnInt,"interState");
                Toast toast = Toast.makeText(contextOfApplication, "INT SWITCHED TO: " + tmp, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //reverse the state when request is not done
                tmp = func.switchValue(interState,"interState");
                interState = tmp;
            }
            //ui.update(btnInt,"interState");
        });
        btnSaveSettings.setOnClickListener(view -> {
            saveData();
            saveBtnLO.setVisibility(View.GONE);

        });

        spnDrlDelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (saveBtnLO.getVisibility() == View.GONE) {
                        saveBtnLO.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        spnAmpDelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (saveBtnLO.getVisibility() == View.GONE) {
                        saveBtnLO.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        spnDvrDelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (saveBtnLO.getVisibility() == View.GONE) {
                        saveBtnLO.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        spnIntDelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (saveBtnLO.getVisibility() == View.GONE) {
                        saveBtnLO.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        tglDrl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (saveBtnLO.getVisibility() == View.GONE) {
                    saveBtnLO.setVisibility(View.VISIBLE);
                }
            }
        });
        tglDvr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (saveBtnLO.getVisibility() == View.GONE) {
                    saveBtnLO.setVisibility(View.VISIBLE);
                }
            }
        });
        tglInt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (saveBtnLO.getVisibility() == View.GONE) {
                    saveBtnLO.setVisibility(View.VISIBLE);
                }
            }
        });
        tglAmp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (saveBtnLO.getVisibility() == View.GONE) {
                    saveBtnLO.setVisibility(View.VISIBLE);
                }
            }
        });
        //endregion

    }

    @Override
    public void onClick(View view) {

    }
    private void getData(SharedPreferences prefs){
        if (prefs.contains("drlState") & prefs.contains("dvrDelay")) {
            drlState = prefs.getString("drlState",null);
            interState = prefs.getString("interState",null);
            ampState = prefs.getString("ampState",null);
            dvrState = prefs.getString("dvrState",null);
            defDrlState = prefs.getString("defDrlState",null);
            defInterState = prefs.getString("defInterState",null);
            defAmpState = prefs.getString("defAmpState",null);
            defDvrState = prefs.getString("defDvrState",null);
            drlDelay = prefs.getString("drlDelay",null);
            interDelay = prefs.getString("interDelay",null);
            ampDelay = prefs.getString("ampDelay",null);
            dvrDelay = prefs.getString("dvrDelay",null);

        } else {
            editor = prefs.edit();
            editor.putString("drlState", "OFF");
            editor.putString("interState", "OFF");
            editor.putString("ampState", "OFF");
            editor.putString("dvrState", "OFF");

            editor.putString("defDrlState", "OFF");
            editor.putString("defInterState", "OFF");
            editor.putString("defAmpState", "OFF");
            editor.putString("defDvrState", "OFF");

            editor.putString("drlDelay", "10");
            editor.putString("interDelay", "10");
            editor.putString("ampDelay", "10");
            editor.putString("dvrDelay", "10");

            editor.apply();
        }
    }
    private void saveData(){
        editor = prefs.edit();

        drlDelay = String.valueOf(spnDrlDelay.getSelectedItemPosition()*5);
        interDelay = String.valueOf(spnIntDelay.getSelectedItemPosition()*5);
        ampDelay = String.valueOf(spnAmpDelay.getSelectedItemPosition()*5);
        dvrDelay = String.valueOf(spnDvrDelay.getSelectedItemPosition()*5);

        if(tglAmp.isChecked()){
            defAmpState = "ON";
        }else{
            defAmpState = "OFF";
        }

        if(tglDrl.isChecked()){
            defDrlState = "ON";
        }else{
            defDrlState = "OFF";
        }

        if(tglDvr.isChecked()){
            defDvrState = "ON";
        }else{
            defDvrState = "OFF";
        }

        if(tglInt.isChecked()){
            defInterState = "ON";
        }else{
            defInterState = "OFF";
        }

        editor.putString("drlState", drlState);
        editor.putString("interState", interState);
        editor.putString("ampState", ampState);
        editor.putString("dvrState", dvrState);

        editor.putString("defDrlState", defDrlState);
        editor.putString("defInterState", defInterState);
        editor.putString("defAmpState", defAmpState);
        editor.putString("defDvrState", defDvrState);

        editor.putString("drlDelay", drlDelay);
        editor.putString("interDelay", interDelay);
        editor.putString("ampDelay", ampDelay);
        editor.putString("dvrDelay", dvrDelay);

        editor.apply();
        String resp = volleyRequest.SendRequest();
        Toast.makeText(contextOfApplication,resp,Toast.LENGTH_SHORT).show();
    }
    private void buildSpinners(Spinner drl,String defDrlDelay,Spinner amp,String defAmpDelay,Spinner dvr,String defDvrDelay,Spinner inter,String defIntDelay){

        int t;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.getContextOfApplication(), R.layout.spinner_item, Config.arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        drl.setAdapter(adapter);
        t = Integer.parseInt(defDrlDelay);
        drl.setSelection(t/5);

        amp.setAdapter(adapter);
        t = Integer.parseInt(defAmpDelay);
        amp.setSelection(t/5);

        dvr.setAdapter(adapter);
        t = Integer.parseInt(defDvrDelay);
        dvr.setSelection(t/5);

        inter.setAdapter(adapter);
        t = Integer.parseInt(defIntDelay);
        inter.setSelection(t/5);
    }
    private void initializeData(){

        //region check permissions
        if(permManager.checkPermissionWifi(Manifest.permission.ACCESS_WIFI_STATE)){
            permManager.requestPermissionWifi(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if(permManager.checkPermissionWifi(Manifest.permission.INTERNET)){
            permManager.requestPermissionWifi(Manifest.permission.INTERNET);
        }
        if(permManager.checkPermissionWifi(Manifest.permission.ACCESS_NETWORK_STATE)){
            permManager.requestPermissionWifi(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(permManager.checkPermissionWifi(Manifest.permission.ACCESS_FINE_LOCATION)){
            permManager.requestPermissionWifi(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(permManager.checkPermissionWifi(Manifest.permission.ACCESS_COARSE_LOCATION)){
            permManager.requestPermissionWifi(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(permManager.checkPermissionWifi(Manifest.permission.CHANGE_WIFI_STATE)){
            permManager.requestPermissionWifi(Manifest.permission.CHANGE_WIFI_STATE);
        }
        /*
        if(!permManager.checkPermissionWifi(Manifest.permission.UPDATE_DEVICE_STATS)){
            permManager.requestPermissionWifi(Manifest.permission.UPDATE_DEVICE_STATS);
        }
        if(!permManager.checkPermissionWifi(Manifest.permission.WAKE_LOCK)){
            permManager.requestPermissionWifi(Manifest.permission.WAKE_LOCK);
        }
        */
        //endregion
        getData(prefs);
        if(fRun){
            fRun = false;
            drlState = defDrlState;
            interState = defInterState;
            ampState = defAmpState;
            dvrState = defDvrState;

            ui.update(btnDrl,"defDrlState");
            ui.update(btnAmp,"defAmpState");
            ui.update(btnDvr,"defDvrState");
            ui.update(btnInt,"defInterState");
        }

        String resp = volleyRequest.SendRequest();
        Toast.makeText(contextOfApplication,resp,Toast.LENGTH_SHORT).show();

        //region debug
        debugText = (TextView) findViewById(R.id.TXT_DEBUG);
        debugText.setText(resp);
        //endregion

        buildSpinners(spnDrlDelay,drlDelay,spnAmpDelay,ampDelay,spnDvrDelay,dvrDelay,spnIntDelay,interDelay);

        tglDrl.setChecked(defDrlState.equals("ON"));
        tglAmp.setChecked(defAmpState.equals("ON"));
        tglDvr.setChecked(defDvrState.equals("ON"));
        tglInt.setChecked(defInterState.equals("ON"));

        saveBtnLO.setVisibility(View.GONE);
        //region check wifi state

        //endregion

    }

}

