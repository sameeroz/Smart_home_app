import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sameer.smart_home.R;

public class Login extends AppCompatActivity {

    private EditText username, password;
    private Button login;
    private ProgressBar loading;
    private FirebaseAuth mAuth;
    public static  String userUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        // Initialization variables

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        loading = findViewById(R.id.loading);

        // Login OnClick controller

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Login.this, Main.class);
                //startActivity(i);
                userUid = "ov72QHDqY4TeF8XmftCFPz37fcq1";
                /*if(true)
                {
                    return;
                }*/

                if(username.getText().length() == 0 || password.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Please fill in the blanks", Toast.LENGTH_SHORT).show();
                    return;
                }
                login.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);


                // Initialize Firebase Auth
                mAuth = FirebaseAuth.getInstance();


                mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                        .addOnCompleteListener( Login.this, (OnCompleteListener<AuthResult>) task -> {
                            loading.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);

                               userUid = user.getUid();
                                Intent intent = new Intent(Login.this, Main.class);
                                startActivity(intent);
                            }
                            else {
                                login.setVisibility(View.VISIBLE);
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.\n\nPlease try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });



    }
}
