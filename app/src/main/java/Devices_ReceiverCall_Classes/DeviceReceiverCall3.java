package Devices_ReceiverCall_Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import Devices_Service_Classes.DeviceTimer3;

public class DeviceReceiverCall3 extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service 3 Stops", "Ohhhhhhh");
        Intent i = new Intent(context, DeviceTimer3.class);

        i.putExtra("time",intent.getDoubleExtra("time",0));
        i.putExtra("inst",intent.getStringExtra("inst"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }
}