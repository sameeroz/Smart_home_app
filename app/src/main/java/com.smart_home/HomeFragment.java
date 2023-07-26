package com.smart_home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.sameer.smart_home.R;
import ui.timer.TimerViewModel;
import com.scwang.wave.MultiWaveHeader;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private TimerViewModel mViewModel;

    private MultiWaveHeader waterTank;
    CardView energyCardView;
    TextView time, temperature, waterPercentage, devicesOn, humidity;
    ProgressBar loading;
    ImageView try_again, photo_tank;
    LinearLayout try_again_layout;
    private double waterTankCapacity = 0;
    String wHTAPI = null;
    Timer timer;
    TimerTask timerTask;


    int count = 0;
    private boolean firstInitialize = false;

    public static HomeFragment
    newInstance() {
        return new HomeFragment();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(Main.action_toolbar != null)
        {
            Main.action_toolbar.setTitle(R.string.title_home);
        }

        View root = inflater.inflate(R.layout.home_fragment, container, false);
        waterTank = root.findViewById(R.id.tank);
        energyCardView = root.findViewById(R.id.energy_cardview);
        time = root.findViewById(R.id.time_view);
        temperature = root.findViewById(R.id.temperature_view);
        waterPercentage = root.findViewById(R.id.waterpercent);
        photo_tank = root.findViewById(R.id.photo_tank);
        devicesOn = root.findViewById(R.id.devices_view);
        humidity = root.findViewById(R.id.humidity);
        loading = root.findViewById(R.id.loading);
        try_again = root.findViewById(R.id.try_again);
        try_again_layout = root.findViewById(R.id.try_again_layout);

        try_again.setOnClickListener(v -> {
            FragmentTransaction tr = getFragmentManager().beginTransaction();
            tr.replace(R.id.nav_host_fragment, newInstance());
            tr.commit();
        });

        if(!haveNetworkConnection())
        {
            try_again_layout.setVisibility(View.VISIBLE);
        }


        // Device On Counter Code

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Login.userUid + "/hoom");
        myRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                System.out.println("doooooooooooooone");
//                Log.e("firebase", "Error getting data/n " + Login.userUid, task.getException());
            }
            else {

                if(!task.getResult().exists())
                {
                    Toast.makeText(getContext(),"لا يوجد بيانات لهذي الصفحة", Toast.LENGTH_SHORT).show();
                    try_again_layout.setVisibility(View.VISIBLE);
                    return;
                }



                for (DataSnapshot child : task.getResult().getChildren()) {

                    if(child.getKey().contains("Water"))
                    {
                        Long s = (Long) child.getValue();
                        int a = Math.toIntExact(s);
                        waterTankCapacity = a;
                        continue;
                    }

                    if(child.getKey().contains("WHT API"))
                    {
                        String s = (String) child.getValue();
                        wHTAPI = s;
                        continue;
                    }


                    HashMap<String, Object> roomData = (HashMap<String, Object>) child.getValue();

                    for (DataSnapshot c : child.getChildren()) {

                        if(c.getKey().contains("device"))
                        {
                            HashMap<String, Object> deviceData = (HashMap<String, Object>) c.getValue();
                            Boolean status = (Boolean) deviceData.get("status");

                            if (status)
                            {
                                count++;
                            }

                        }
                    }

                }
            }
            if(count > 0)
            {
                energyCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                devicesOn.setText(Integer.toString(count));
            }

        });


        // Temperature Field



        timer = new Timer();

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {

                if(getActivity() == null) {
                    cancel();
                    return;
                }

                if(wHTAPI == null) {
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(requireContext());
                String url = wHTAPI;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {

                            loading.setVisibility(View.GONE);

                            // Display the first 500 characters of the response string.
                            int i = response.lastIndexOf("field1");
                            String waterLevel = response.substring(i+9,response.length()-4);
                            waterLevel = waterLevel.substring(0,waterLevel.indexOf("\""));

                            int ii = response.lastIndexOf("field2");
                            String humidityData = response.substring(ii+9,response.length()-4);
                            humidityData =  humidityData.substring(0,humidityData.indexOf("\""));

                            int iii = response.lastIndexOf("field3");
                            //String temperatureData = response.substring(iii+9,response.length()-4);
                            //temperatureData =  temperatureData.substring(0,temperatureData.indexOf("\""));

                            System.out.println(waterLevel);
                            System.out.println(humidityData);

                            if(waterLevel.equals("") || humidity.equals(""))
                            {
                                Toast.makeText(getContext(), "Something went wrong, \n Please refresh the app", Toast.LENGTH_LONG).show();
                                waterLevel = "0";
                                humidityData = "0";
                            }
                            else if(waterLevel.contains("\\n"))
                            {
                                waterLevel = waterLevel.substring(2);
                            }
                            else if(waterLevel.contains("\\n\\n"))
                            {
                                waterLevel = waterLevel.substring(2);
                            }

                            if(waterLevel.equals("") || humidity.equals(""))
                            {
                                //Toast.makeText(getContext(), "Something went wrong, \n Please refresh the app", Toast.LENGTH_LONG).show();
                                waterLevel = "0";
                                humidityData = "0";
                            }


                             if ( Integer.parseInt(waterLevel) > waterTankCapacity )
                            {
                                int p = (int) waterTankCapacity;
                                waterLevel = Integer.toString(p);
                            }

                            String waterLevelFinal = waterLevel;
                            String humidityFinal = humidityData;
                            getActivity().runOnUiThread(() -> {

                                if(getActivity() == null) {
                                    cancel();
                                    return;
                                }

                                if(!haveNetworkConnection())
                                {
                                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                                        tr.replace(R.id.nav_host_fragment, newInstance());
                                        tr.commit();

                                }


                                 temperature.setText(waterLevelFinal);
                                 humidity.setText(humidityFinal);
                                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                time.setText(currentDateTimeString);

                                if (waterTankCapacity  == 0)
                                {
                                    return;
                                }

                                double waterp = ( Integer.parseInt(waterLevelFinal) / waterTankCapacity ) * 100;
                                waterp = Math.round(waterp);

                                ConstraintLayout.LayoutParams layoutparams = (ConstraintLayout.LayoutParams) waterTank.getLayoutParams();
                                layoutparams.height = (int) (( Integer.parseInt(waterLevelFinal) / waterTankCapacity ) * (photo_tank.getHeight() - 115 ));

                                if(waterp >= 20.0)
                                {
                                    layoutparams.leftMargin = 18;
                                    layoutparams.rightMargin = 18;
                                }

                                else if( waterp <= 8.0 )
                                {
                                    layoutparams.leftMargin = 100;
                                    layoutparams.rightMargin = 100;
                                }
                                else if(waterp < 20.0)
                                {
                                    layoutparams.leftMargin = 55;
                                    layoutparams.rightMargin = 55;
                                }

                                waterTank.setLayoutParams(layoutparams);
                                waterPercentage.setText(Double.toString(waterp));

                            });

                        }, error -> getActivity().runOnUiThread(() -> {

                            if(getActivity() == null) {
                                cancel();
                                return;
                            }

                            temperature.setText("That didn't work!");
                        }));

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);


        firstInitialize = true;
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!firstInitialize)
        {
            FragmentTransaction tr = getFragmentManager().beginTransaction();
            tr.replace(R.id.nav_host_fragment, newInstance());
            tr.commit();
            System.out.println("eeeeeee");
        }

        System.out.println("ssssss");
    }

    @Override
    public void onPause() {
        timer.cancel();
        firstInitialize = false;
        super.onPause();
    }




    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            //System.out.println("-----> "+cm.getActiveNetworkInfo().isConnected());
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


}