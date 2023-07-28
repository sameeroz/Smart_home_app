package com.smart_home.timer;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart_home.Devices_Service_Classes.DeviceTimer1;
import com.smart_home.Devices_Service_Classes.DeviceTimer2;
import com.smart_home.Devices_Service_Classes.DeviceTimer3;
import com.smart_home.Devices_Service_Classes.DeviceTimer4;
import com.smart_home.Devices_Service_Classes.DeviceTimer5;
import com.smart_home.Main;
import com.smart_home.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFragment extends Fragment {

    private ui.timer.TimerViewModel mViewModel;
    LinearLayout linearLayout;
    TextView txt;
    Timer devTimer;
    public TimerTask timerTask;
    double time1, time2, time3, time4, time5;
    private int[] imageList = {R.drawable.bulb, R.drawable.tv,R.drawable.socket};


    public static TimerFragment
        newInstance() {
        return new TimerFragment();
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Main.action_toolbar.setTitle(R.string.title_timer);
        View root = inflater.inflate(R.layout.timer_fragment, container, false);
        linearLayout = root.findViewById(R.id.linearLayout);
        txt = root.findViewById(R.id.txt);

        //linearLayout.addView(cardView);  // insert the full card into outer linear layout

//         if (deviceFileReader("DeviceTimer1") != null)
//         {
//             txt.setVisibility(View.GONE);
//             String[] data;
//             data = deviceFileReader("DeviceTimer1");
//             linearLayout.addView(customCardView(data[0], data[1],
//                     Integer.parseInt(data[2]),DeviceTimer1.class,"DeviceTimer1",0));
//         }
//        if (deviceFileReader("DeviceTimer2") != null)
//        {
//            txt.setVisibility(View.GONE);
//            String[] data;
//            data = deviceFileReader("DeviceTimer2");
//
//            linearLayout.addView(customCardView(data[0], data[1],
//                    Integer.parseInt(data[2]), DeviceTimer2.class,"DeviceTimer2",1));
//        }
//        if (deviceFileReader("DeviceTimer3") != null)
//        {
//            txt.setVisibility(View.GONE);
//            String[] data;
//            data = deviceFileReader("DeviceTimer3");
//            linearLayout.addView(customCardView(data[0], data[1],
//                    Integer.parseInt(data[2]), DeviceTimer3.class,"DeviceTimer3",2));
//        }
//        if (deviceFileReader("DeviceTimer4") != null)
//        {
//            txt.setVisibility(View.GONE);
//            String[] data;
//            data = deviceFileReader("DeviceTimer4");
//            linearLayout.addView(customCardView(data[0], data[1],
//                    Integer.parseInt(data[2]), DeviceTimer4.class,"DeviceTimer4",3));
//        }
//        if (deviceFileReader("DeviceTimer5") != null)
//        {
//            txt.setVisibility(View.GONE);
//            String[] data;
//            data = deviceFileReader("DeviceTimer5");
//            linearLayout.addView(customCardView(data[0], data[1],
//                    Integer.parseInt(data[2]), DeviceTimer5.class,"DeviceTimer5",4));
//        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ui.timer.TimerViewModel.class);
        // TODO: Use the ViewModel
    }


    private String getTimerText(double time)
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double t1 = intent.getDoubleExtra("Device1",-1);
            double t2 = intent.getDoubleExtra("Device2",-1);
            double t3 = intent.getDoubleExtra("Device3",-1);
            double t4 = intent.getDoubleExtra("Device4",-1);
            double t5 = intent.getDoubleExtra("Device5",-1);

            if((t1 > -1.0))
            {
                Log.i("sd1", "Device 1 Seconds remaining "+t1);
                time1 = t1;
            }
            if((t2 > -1.0))
            {
                Log.i("sd1", "Device 2 Seconds remaining "+t2);
                time2 = t2;
            }
            if((t3 > -1.0))
            {
                Log.i("sd1", "Device 3 Seconds remaining "+t3);
                time3 = t3;
            }
            if((t4 > -1.0))
            {
                Log.i("sd1", "Device 4 Seconds remaining "+t4);
                time4 = t4;
            }
            if((t5 > -1.0))
            {
                Log.i("sd1", "Device 5 Seconds remaining "+t5);
                time5 = t5;
            }


        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver, new IntentFilter(DeviceTimer1.Timer_Countdown));
        Log.i("TAG", "Registered  Broadcast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
        Log.i("TAG", "Unregistered  Broadcast receiver");
    }

    @Override
    public void onStop() {
        try{
            getContext().unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e)
        {

        }
        super.onStop();

    }

    public  CardView customCardView(String roomName, String deviceName,
                                    int imageId, Class<?> serviceClass, String fileName, int noOfTimer)
    {
        int cardViewHeight = 250;
        int imgCardWidth = 240;
        int imgTopPadding  = 50;
        int imgBottomPadding = 60;
        int deviceTextSize = 20;
        int roomTextSize = 10;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cardViewHeight = 180;
            imgCardWidth = 150;
            imgTopPadding  = 30;
            imgBottomPadding = 120;
            deviceTextSize = 25;
            roomTextSize = 10;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, cardViewHeight);
        lp.setMargins(45,40,45,10);

        CardView cardView = new CardView(getActivity());  // Creating Card View
        cardView.setId(Integer.parseInt("2"));
        cardView.setCardBackgroundColor(getResources().getColor(R.color.device_card_bgcolor));
        cardView.setLayoutParams(lp);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogShower(serviceClass, fileName,timerTask);
            }
        });

        LinearLayout inner_linearLayout = new LinearLayout(getActivity());  // Creating the Linear Layout inside Card View
        CardView circular_card = new CardView(getActivity());  // Creating Card View inside linear layout (Image Card )
        LinearLayout.LayoutParams layoutPara = new LinearLayout.LayoutParams( imgCardWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutPara.setMargins(30,21,0,21);
        circular_card.setLayoutParams(layoutPara);
        circular_card.setElevation(8);
        circular_card.setRadius(210);

        ImageView circular_img = new ImageView(getActivity());  // Image inside Image Card
        circular_img.setImageResource(imageList[imageId]);
        LinearLayout.LayoutParams imglayout = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, 240);
        circular_img.setLayoutParams(imglayout);

        circular_img.setPadding(10,imgTopPadding,10,imgBottomPadding);

        circular_card.addView(circular_img);  // insert image into Image card
        inner_linearLayout.addView(circular_card);  // insert Image card into inner linear layout

        // Title Text View Design Code
        LinearLayout text_views_vertical_layout = new LinearLayout(getActivity());  // Create vertical layout for Text Views
        LinearLayout.LayoutParams text_views_layout_params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        text_views_layout_params.setMargins(15,0,30,0);
        text_views_vertical_layout.setOrientation(LinearLayout.VERTICAL);
        text_views_layout_params.gravity = Gravity.CENTER_VERTICAL;
        text_views_vertical_layout.setLayoutParams(text_views_layout_params);

        TextView device = new TextView(getActivity());
        device.setLayoutParams(text_views_layout_params);
        device.setText(deviceName);
        device.setTextSize(deviceTextSize);
        text_views_vertical_layout.addView(device);

        TextView room = new TextView(getActivity());
        room.setLayoutParams(text_views_layout_params);
        room.setText(roomName);
        room.setTextSize(roomTextSize);
        text_views_vertical_layout.addView(room);
        inner_linearLayout.addView(text_views_vertical_layout);

        // Timer Text View
        TextView timer = new TextView(getActivity());
        LinearLayout.LayoutParams timer_param = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        timer_param.setMargins(0,0,35,0);
        timer.setLayoutParams(timer_param);
        timer.setText("00:00:00");
        timer.setTextSize(21);
        devTimer = new Timer();
        timerTask = new TimerTask()
                                            {
                          @Override
                          public void run()
                          {

                              if(getActivity() == null) {
                                  cancel();
                                  return;
                              }

                              if(startTimer(noOfTimer) == 0 && !isMyServiceRunning(serviceClass))
                              {
                                  if(getActivity() != null) {
                                  cancel();
                                     // startActivity((Intent.makeRestartActivityTask(getActivity().getIntent().getComponent())));
                                      FragmentTransaction tr = getFragmentManager().beginTransaction();
                                      tr.replace(R.id.nav_host_fragment, newInstance());
                                      tr.commit();
                                  }
                              }

                              if(getActivity() != null) {
                                  getActivity().runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          timer.setText(getTimerText(startTimer(noOfTimer)));
                                      }
                                  });
                              }


                          }

                      };

        devTimer.scheduleAtFixedRate(timerTask, 0 ,1000);

        timer.setGravity(Gravity.CENTER | Gravity.RIGHT);
        //inner_linearLayout.addView(timer);

        /*//Toggle Button
        ToggleButton toggleButton = new ToggleButton(getActivity());
        LinearLayout.LayoutParams toggleButton_param = new LinearLayout.LayoutParams( 0, LinearLayout.LayoutParams.WRAP_CONTENT);
        toggleButton_param.gravity =  Gravity.CENTER_VERTICAL ;
        toggleButton_param.setMargins(0,150,0,0);
        toggleButton.setBackground(getResources().getDrawable(R.drawable.custom_checker));
        toggleButton.setTextOff("");
        toggleButton.setTextOn("");
        toggleButton.setPadding(0,100,0,0);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getContext(),Boolean.toString(isChecked),Toast.LENGTH_LONG).show();

            }
        });*/
        /*LinearLayout dd = new LinearLayout(getActivity());  // Create vertical layout for Text Views
        LinearLayout.LayoutParams dd_layout_params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dd_layout_params.setMargins(25,80,40,80);
        dd.setLayoutParams(dd_layout_params);
        dd.addView(toggleButton);
        inner_linearLayout.addView(dd);*/


        cardView.addView(inner_linearLayout);  // insert inner linear layout into Card
        cardView.addView(timer);
        return  cardView;
    }

    private double startTimer(int i) {
        double[] timerList = {time1,time2,time3,time4,time5};

        return timerList[i];
    }

    private String[] deviceFileReader(String fileName) {
        String[] a = new String[5];
        a[0] = "";
        a[1] = "";
        a[2] = "";
        File internalStorageDir = getContext().getFilesDir();
        File alice = new File(internalStorageDir, fileName+".txt");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if(fos == null)
            {
                return  null;
            }

            if(fos.available() == 0)
            {
                return null;
            }
            int counter = 0;
            while (fos.available() > 0 ) {
                int p = fos.read();

                if(p == (int)('\n'))
                {
                    counter++;
                    continue;
                }
                a[counter] = a[counter] + (char) p;

            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }

    private void alertDialogShower(Class<?> serviceClass, String fileName, TimerTask timerTask)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Stop Timer")
                .setMessage("Do you want to cancel the Timer ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        timerTask.cancel();
                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        tr.replace(R.id.nav_host_fragment, newInstance());
                        tr.commit();
                        toFileWriter(fileName);
                       getContext().stopService(new Intent(getContext(), serviceClass));
                       //getActivity().recreate();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.timer)
                .show();
    }

    private void toFileWriter(String fileName)
    {
        File internalStorageDir = getContext().getFilesDir();
        File f = new File(internalStorageDir, fileName+".txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write("!".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}