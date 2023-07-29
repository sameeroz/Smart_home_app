package com.smart_home.main_fragments.favourite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.smart_home.Main;
import com.smart_home.Model_Class.ModelWholeRoom;
import com.smart_home.R;
import com.smart_home.Model_Class.RoomControl;
import com.smart_home.Room;

public class FavouriteFragment extends Fragment {

    private ui.favourite.FavouriteViewModel favouriteViewModel;
    ModelWholeRoom m;
    LinearLayout favouriteGrid;
    TextView txt;
    ImageView favTryAgain;
    LinearLayout favTryAgainLayout;
    private boolean firstInitialize = false;
    private int[] imageList = {R.drawable.hallroom, R.drawable.livingroom, R.drawable.bedroom, R.drawable.bathroom};

    public static FavouriteFragment
    newInstance() {
        return new FavouriteFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel = new ViewModelProvider(this).get(ui.favourite.FavouriteViewModel.class);
        View root = inflater.inflate(R.layout.favourite_fragment, container, false);
        Main.action_toolbar.setTitle(R.string.title_favourite);

        favouriteGrid = root.findViewById(R.id.favourite_grid);
        txt = root.findViewById(R.id.txt);
        favTryAgainLayout = root.findViewById(R.id.fav_try_again_layout);
        favTryAgain = root.findViewById(R.id.fav_try_again);

//        for (int i = 0; i < RoomControl.allRoomsData.size(); i++)
//        {
//            if(RoomControl.allRoomsData.get(i).isFavourite)
//            {
//                txt.setVisibility(View.GONE);
//            }
//        }

        for (int i = 0; i < RoomControl.allRoomsData.size(); i++)
        {
            m = RoomControl.allRoomsData.get(i);
            if(m.isFavourite)
            {
                favouriteGrid.addView(addRoom(i,m));
            }
        }

        favTryAgain.setOnClickListener(v -> {
            FragmentTransaction tr = getFragmentManager().beginTransaction();
            tr.replace(R.id.nav_host_fragment, newInstance());
            tr.commit();
        });

        if(!haveNetworkConnection())
        {
            favTryAgainLayout.setVisibility(View.VISIBLE);
        }
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Home 1/"+ Login.userUid);
//        myRef.get().addOnCompleteListener(task -> {
//
//            if (!task.isSuccessful()) {
//                Log.e("firebase", "Error getting data", task.getException());
//                favTryAgainLayout.setVisibility(View.VISIBLE);
//                return;
//            }
//            else {
//
//                if(!task.getResult().exists())
//                {
//                    Toast.makeText(getContext(),"لا يوجد بيانات لهذي الصفحة", Toast.LENGTH_SHORT).show();
//                    favTryAgainLayout.setVisibility(View.VISIBLE);
//                    return;
//                }
//
//                int count = 0;
//                for (DataSnapshot child : task.getResult().getChildren()) {
//
//                    if(child.getKey().contains("Water"))
//                    {
//                        continue;
//                    }
//                    if(child.getKey().contains("WHT API"))
//                    {
//                        continue;
//                    }
//
//                    HashMap<String, Object> roomData = (HashMap<String, Object>) child.getValue();
//                    boolean isFav = (boolean) roomData.get("isFavourite");
//
//                    if(isFav)
//                    {
//                            txt.setVisibility(View.GONE);
//                            Long idLong = (Long) roomData.get("id");
//                            int id = Math.toIntExact(idLong);
//
//                            Long imageIdLong = (Long) roomData.get("imageId");
//                            int imageId = Math.toIntExact(imageIdLong);
//
//                            favouriteGrid.addView(roomCard(id,child.getKey(), imageId));
//                    }
//                }
//            }
//        });

        firstInitialize = true;
        return root;
    }




    private CardView addRoom(int index, ModelWholeRoom modelWholeRoom)
    {

        CardView cardView = new CardView(getContext());
        cardView.setRadius(30);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.login_btn_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                850,
                850);
        lp.setMargins(30,0,20,30);
        cardView.setLayoutParams(lp);
        cardView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), Room.class);
                intent.putExtra("image",modelWholeRoom.roomImage);
                RoomControl.roomImage = getResources().getDrawable(modelWholeRoom.roomImage);
                RoomControl.roomNumber = index;
                startActivity(intent);
                onDestroy();

        });

        LinearLayout.LayoutParams card_param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        //cardView.setLayoutParams(card_param);


        LinearLayout inner_linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams inner_linearLayout_param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        inner_linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams imageParameter = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.2f
        );

        LinearLayout.LayoutParams textParameter = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.8f
        );



        // Image
        ImageView image = new ImageView(getContext());
        image.setImageResource(modelWholeRoom.roomImage);

        image.setLayoutParams(imageParameter);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        inner_linearLayout.addView(image);



        // Title txt

        TextView title = new TextView(getContext());
        title.setLayoutParams(textParameter);
        title.setText(modelWholeRoom.roomName);
        //title.setTextSize(23);
        title.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.lightgray));
        title.setBackgroundColor(getResources().getColor(R.color.lightBlack));
        inner_linearLayout.addView(title);


        cardView.addView(inner_linearLayout);

        return cardView;
    }

    private CardView roomCard(int id, String roomName, int imageId)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        Rect rectangle= new Rect();
        Window window= ((Activity) getContext()).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight= rectangle.top;

        int height = displayMetrics.heightPixels + statusBarHeight;
        int width = displayMetrics.widthPixels;


        CardView cardView = new CardView(getContext());
        cardView.setRadius(30);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.login_btn_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, height / 7);
        lp.setMargins(55, 10, 55, 35);
        cardView.setRadius(60);
        cardView.setCardElevation(15);
        cardView.setLayoutParams(lp);


        // Vertical Linear Layout

        LinearLayout inner_linearLayout = new LinearLayout(getContext());
        inner_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        inner_linearLayout.setBackgroundColor(getResources().getColor(R.color.favourite_card_view_color));


        // Create Image
        ImageView img = new ImageView(getContext());
        img.setImageResource(imageList[imageId]);
        LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.6f);
        img.setLayoutParams(imgLayout);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setTransitionName("robot");

        inner_linearLayout.addView(img);  // insert Image card into inner linear layout



        // Creating Title text
        TextView titleText = new TextView(getContext());
        LinearLayout.LayoutParams txtLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.4f);
        titleText.setLayoutParams(txtLayout);
        titleText.setText(roomName);
        titleText.setTextSize(16);
        titleText.setTextColor(getResources().getColor(R.color.text_color));
        titleText.setGravity(Gravity.CENTER);
        //titleText.setBackgroundColor(getResources().getColor(R.color.lightBlack));

        inner_linearLayout.addView(titleText);

        // Creating FAvourite image
        ImageView fav = new ImageView(getContext());
        fav.setImageResource(R.drawable.heart3);
        LinearLayout.LayoutParams favLayout = new LinearLayout.LayoutParams(
                (width / 2 )  , // 800
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.4f);
        favLayout.setMargins(35, 0, 35, 0);
        fav.setLayoutParams(favLayout);


        inner_linearLayout.addView(fav);

        cardView.addView(inner_linearLayout);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Room.class);
                RoomControl.roomImage = img.getDrawable();
                RoomControl.roomNumber = id;
                intent.putExtra("smart_home.Room Name",roomName);
                startActivity(intent);
            }
        });


        return cardView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!firstInitialize)
        {
            FragmentTransaction tr = getFragmentManager().beginTransaction();
            tr.replace(R.id.nav_host_fragment, newInstance());
            tr.commit();
        }

    }
     @Override
    public void onPause() {
        super.onPause();
         firstInitialize = false;
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
}