package com.example.carcontroll;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class permissionManager {
    Activity activity;
    Context context;

    public permissionManager(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public boolean checkPermissionWifi(String permission) {
        int result = ContextCompat.checkSelfPermission(context,permission);
        return result != PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionWifi(String permission) {
        requestPermissions(activity,
                new String[] { permission },
                1);
    }
}
