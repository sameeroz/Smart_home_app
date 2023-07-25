import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.transition.AutoTransition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import com.sameer.smart_home.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    CardView hallRoom, livingRoom, bedRoom1, bedRoom2,bathRoom1, bathRoom2;
    Socket socket;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar action_toolbar;
    NavigationView navigationView;
    ImageView hallImage, livingImage, bedImage1, bedImage2, bathImage1, bathImage2;
    GridLayout gridLayout;

    String ip = "192.168.1.130";
    static  String IP = "192.168.1.130";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setExitTransition(new AutoTransition());
        }

        gridLayout = findViewById(R.id.grid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LinearLayout.LayoutParams layoutPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.MATCH_PARENT);
            layoutPara.setMargins(0,100,0,0);
            gridLayout.setLayoutParams(layoutPara);
        }

        //Card View variables

        /*hallRoom = findViewById(R.id.hallRoom);
        livingRoom = findViewById(R.id.livingRoom);
        bedRoom1 = findViewById(R.id.bedRoom1);
        bedRoom2 = findViewById(R.id.bedRoom2);
        bathRoom1 = findViewById(R.id.bathRoom1);
        bathRoom2 = findViewById(R.id.bathRoom2);

        // Card View Images

        hallImage = findViewById(R.id.hallImage);
        livingImage = findViewById(R.id.livingImage);
        bedImage1 = findViewById(R.id.bedImage1);
        bedImage2 = findViewById(R.id.bedImage2);
        bathImage1 = findViewById(R.id.bathImage1);
        bathImage2 = findViewById(R.id.bathImage2);*/

        // Card View Click listeners

        hallRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this, hallImage, "robot");
                RoomControl.roomImage = hallImage.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });

        livingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                livingImage, "robot");
                RoomControl.roomImage = livingImage.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });

        bedRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                bedImage1, "robot");
                RoomControl.roomImage = bedImage1.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });

        bedRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                bedImage2, "robot");
                RoomControl.roomImage = bedImage2.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });

        bathRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                bathImage1, "robot");
                RoomControl.roomImage = bathImage1.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });

        bathRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomItem.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                bathImage2, "robot");
                RoomControl.roomImage = bathImage2.getDrawable();
                startActivity(intent, options.toBundle());
            }
        });


        //Drawer layout code
       /* drawerLayout = findViewById(R.id.drawer);
        action_toolbar = findViewById(R.id.toolbar_main);
        navigationView = findViewById(R.id.navigation_view);*/


        getWindow().setStatusBarColor(getResources().getColor(R.color.lightgray));

        action_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        action_toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        action_toolbar.setTitle(getTitle());
        getSupportActionBar().hide();
        //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.darker_gray)));
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,action_toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString())
                {
                    case "Settings":
                        i();
                        break;
                    case "Contact us":
                        System.out.println("ccccccc");
                        break;
                    default:
                        System.out.println("fffff");
                }

                return false;
            }
        });

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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void i()
    {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 1:
                System.out.println("hi");
                return true;
            case 2:
              //  showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

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

    }

}