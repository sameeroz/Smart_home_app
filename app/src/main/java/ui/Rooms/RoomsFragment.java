package ui.Rooms;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.transition.AutoTransition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import Login;
import Main;
import com.sameer.smart_home.R;
import RoomItem;
import RoomControl;

import java.util.HashMap;

public class RoomsFragment extends Fragment {

    private RoomsViewModel homeViewModel;

    GridLayout gridLayout;
    ProgressBar rooms_loadingbar;
    ImageView roomsTryAgain;
    LinearLayout roomsTryAgainLayout;

    public static RoomsFragment
    newInstance() {
        return new RoomsFragment();
    }

    private int[] imageList = {R.drawable.hallroom, R.drawable.livingroom, R.drawable.bedroom, R.drawable.bathroom};

    String ip = "192.168.1.130";
    static  String IP = "192.168.1.130";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(RoomsViewModel.class);
        View root = inflater.inflate(R.layout.rooms_fragment, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        if(Main.action_toolbar != null)
        {

            Main.action_toolbar.setTitle(R.string.title_room);
        }


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setExitTransition(new AutoTransition());
        }

        gridLayout = root.findViewById(R.id.grid);
        rooms_loadingbar = root.findViewById(R.id.rooms_loadingbar);
        roomsTryAgainLayout = root.findViewById(R.id.rooms_try_again_layout);
        roomsTryAgain = root.findViewById(R.id.rooms_try_again);

        roomsTryAgain.setOnClickListener(v -> {
            FragmentTransaction tr = getFragmentManager().beginTransaction();
            tr.replace(R.id.nav_host_fragment, newInstance());
            tr.commit();
        });

        if(!haveNetworkConnection())
        {
            roomsTryAgainLayout.setVisibility(View.VISIBLE);
        }



        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("Home 1/"+ Login.userUid);
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {



                    if(!task.getResult().exists())
                    {
                        Toast.makeText(getContext(),"لا يوجد بيانات لهذي الصفحة", Toast.LENGTH_SHORT).show();
                        roomsTryAgainLayout.setVisibility(View.VISIBLE);
                        return;
                    }

                   // System.out.println("childrens  ----> "+ String.valueOf(task.getResult().getChildrenCount()));


                    int count = 0;
                    for (DataSnapshot child : task.getResult().getChildren()) {
                        if(child.getKey().contains("Water"))
                        {
                            continue;
                        }
                        if(child.getKey().contains("WHT API"))
                        {
                            continue;
                        }

                        HashMap<String, Object> deviceData = (HashMap<String, Object>) child.getValue();

                        // room id
                        Long roomIdLong = (Long) deviceData.get("id");
                        int roomId = Math.toIntExact(roomIdLong);

                        //room image
                        Long roomImageLong = (Long) deviceData.get("imageId");
                        int roomImage = Math.toIntExact(roomImageLong);


                        gridLayout.addView(roomCard(roomId,child.getKey(), roomImage));

                    }
                    rooms_loadingbar.setVisibility(View.GONE);

                }
            }
        });

        // Card View Click listeners

       /* hallRoom.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(), hallImage, "robot");
                RoomControl.roomImage = hallImage.getDrawable();
                RoomControl.roomNumber = 0;
                startActivity(intent, options.toBundle());
            }
        });

        livingRoom.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                livingImage, "robot");
                RoomControl.roomImage = livingImage.getDrawable();
                RoomControl.roomNumber = 1;
                startActivity(intent, options.toBundle());
            }
        });

        bedRoom1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                bedImage1, "robot");
                RoomControl.roomImage = bedImage1.getDrawable();
                RoomControl.roomNumber = 2;
                startActivity(intent, options.toBundle());
            }
        });

        bedRoom2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                bedImage2, "robot");
                RoomControl.roomImage = bedImage2.getDrawable();
                RoomControl.roomNumber = 3;
                startActivity(intent, options.toBundle());
            }
        });

        bathRoom1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                bathImage1, "robot");
                RoomControl.roomImage = bathImage1.getDrawable();
                RoomControl.roomNumber = 4;
                startActivity(intent, options.toBundle());
            }
        });

        bathRoom2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                bathImage2, "robot");
                RoomControl.roomImage = bathImage2.getDrawable();
                RoomControl.roomNumber = 5;
                startActivity(intent, options.toBundle());
            }
        });*/


        return root;
    }

    private CardView roomCard(int id, String roomName, int imageId)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        Rect rectgle= new Rect();
        Window window= ((Activity) getContext()).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int statusBarHeight= rectgle.top;


        String deviceMan = android.os.Build.MANUFACTURER;

        if (!deviceMan.equalsIgnoreCase("Xiaomi"))
        {
            statusBarHeight = 0;
        }

        int height = displayMetrics.heightPixels + statusBarHeight;
        int width = displayMetrics.widthPixels;

        CardView cardView = new CardView(getContext());
        cardView.setRadius(30);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.login_btn_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( (width / 2 ) - 80, (height / 3 ) - 200);
        lp.setMargins(25, 10, 25, 35);
        cardView.setRadius(60);
        cardView.setCardElevation(15);
        cardView.setLayoutParams(lp);


        // Vertical Linear Layout

        LinearLayout inner_linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.MATCH_PARENT);
        inner_linearLayout.setOrientation(LinearLayout.VERTICAL);


        // Create Image
        ImageView img = new ImageView(getContext());
        img.setImageResource(imageList[imageId]);
        LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.2f);
        img.setLayoutParams(imgLayout);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setTransitionName("robot");

        inner_linearLayout.addView(img);  // insert Image card into inner linear layout



        // Creating Device text
        TextView titleText = new TextView(getContext());
        LinearLayout.LayoutParams txtLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.8f);
        titleText.setLayoutParams(txtLayout);
        titleText.setText(roomName);
        titleText.setTextSize(16);
        titleText.setTextColor(getResources().getColor(R.color.text_color));
        titleText.setGravity(Gravity.CENTER);

        inner_linearLayout.addView(titleText);
        cardView.addView(inner_linearLayout);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(), img, "robot");
                RoomControl.roomImage = img.getDrawable();
                RoomControl.roomNumber = id;
                intent.putExtra("RoomItem Name",roomName);
                startActivity(intent, options.toBundle());
            }
        });


        return cardView;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


/*
    void instructionSenter(String message)
    {
        boolean condition = false;
        int counter = 0;

        Socket socket = null;

        try {
            socket = new Socket(IP,81);
        } catch (IOException ex) {
            condition = true;
            return;
        }
        if(condition)
        {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("wrong ");
            }
            return;
        }

        System.out.println("Done Sending Message");

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(message);
            out.flush();
            socket.close();
        } catch (IOException ex) {

        }

    }*/
}