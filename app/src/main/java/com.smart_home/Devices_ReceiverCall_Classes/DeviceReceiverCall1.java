package com.smart_home.Devices_ReceiverCall_Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.smart_home.Devices_Service_Classes.DeviceTimer1;

public class DeviceReceiverCall1 extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service 1 Stops", "Ohhhhhhh");
        Intent i = new Intent(context, DeviceTimer1.class);

        i.putExtra("time",intent.getDoubleExtra("time",0));
        i.putExtra("inst",intent.getStringExtra("inst"));
        //context.startService(i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }
}