package com.sameer.arduinocontroller.Room_Service_Classes;

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

import com.sameer.arduinocontroller.Room_Receiver_Call_Classes.ReceiverCall3;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService3 extends Service {
    private String TAG = "TimerService3";
    public static final String Timer_Countdown = "com.sameer.arduinocontroller";
    Intent intent = new Intent(Timer_Countdown);
    Timer timer;
    TimerTask timerTask;
    double time = 15.0;
    Boolean isFinished = false;
    String instructions = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        time  = intent.getDoubleExtra("time",10);
        instructions = intent.getStringExtra("inst");

        Log.i(TAG,"Time is set = "+time);
        Log.i(TAG,"Instructions .. "+instructions);


        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           // startForeground(3, new Notification());
            startMyOwnForeground();
        }

        Log.i(TAG,"Started Timer.....");
        timer = new Timer();

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                Log.i(TAG, getTimerText());
                intent.putExtra("Countdown3", time);
                sendBroadcast(intent);
                time--;
                if(time == 0.0 )
                {
                    isFinished = true;
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

        if(stopChecker().equalsIgnoreCase("True"))
        {
            File internalStorageDir = getFilesDir();
            File alice = new File(internalStorageDir, "2.txt");
            try {
                alice.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fos = new FileOutputStream(alice);
                fos.write("False".getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"Timer 3 has Stopped",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isFinished)
        {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("time",time);
            broadcastIntent.putExtra("inst",instructions);
            broadcastIntent.setAction("ServiceStopped3");
            broadcastIntent.setClass(this, ReceiverCall3.class);
            this.sendBroadcast(broadcastIntent);
        }
        else
        {
            Log.i(TAG,"Timer 3 has Completely finished .....");
            Toast.makeText(getApplicationContext(),"Timer (3) has finished",Toast.LENGTH_SHORT).show();
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

        if(stopChecker().equalsIgnoreCase("True"))
        {
            File internalStorageDir = getFilesDir();
            File alice = new File(internalStorageDir, "2.txt");
            try {
                alice.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fos = new FileOutputStream(alice);
                fos.write("False".getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"Timer 3 has Stopped",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isFinished)
        {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("time",time);
            broadcastIntent.putExtra("inst",instructions);
            broadcastIntent.setAction("ServiceStopped3");
            broadcastIntent.setClass(this, ReceiverCall3.class);
            this.sendBroadcast(broadcastIntent);
        }
        else
        {
            Log.i(TAG,"Timer 3 has Completely finished .....");
            Toast.makeText(getApplicationContext(),"Timer 3 has finished",Toast.LENGTH_SHORT).show();
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

    private String stopChecker() {
        String a = "";
        File internalStorageDir = getFilesDir();
        File alice = new File(internalStorageDir, "2.txt");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

            if(fos == null)
            {
                return  "null";
            }

            while (fos.available() > 0) {
                a = a + ((char) fos.read());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
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