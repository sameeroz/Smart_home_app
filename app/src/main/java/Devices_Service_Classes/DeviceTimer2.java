package Devices_Service_Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import Devices_ReceiverCall_Classes.DeviceReceiverCall2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceTimer2 extends Service {
    private String TAG = "DeviceService2";
    public static final String Timer_Countdown = "com.sameer.arduinocontroller.ui.timer";
    Intent intent = new Intent(Timer_Countdown);
    Timer timer;
    public static TimerTask timerTask;
    double time = 10.0;
    Boolean isFinished = false;
    private String instructions ="";
    private String path ="";
    private Boolean status;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        time  = intent.getDoubleExtra("time",30);
        path = intent.getStringExtra("path");
        status = intent.getBooleanExtra("status", false);

        Log.i(TAG,"Time is set = "+time);
        Log.i(TAG,"Instructions .. "+path);

        return START_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //startForeground(1, new Notification());
            startMyOwnForeground();
        }

        Log.i(TAG,"Started Timer.....");
        timer = new Timer();

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                time--;
                Log.i(TAG, getTimerText());
                intent.putExtra("Device2", time);
                sendBroadcast(intent);

                if(time == 0.0 )
                {
                    FirebaseApp.initializeApp(getApplicationContext());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(path);
                    myRef.setValue(status);

                    dataDeleter();
                    isFinished =true;
                    cancel();
                    stopSelf();
                }

            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG,"Task has removed.....");

        if(stopChecker())
        {
            dataDeleter();
            Toast.makeText(getApplicationContext(),"Timer 2 has Stopped",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isFinished)
        {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("time",time);
            broadcastIntent.putExtra("path",path);
            broadcastIntent.setAction("DeviceTimer2");
            broadcastIntent.setClass(this, DeviceReceiverCall2.class);
            this.sendBroadcast(broadcastIntent);
        }
        else
        {
            Log.i(TAG,"Device 2 has Completely finished .....");
            Toast.makeText(getApplicationContext(),"Device 2 has finished",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Timer has Stopped.....");
        if(timerTask != null)
        {
            timerTask.cancel();
        }

        if(stopChecker())
        {
            dataDeleter();
            Toast.makeText(getApplicationContext(),"Timer 2 has Stopped",Toast.LENGTH_SHORT).show();
            return;
        }



        if(!isFinished)
        {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("time",time);
            broadcastIntent.putExtra("path",path);
            broadcastIntent.setAction("DeviveTimer2");
            broadcastIntent.setClass(this, DeviceReceiverCall2.class);
            this.sendBroadcast(broadcastIntent);
        }
        else
        {
            Log.i(TAG,"Device 2 has Completely finished .....");
            Toast.makeText(getApplicationContext(),"Device 2 has finished",Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

    private Boolean stopChecker() {
        String a = "";
        File internalStorageDir = getFilesDir();
        File alice = new File(internalStorageDir, "DeviceTimer2.txt");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
            while (fos.available() > 0 ) {
                int p = fos.read();

                if (p == (int) ('!')) {
                    return true;
                }
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void dataDeleter() {
        File internalStorageDir = getFilesDir();
        File f = new File(internalStorageDir, "DeviceTimer2.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write("".getBytes());
            System.out.println("Done Deleting");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}