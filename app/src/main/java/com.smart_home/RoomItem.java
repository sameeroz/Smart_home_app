package com.smart_home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitvale.switcher.SwitcherX;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart_home.Devices_Service_Classes.DeviceTimer1;
import com.smart_home.Devices_Service_Classes.DeviceTimer2;
import com.smart_home.Devices_Service_Classes.DeviceTimer3;
import com.smart_home.Devices_Service_Classes.DeviceTimer4;
import com.smart_home.Devices_Service_Classes.DeviceTimer5;

import Room_Service_Classes.TimerService;
import Room_Service_Classes.TimerService2;
import Room_Service_Classes.TimerService3;
import Room_Service_Classes.TimerService4;
import Room_Service_Classes.TimerService5;
import Room_Service_Classes.TimerService6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RoomItem extends AppCompatActivity {
    private final String TAG = "MAIN";
    ImageView roomImage, favourite;
    boolean isFavourite = false;
    Animation anim;
    CardView cardview, mainCard;
    TextView timerTextView;
    LinearLayout cardVerticalLayout;
    private String roomName = null;


    private final int[] imageList = {R.drawable.bulb, R.drawable.tv, R.drawable.socket};

    Double time = 0.0;
    Double time2 = 0.0;
    int hour, minutes;
    ModelWholeRoom m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_item);
        roomImage = findViewById(R.id.roomImage);
        favourite = findViewById(R.id.favourite);
        cardview = findViewById(R.id.cardview);
        cardVerticalLayout = findViewById(R.id.card_vertical_layout);
        mainCard = findViewById(R.id.mainCard);
        ProgressBar roomLoading = findViewById(R.id.room_loading);
        timerTextView = findViewById(R.id.timer_textview);
        getSupportActionBar().hide();


        roomImage.setImageDrawable(RoomControl.roomImage);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate_card_view);
        cardview.startAnimation(anim);
        mainCard.startAnimation(anim);


        if (getIntent().getExtras() != null) {
            roomName = getIntent().getStringExtra("Room Name");
        }


//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Home 1/"+ Login.userUid+"/"+roomName);
//        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//
//                    roomLoading.setVisibility(View.GONE);
//
//                    if(!task.getResult().exists())
//                    {
//                        Toast.makeText(getApplicationContext(),"لا يوجد بيانات لهذي الصفحة", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//
//                   int count = 0;
//                    for (DataSnapshot child : task.getResult().getChildren()) {
//                        System.out.println(child.getKey());
//
//                        if(child.getKey().contains("device"))
//                        {
//                            HashMap<String, Object> deviceData = (HashMap<String, Object>) child.getValue();
//
//                            Long s = (Long) deviceData.get("image");
//                            int a = Math.toIntExact(s);
//                            String name  = (String) deviceData.get("name");
//                            Boolean status = (Boolean) deviceData.get("status");
//                            cardVerticalLayout.addView(customCardView(count, a, deviceData.get("name").toString(),
//                                    roomName, "", "", status));
//
//                            count++;
//                        }
//
//                        if(child.getKey().contains("isFavourite"))
//                        {
//                            Boolean status = (Boolean) child.getValue();
//                            if(status)
//                            {
//                              isFavourite = true;
//                              favourite.setImageDrawable(getResources().getDrawable(R.drawable.heart3));
//                            }
//                              else
//                             {
//                              isFavourite = false;
//                             favourite.setImageDrawable(getResources().getDrawable(R.drawable.favourite));
//                             }
//                        }
//
//
//                    }
//
//                }
//            }
//        });


        // Displaying without using the API
        m = RoomControl.allRoomsData.get(RoomControl.roomNumber);
        for (int i = 0; i < m.roomDevices.size(); i++) {
            DeviceData deviceData = m.roomDevices.get(i);
            cardVerticalLayout.addView(customCardView(i, deviceData.imageId, deviceData.deviceName,
                    deviceData.roomName, deviceData.turnOnInstructions,
                    deviceData.turnOffInstructions, i % 2 == 0));
        }

        if (m.isFavourite) {
            favourite.setImageDrawable(getResources().getDrawable(R.drawable.heart3));
        } else {
            favourite.setImageDrawable(getResources().getDrawable(R.drawable.favourite));

        }
        roomLoading.setVisibility(View.GONE);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void favourite(View view) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (!isFavourite) {
            isFavourite = true;
            favourite.setImageDrawable(getResources().getDrawable(R.drawable.heart3));
            DatabaseReference myRef = database.getReference("Home 1/" + Login.userUid + "/" + roomName + "/isFavourite");
            myRef.setValue(isFavourite);
        } else {
            favourite.setImageDrawable(getResources().getDrawable(R.drawable.favourite));
            isFavourite = false;
            DatabaseReference myRef = database.getReference("Home 1/" + Login.userUid + "/" + roomName + "/isFavourite");
            myRef.setValue(isFavourite);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String message) {
        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String NOTIFICATION_CHANNEL_ID = "example.sameer";
        String channelName = "ervice";
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.switch_close)
                .setContentTitle("Smartek")
                .setContentText(message)
                .setAutoCancel(true)
                //.setSound(defaultSoundUri)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(channelName,
                "I loved it",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        anim = AnimationUtils.loadAnimation(this, R.anim.translate_reverse_card_view);
        Animation fadeout = new AlphaAnimation(1.f, 0.f);
        fadeout.setDuration(500);
        cardview.startAnimation(anim);
        cardview.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardview.setVisibility(View.GONE);
            }
        }, 500);


    }


    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String getTimerText2() {
        int rounded = (int) Math.round(time2);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    public void setTimer(int deviceNumber, int imageId, String deviceName, String roomName,
                         String turnOnInstructions, String turnOffInstructions, boolean status) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                RoomItem.this,
                R.style.MyTimePicker,
                (view, hourOfDay, minute) -> {

                    if (!(hourOfDay > 0 || minute > 0)) {
                        Toast.makeText(getApplicationContext(), "Please choose a time to start the timer", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    hour = hourOfDay;
                    minutes = minute;
                    time = hour * 60 * 60.0 + minutes * 60;
                    int d = deviceNumber;

                    if (!isMyServiceRunning(DeviceTimer1.class)) {
                        Toast.makeText(getApplicationContext(), "Device Timer 1 started", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomItem.this, DeviceTimer1.class);
                        intent.putExtra("time", time);
                        intent.putExtra("status", status);
                        intent.putExtra("path", "Home 1/" + Login.userUid + "/" + roomName + "/device " + (++d) + "/status");
                        toFileWriter(roomName + "\n" + deviceName + "\n" + imageId + "\n"
                                + turnOnInstructions + "\n" + turnOffInstructions, "DeviceTimer1");

                        Log.i(TAG, "StartedDevice 1");
                        startService(intent);

                    } else if (!isMyServiceRunning(DeviceTimer2.class)) {
                        Toast.makeText(getApplicationContext(), "Device Timer 2 started", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomItem.this, DeviceTimer2.class);
                        intent.putExtra("time", time);
                        intent.putExtra("status", status);
                        intent.putExtra("path", "Home 1/" + Login.userUid + "/" + roomName + "/device " + (++d) + "/status");

                        toFileWriter(roomName + "\n" + deviceName + "\n" + imageId + "\n"
                                + turnOnInstructions + "\n" + turnOffInstructions, "DeviceTimer2");

                        Log.i(TAG, "StartedDevice 2");
                        startService(intent);
                    } else if (!isMyServiceRunning(DeviceTimer3.class)) {
                        Toast.makeText(getApplicationContext(), "Device Timer 3 started", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomItem.this, DeviceTimer3.class);
                        intent.putExtra("time", time);
                        intent.putExtra("status", status);
                        intent.putExtra("path", "Home 1/" + Login.userUid + "/" + roomName + "/device " + (++d) + "/status");

                        toFileWriter(roomName + "\n" + deviceName + "\n" + imageId + "\n"
                                + turnOnInstructions + "\n" + turnOffInstructions, "DeviceTimer3");

                        Log.i(TAG, "StartedDevice 3");
                        startService(intent);
                    } else if (!isMyServiceRunning(DeviceTimer4.class)) {
                        Toast.makeText(getApplicationContext(), "Device Timer 4 started", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomItem.this, DeviceTimer4.class);
                        intent.putExtra("time", time);
                        intent.putExtra("status", status);
                        intent.putExtra("path", "Home 1/" + Login.userUid + "/" + roomName + "/device " + (++d) + "/status");

                        toFileWriter(roomName + "\n" + deviceName + "\n" + imageId + "\n"
                                + turnOnInstructions + "\n" + turnOffInstructions, "DeviceTimer4");

                        Log.i(TAG, "StartedDevice 4");
                        startService(intent);
                    } else if (!isMyServiceRunning(DeviceTimer5.class)) {
                        Toast.makeText(getApplicationContext(), "Device Timer 5 started", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomItem.this, DeviceTimer5.class);
                        intent.putExtra("time", time);
                        intent.putExtra("status", status);
                        intent.putExtra("path", "Home 1/" + Login.userUid + "/" + roomName + "/device " + (++d) + "/status");

                        toFileWriter(roomName + "\n" + deviceName + "\n" + imageId + "\n"
                                + turnOnInstructions + "\n" + turnOffInstructions, "DeviceTimer5");

                        Log.i(TAG, "StartedDevice 5");
                        startService(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "The timer is already running", Toast.LENGTH_SHORT).show();
                    }
                }, 12, 0, true
        );
        timePickerDialog.setTitle("Set Timer");
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(hour, minutes);
        timePickerDialog.show();


    }

    public void setTimer(View view) {
        if (RoomControl.roomNumber == 0) {
            if (isMyServiceRunning(TimerService.class)) {
                alertDialogShower(0, TimerService.class);
                return;
            }
        }
        if (RoomControl.roomNumber == 1) {
            if (isMyServiceRunning(TimerService2.class)) {
                alertDialogShower(1, TimerService2.class);
                return;
            }
        }
        if (RoomControl.roomNumber == 2) {
            if (isMyServiceRunning(TimerService3.class)) {
                alertDialogShower(2, TimerService3.class);
                return;
            }
        }
        if (RoomControl.roomNumber == 3) {
            if (isMyServiceRunning(TimerService4.class)) {
                alertDialogShower(3, TimerService4.class);
                return;
            }
        }
        if (RoomControl.roomNumber == 4) {
            if (isMyServiceRunning(TimerService5.class)) {
                alertDialogShower(4, TimerService5.class);
                return;
            }
        }
        if (RoomControl.roomNumber == 5) {
            if (isMyServiceRunning(TimerService6.class)) {
                alertDialogShower(5, TimerService6.class);
                return;
            }
        }


        AlertDialog alertDialog = new AlertDialog.Builder(RoomItem.this)
                .setTitle("Set Timer")
                .setMessage("Do you want to set a Timer for all the room?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                RoomItem.this,
                                R.style.MyTimePicker,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        if (!(hourOfDay > 0 || minute > 0)) {
                                            Toast.makeText(getApplicationContext(), "Please choose a time to start the timer", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        hour = hourOfDay;
                                        minutes = minute;
                                        time = hour * 60 * 60.0 + minutes * 60;

                                        if (RoomControl.roomNumber == 0) {
                                            Intent intent = new Intent(RoomItem.this, TimerService.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            Log.i(TAG, "StartedService 1");
                                            startService(intent);
                                        }
                                        if (RoomControl.roomNumber == 1) {
                                            Intent intent = new Intent(RoomItem.this, TimerService2.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            startService(intent);
                                            Log.i(TAG, "StartedService 2 ");
                                        }
                                        if (RoomControl.roomNumber == 2) {
                                            Intent intent = new Intent(RoomItem.this, TimerService3.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            startService(intent);
                                            Log.i(TAG, "StartedService 3");
                                        }
                                        if (RoomControl.roomNumber == 3) {
                                            Intent intent = new Intent(RoomItem.this, TimerService4.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            startService(intent);
                                            Log.i(TAG, "StartedService4");
                                        }
                                        if (RoomControl.roomNumber == 4) {
                                            Intent intent = new Intent(RoomItem.this, TimerService5.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            startService(intent);
                                            Log.i(TAG, "StartedService5");
                                        }
                                        if (RoomControl.roomNumber == 5) {
                                            Intent intent = new Intent(RoomItem.this, TimerService6.class);
                                            intent.putExtra("time", time);
                                            intent.putExtra("inst", m.turnOffInstructions);
                                            startService(intent);
                                            Log.i(TAG, "StartedService6");
                                        }
                                    }
                                }, 12, 0, true
                        );
                        timePickerDialog.setTitle("Set Timer");
                        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        timePickerDialog.updateTime(hour, minutes);
                        timePickerDialog.show();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.timer)
                .show();
    }


    public CardView customCardView(int first, int imageId, String deviceName, String roomName,
                                   String turnOnInstructions, String turnOffInstructions, Boolean switchStatus) {
        int cardViewHeight = 250;
        int leftRightMargin = 45;
        int imgCardWidth = 220;
        int imgTopPadding = 50;
        int imgBottomPadding = 70;
        int timerWidth = 100;
        int timerHeight = 130;
        int timerMarginRight = 230;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cardViewHeight = 180;
            leftRightMargin = 25;
            imgCardWidth = 150;
            imgTopPadding = 30;
            imgBottomPadding = 120;
            timerWidth = 70;
            timerHeight = 100;
            timerMarginRight = 160;
        }
        // Creating Card View

        CardView cardView = new CardView(this);
        cardView.setId(roomName.charAt(0) + deviceName.charAt(0));
        cardView.setRadius(30);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.device_card_bgcolor));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, cardViewHeight);
        lp.setMargins(leftRightMargin, 25, leftRightMargin, 20);

        cardView.setLayoutParams(lp);

        // Creating the Horizontal Linear Layout inside Card View
        LinearLayout inner_linearLayout = new LinearLayout(this);

        // Creating Image Circular Card View
        CardView circular_card = new CardView(this);
        LinearLayout.LayoutParams layoutPara = new LinearLayout.LayoutParams(imgCardWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutPara.setMargins(30, 21, 0, 21);
        circular_card.setLayoutParams(layoutPara);
        circular_card.setElevation(8);
        circular_card.setRadius(210);
        circular_card.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));

        //circular_card.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Create Image
        ImageView circular_img = new ImageView(this);
//        circular_img.setImageResource(imageList[imageId]);
        circular_img.setImageResource(imageId);
        LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 240);
        circular_img.setLayoutParams(imgLayout);
        circular_img.setPadding(10, imgTopPadding, 10, imgBottomPadding);

        circular_card.addView(circular_img);  // insert image into Image card
        inner_linearLayout.addView(circular_card);  // insert Image card into inner linear layout

        // Creating Vertical Linear Layout for texts
        LinearLayout text_views_vertical_layout = new LinearLayout(this);  // Create vertical layout for Text Views
        LinearLayout.LayoutParams text_views_layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        text_views_layout_params.setMargins(23, 0, 0, 0);
        text_views_vertical_layout.setOrientation(LinearLayout.VERTICAL);
        text_views_layout_params.gravity = Gravity.CENTER_VERTICAL;
        text_views_vertical_layout.setLayoutParams(text_views_layout_params);

        // Creating Device text
        TextView device = new TextView(this);
        device.setLayoutParams(text_views_layout_params);
        device.setText(deviceName);
        device.setTextSize(23);
        text_views_vertical_layout.addView(device);

        // Creating smart_home.RoomItem text
        TextView room = new TextView(this);
        room.setLayoutParams(text_views_layout_params);
        room.setText(roomName);
        room.setTextSize(10);
        text_views_vertical_layout.addView(room);
        inner_linearLayout.addView(text_views_vertical_layout);

        // Creating Timer Image View
        ImageView timer = new ImageView(this);
        LinearLayout.LayoutParams timer_param = new LinearLayout.LayoutParams(timerWidth, timerHeight);
        timer_param.setMargins(0, 0, timerMarginRight + 30, 0);
        timer_param.gravity = Gravity.CENTER_VERTICAL;
        timer.setLayoutParams(timer_param);
        timer.setImageResource(R.drawable.timer);
        timer.setForegroundGravity(Gravity.RIGHT | Gravity.CENTER);
        int finalFirst1 = first;

        //Creating the linear layout that holds the timer text view
        LinearLayout timerLinearLayout = new LinearLayout(this);  // Create vertical layout for Toggle Button
        LinearLayout.LayoutParams ee_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        timerLinearLayout.setLayoutParams(ee_params);
        timerLinearLayout.setGravity(Gravity.RIGHT | Gravity.CENTER);
        timerLinearLayout.addView(timer);


        //Creating the linear layout that holds the Toggle Button
        LinearLayout toggleButtonLinearLayout = new LinearLayout(this);  // Create vertical layout for Toggle Button
        LinearLayout.LayoutParams e_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toggleButtonLinearLayout.setLayoutParams(e_params);
        toggleButtonLinearLayout.setGravity(Gravity.RIGHT | Gravity.CENTER);
        //toggleButtonLinearLayout.addView(toggleButton);

        SwitcherX switcherX = new SwitcherX(getApplicationContext());
        LinearLayout.LayoutParams switch_param = new LinearLayout.LayoutParams(140, 80);
        switch_param.gravity = Gravity.CENTER | Gravity.RIGHT;
        switch_param.setMargins(0, 0, 15, 0);
        switcherX.setLayoutParams(switch_param);
        switcherX.setOnColor(getResources().getColor(R.color.green));
        switcherX.setOffColor(getResources().getColor(R.color.red));
        //switcherX.generateShadow();
        switcherX.animateSwitch();
        switcherX.setChecked(switchStatus, false);


        // switch onclick implementation
        // if clicked the database will be updated too

        int finalFirst = first;
//        switcherX.setOnClickListener(v -> {
//            int deviceNumber = finalFirst;
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("Home 1/" + Login.userUid + "/" + roomName + "/device " + (++deviceNumber) + "/status");
//            myRef.setValue(!switcherX.isChecked());
//        });

        timer.setOnClickListener(v -> {
            setTimer(finalFirst1, imageId, deviceName, roomName
                    , turnOnInstructions, turnOffInstructions, !switcherX.isChecked());

            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

            timer.startAnimation(animFadeIn);
        });

        // Listens to firebase realtime database if switch is clicked by another user
        // for the home the switch is changed simultaneously

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Home 1/" + Login.userUid + "/" + roomName + "/device " + (++first) + "/status");
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.getValue() != null) {
//                    Boolean b = Boolean.parseBoolean(dataSnapshot.getValue().toString());
//                    switcherX.setChecked(b, false);
//                } else {
//                    System.out.println("The switch is clicked");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        myRef.addValueEventListener(postListener);


        toggleButtonLinearLayout.addView(switcherX);
        cardView.addView(inner_linearLayout);  // insert inner linear layout into Card
        cardView.addView(toggleButtonLinearLayout); // insert toggle Button Linear Layout into Card
        cardView.addView(timerLinearLayout); // insert timer linear layout into Card
        return cardView;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double t1 = intent.getDoubleExtra("Countdown1", -1);
            double t2 = intent.getDoubleExtra("Countdown2", -1);
            double t3 = intent.getDoubleExtra("Countdown3", -1);
            double t4 = intent.getDoubleExtra("Countdown4", -1);
            double t5 = intent.getDoubleExtra("Countdown5", -1);
            double t6 = intent.getDoubleExtra("Countdown6", -1);


            if ((t1 > -1.0) && RoomControl.roomNumber == 0) {
                time = t1;
                Log.i(TAG, "CountDown1-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            } else if ((t2 > -1.0) && RoomControl.roomNumber == 1) {
                time = t2;
                Log.i(TAG, "CountDown2-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            } else if ((t3 > -1.0) && RoomControl.roomNumber == 2) {
                time = t3;
                Log.i(TAG, "CountDown3-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            } else if ((t4 > -1.0) && RoomControl.roomNumber == 3) {
                time = t4;
                Log.i(TAG, "CountDown4-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            } else if ((t5 > -1.0) && RoomControl.roomNumber == 4) {
                time = t5;
                Log.i(TAG, "CountDown5-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            } else if ((t6 > -1.0) && RoomControl.roomNumber == 5) {
                time = t6;
                Log.i(TAG, "CountDown6-  Seconds remaining " + time);
                timerTextView.setText(getTimerText());
            }
        }
    };


    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double t0 = intent.getDoubleExtra("Device1", -1);

            if ((t0 > -1.0)) {
                Log.i("sd1", "Count remaining " + t0);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver2, new IntentFilter(DeviceTimer1.Timer_Countdown));
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.Timer_Countdown));
        Log.i(TAG, "Registered  Broadcast reciever");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);
        Log.i(TAG, "Unregistered  Broadcast reciever");

    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
            unregisterReceiver(broadcastReceiver2);
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // stopService(new Intent(this,TimerService.class));
        Log.i(TAG, "Stopped Service");
        super.onDestroy();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void alertDialogShower(int index, Class<?> serviceClass) {
        AlertDialog alertDialog = new AlertDialog.Builder(RoomItem.this)
                .setTitle("Stop Timer")
                .setMessage("Do you want to cancel the Timer ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File internalStorageDir = getFilesDir();
                        File f = new File(internalStorageDir, index + ".txt");
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write("True".getBytes());
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stopService(new Intent(RoomItem.this, serviceClass));
                        timerTextView.setText("00:00:00");
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.timer)
                .show();
    }

    private void toFileWriter(String input, String fileName) {
        File internalStorageDir = getFilesDir();
        File f = new File(internalStorageDir, fileName + ".txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(input.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}