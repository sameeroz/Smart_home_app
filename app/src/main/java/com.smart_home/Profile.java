package com.smart_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Profile extends AppCompatActivity {

    Button choosePic;
    ImageView  profilePicture;
    EditText name;

    int SELECT_PICTURE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        choosePic = findViewById(R.id.choosePic);
        profilePicture = findViewById(R.id.pic);
        name = findViewById(R.id.name);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString("name", "");
        name.setText(s1);

        choosePic.setOnClickListener( e -> {

            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        });



        File internalStorageDir = getFilesDir();
        File alice = new File(internalStorageDir, "profile Picture.png");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(alice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(fos != null)
        {
            Bitmap bitmap = BitmapFactory.decodeStream(fos); //This gets the image
            profilePicture.setImageBitmap(bitmap);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    profilePicture.setImageURI(selectedImageUri);

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) profilePicture.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    FileOutputStream fileOutputStream = null;

                    File internalStorageDir = getFilesDir();
                    File f = new File(internalStorageDir, "profile Picture.png");

                    try {
                        fileOutputStream = new FileOutputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name", name.getText().toString());
        myEdit.commit();
    }
}