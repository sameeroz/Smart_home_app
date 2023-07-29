package com.smart_home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.smart_home.Model_Class.DeviceData;
import com.smart_home.Model_Class.ModelWholeRoom;
import com.smart_home.Model_Class.RoomControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class Main extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    public static Toolbar action_toolbar;
    NavigationView navigationView;
    AnimatedBottomBar animatedBottomBar;
    ImageView profilePic;
    TextView profileName;
//    String ip = "192.168.1.130";
//    static  String IP = "192.168.1.130";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().hide();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //Drawer layout code
        drawerLayout = findViewById(R.id.drawers);
        action_toolbar = findViewById(R.id.toolbar_main);
        navigationView = findViewById(R.id.navigation_views);
        animatedBottomBar = findViewById(R.id.bottom_bar);
        ConstraintLayout v = (ConstraintLayout) navigationView.getHeaderView(0);
        CardView vv = (CardView) v.getChildAt(0);
        LinearLayout ll = (LinearLayout) vv.getChildAt(0);
        profilePic = (ImageView) ll.getChildAt(0);

        //profile picture in drawer
        File internalStorageDir = getFilesDir();
        File alice = new File(internalStorageDir, "profile Picture.png");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }

        if(fos != null)
        {
            Bitmap bitmap = BitmapFactory.decodeStream(fos); //This gets the image
            profilePic.setImageBitmap(bitmap);
        }

        LinearLayout lll = (LinearLayout) ll.getChildAt(1);
        profileName =  (TextView) lll.getChildAt(0);

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

               switch (i1)
               {
                   case 0:
                       navController.navigate(R.id.navigation_room);
                       break;
                   case 1 :
                       navController.navigate(R.id.navigation_home);
                       break;
                   case 2 :
                       navController.navigate(R.id.navigation_favourite);
                       break;
                   case  3 :
                       navController.navigate(R.id.navigation_timer);
                       break;

                   default:
                       navController.navigate(R.id.navigation_home);
                       break;
               }

            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {
                System.out.println("The bar reselected");
            }
        });


        action_toolbar.setBackgroundColor(getResources().getColor(R.color.primaryBgColor));
        action_toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_color));
        action_toolbar.setTitle(getText(R.string.app_name));
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,action_toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getTitle().toString())
            {
                case "Settings":
                case "Contact us":
                    System.out.println("TODO");
                    break;
                default:
            }

            return false;
        });

        // Adding the rooms details manually without fetching from API--------------->


        // Hall room 1  Initializer
        ModelWholeRoom m = new ModelWholeRoom();
        m.roomImage = R.drawable.hallroom;
        m.roomName = "Hall room";
        m.turnOnInstructions = "turnOnHallRoom";
        m.turnOffInstructions = "turnOffHallRoom";
        m.isFavourite = true;

        m.roomDevices.put(0,new DeviceData(R.drawable.bulb,"lamp 1"
                , m.roomName, m.turnOnInstructions+"-lamp 1",m.turnOffInstructions+"-lamp 1"));

        m.roomDevices.put(1,new DeviceData(R.drawable.sock,"Socket 1"
                , m.roomName, m.turnOnInstructions+"-Socket 1",m.turnOffInstructions+"-Socket 1"));

        RoomControl.allRoomsData.add(m);

        //Living room 2

        ModelWholeRoom m1 = new  ModelWholeRoom();
        m1.roomImage = R.drawable.livingroom;
        m1.roomName = "Living room 1";
        m1.turnOnInstructions = "turnOnLivingRoom1";
        m1.turnOffInstructions = "turnOffLivingRoom1";
        m1.isFavourite = false;


        m1.roomDevices.put(0,new DeviceData(R.drawable.bulb,"lamp 1"
                , m1.roomName, m1.turnOnInstructions+"-lamp 1",m1.turnOffInstructions+"-lamp 1"));

        m1.roomDevices.put(1,new DeviceData(R.drawable.tv,"TV"
                , m1.roomName, m1.turnOnInstructions+"-tv",m1.turnOffInstructions+"-tv"));


        m1.roomDevices.put(2,new DeviceData(R.drawable.sock,"Socket 1"
        , m1.roomName, m1.turnOnInstructions+"-Socket 1",m1.turnOffInstructions+"-Socket 1"));


        RoomControl.allRoomsData.add(m1);

        // BedRoom 1

        ModelWholeRoom m2 = new  ModelWholeRoom();
        m2.roomImage = R.drawable.bedroom;
        m2.roomName = "Bed room 1";
        m2.turnOnInstructions = "turnOnBedRoom1";
        m2.turnOffInstructions = "turnOffBedRoom1";
        m2.isFavourite = false;

        m2.roomDevices.put(0,new DeviceData(R.drawable.sock,"Socket 1"
                , m2.roomName, m1.turnOnInstructions+"-Socket 1",m2.turnOffInstructions+"-Socket 1"));

        m2.roomDevices.put(1,new DeviceData(R.drawable.tv,"TV"
                , m2.roomName, m2.turnOnInstructions+"-tv",m2.turnOffInstructions+"-tv"));


        RoomControl.allRoomsData.add(m2);

         // BedRoom 2

        ModelWholeRoom m3 = new  ModelWholeRoom();
        m3.roomImage = R.drawable.bedroom;
        m3.roomName = "Bed room 2";
        m3.turnOnInstructions = "turnOnBedRoom2";
        m3.turnOffInstructions = "turnOffBedRoom2";
        m3.isFavourite = true;


        m3.roomDevices.put(0,new DeviceData(R.drawable.bulb,"lamp 1"
                , m3.roomName, m3.turnOnInstructions+"-lamp 1",m3.turnOffInstructions+"-lamp 1"));

        m3.roomDevices.put(1,new DeviceData(R.drawable.sock,"Socket 1"
                , m3.roomName, m3.turnOnInstructions+"-Socket 1",m3.turnOffInstructions+"-Socket 1"));


        m3.roomDevices.put(2,new DeviceData(R.drawable.tv,"TV"
                , m3.roomName, m3.turnOnInstructions+"-tv",m3.turnOffInstructions+"-tv"));


        RoomControl.allRoomsData.add(m3);

        // BathRoom 1

        ModelWholeRoom m4 = new  ModelWholeRoom();
        m4.roomImage = R.drawable.bathroom;
        m4.roomName = "Bath room 1";
        m4.turnOnInstructions = "turnOnBathRoom1";
        m4.turnOffInstructions = "turnOffBathRoom1";
        m4.isFavourite = true;


        m4.roomDevices.put(0,new DeviceData(R.drawable.bulb,"lamp 1"
                , m4.roomName, m4.turnOnInstructions+"-lamp 1",m4.turnOffInstructions+"-lamp 1"));

        m4.roomDevices.put(1,new DeviceData(R.drawable.sock,"Socket 1"
                , m4.roomName, m4.turnOnInstructions+"-Socket 1",m4.turnOffInstructions+"-Socket 1"));


        RoomControl.allRoomsData.add(m4);

        // BathRoom 2

        ModelWholeRoom m5 = new  ModelWholeRoom();
        m5.roomImage = R.drawable.bathroom;
        m5.roomName = "Bath room 2";
        m5.turnOnInstructions = "turnOnBathRoom2";
        m5.turnOffInstructions = "turnOffBathRoom2";
        m5.isFavourite = false;



        m5.roomDevices.put(0,new DeviceData(R.drawable.sock,"Socket 1"
                , m5.roomName, m5.turnOnInstructions+"-Socket 1",m5.turnOffInstructions+"-Socket 1"));

        m5.roomDevices.put(1,new DeviceData(R.drawable.bulb,"lamp 1"
                , m5.roomName, m5.turnOnInstructions+"-lamp 1",m5.turnOffInstructions+"-lamp 1"));


        RoomControl.allRoomsData.add(m5);

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//                });


    }

    public void profile(View view)  {

        Intent i = new Intent(getApplicationContext(), Profile.class);
        startActivity(i);
    }


    @Override
    protected void onResume() {
        super.onResume();
        File internalStorageDir = getFilesDir();
        File alice = new File(internalStorageDir, "profile Picture.png");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }

        if(fos != null)
        {
            Bitmap bitmap = BitmapFactory.decodeStream(fos);
            profilePic.setImageBitmap(bitmap);
        }

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString("name", "");
        profileName.setText(s1);
    }


    //This function was created for controlling the home locally
    // without internet using the network


//    void instructionSenter(String message)
//    {
//        boolean condition = false;
//        int counter = 0;
//
//        Socket socket = null;
//
//        try {
//            socket = new Socket(IP,81);
//        } catch (IOException ex) {
//            condition = true;
//            return;
//        }
//        if(condition)
//        {
//            try {
//                socket.close();
//            } catch (IOException ex) {
//                System.out.println("wrong ");
//            }
//            return;
//        }
//
//        System.out.println("Done Sending Message");
//
//        BufferedWriter out = null;
//        try {
//            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            out.write(message);
//            out.flush();
//            socket.close();
//        } catch (IOException ex) {
//
//        }
//
//    }


       /* send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //instructionSenter("1");
                    Socket s = null;
                    try {
                        s = new Socket(ip,81);
                    } catch (IOException ex) {
                        System.out.println("Wrong IP Address");
                        Toast.makeText(getApplicationContext(),"Wrong IP Address\nPlease write a correct one",Toast. LENGTH_SHORT).show();
                        return;
                    }
                    DataOutputStream out = null;
                    try {
                        out = new DataOutputStream(s.getOutputStream());
                    } catch (IOException ex) {
                        Toast.makeText(getApplicationContext(),"Cannot send data to arduino\nPlease write a correct one",Toast. LENGTH_SHORT).show();
                        return;
                    }
                Toast.makeText(getApplicationContext(),"\"Sending Message...\"\nPlease write a correct one",Toast. LENGTH_SHORT).show();
                    try {
                        out.writeUTF(message.getText().toString());
                        out.flush();
                    } catch (IOException ex) {
                        Toast.makeText(getApplicationContext(),"\"Sending error...\"\nPlease write a correct one",Toast. LENGTH_SHORT).show();
                        return;
                    }
                try {
                    s.close();
                    Toast.makeText(getApplicationContext(),"\"Done Sending...\"\nPlease write a correct one",Toast. LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"\"Cannot close...\"\nPlease write a correct one",Toast. LENGTH_SHORT).show();
                    return;
                }

                return;
                }
        });*/
}