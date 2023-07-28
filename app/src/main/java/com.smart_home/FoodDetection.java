package com.smart_home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smart_home.DatabaseHandler.DatabaseHandler;
import com.smart_home.DatabaseHandler.Recipe;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FoodDetection extends AppCompatActivity implements View.OnClickListener{


    ListView listView;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    ImageView inputImageView;
    TextView tvPlaceholder;
    ImageView imgSampleOne, imgSampleTwo, imgSampleThree;
    Button captureImageFab,foodConfirmation;
    List<DetectionResult> detectionResults = new ArrayList<>();
    List<String> foodIngredients;
    int time = 3;


    private static final int pic_id = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_detection);

        inputImageView = findViewById(R.id.imageView);
        tvPlaceholder = findViewById(R.id.tvPlaceholder);
        imgSampleOne = findViewById(R.id.imgSampleOne);
        imgSampleTwo = findViewById(R.id.imgSampleTwo);
        imgSampleThree = findViewById(R.id.imgSampleThree);
        captureImageFab = findViewById(R.id.captureImageFab);
        foodConfirmation = findViewById(R.id.confirm_food);
        progressBar = findViewById(R.id.progressBar);
        listView=(ListView)findViewById(R.id.listView);
        frameLayout = findViewById(R.id.frame);

        captureImageFab.setOnClickListener(this);
        imgSampleOne.setOnClickListener(this);
        imgSampleTwo.setOnClickListener(this);
        imgSampleThree.setOnClickListener(this);
        foodConfirmation.setOnClickListener(this);



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch ((v.getId()))
        {
            case R.id.captureImageFab:
                dispatchTakePictureIntent();
                break;
            case R.id.imgSampleOne:
                setViewAndDetect(getSampleImage(R.drawable.a));
                break;
            case R.id.imgSampleTwo:
                setViewAndDetect(getSampleImage(R.drawable.b));
                break;

            case R.id.imgSampleThree:
                setViewAndDetect(getSampleImage(R.drawable.cc));
                break;
            case R.id.confirm_food:
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                DatabaseHandler d = new DatabaseHandler(this);
                List<Recipe> resultList = d.getSpecifiedRecipies(foodIngredients);

                time = 3;
                Timer timer = new Timer();
                TimerTask timerTask  = new TimerTask() {
                    @Override
                    public void run() {
                        if(time == 0)
                        {
                            if(resultList == null)
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Toast.makeText(FoodDetection.this,"No Recepies with this Ingredients..",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        listView.setVisibility(View.VISIBLE);
                                        foodConfirmation.setVisibility(View.INVISIBLE);
                                        frameLayout.setVisibility(View.INVISIBLE);
                                        List<String> l = new ArrayList<>();
                                        for (Recipe r :resultList) {
                                            System.out.println(r.getRecipeName());
                                            l.add(r.getRecipeName());
                                        }

                                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                                android.R.layout.simple_list_item_1, android.R.id.text1, l);
                                        listView.setAdapter(adapter);                                    }
                                });

                            }
                            cancel();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        time--;
                    }
                };
                timer.schedule(timerTask,0,1000);
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    private  void setViewAndDetect(Bitmap bitmap) {
        frameLayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        // Display capture image
        inputImageView.setImageBitmap(bitmap);
        tvPlaceholder.setVisibility(View.INVISIBLE);

        // Run ODT and display result
        // Note that we run this in the background thread to avoid blocking the app UI because
        // TFLite object detection is a synchronised process.

        try {
            runObjectDetection(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        foodConfirmation.setVisibility(View.VISIBLE);

    }

    private Bitmap getSampleImage(int drawable){
        return BitmapFactory.decodeResource( getResources(),drawable, new BitmapFactory.Options());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id && resultCode == Activity.RESULT_OK)
        {
           // setViewAndDetect(getCapturedImage());
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            setViewAndDetect(photo);
        }
    }

    private void runObjectDetection( Bitmap bitmap) throws IOException {
        //TODO: Add object detection code here

        // Step 1: create TFLite's TensorImage object
        TensorImage image = TensorImage.fromBitmap(bitmap);

        // Step 2: Initialize the detector object
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.5f)
                .build();

        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(
                this, // the application context
                "fruit.tflite", // must be same as the filename in assets folder
                options
        );

        // Step 3: feed given image to the model and print the detection result
        List<Detection> results = detector.detect(image);
        System.out.println(results.size());



        // Step 4: Parse the detection result and show it
        foodIngredients = new ArrayList<>();
        debugPrint(results);



        for (Detection result : results)
        {
            List<Category> category = result.getCategories();
            Category c = category.get(0);

            DetectionResult detectionResult = new DetectionResult(result.getBoundingBox(),
                    c.getLabel()+"---"+c.getScore()*100);
            detectionResults.add(detectionResult);
        }


        // Draw the detection result on the bitmap and show it.
        Bitmap imgWithResult = drawDetectionResult(bitmap, detectionResults);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputImageView.setImageBitmap(imgWithResult);
            }
        });
    }

    private void debugPrint( List<Detection> results) {
        for(int i = 0; i < results.size(); i++)
        {
            Detection detection= results.get(i);
            RectF rectF = detection.getBoundingBox();
            System.out.println("Detected object: "+i);
            System.out.println("boundingBox : "+rectF.top + " "+ rectF.bottom+" "+ rectF.left+rectF.right);

            List<Category> category = detection.getCategories();
            for(int j =0 ; j < category.size(); j++)
            {
                Category c = category.get(j);
                if(!foodIngredients.contains(c.getLabel()))
                {
                    foodIngredients.add(c.getLabel());
                }

                System.out.print("Label is : "+c.getLabel()+" --- Score is :");
                float confidence = c.getScore();
                System.out.println(confidence * 100f);
            }
        }
    }


    private Bitmap drawDetectionResult(Bitmap bitmap,
                                       List<DetectionResult> detectionResults)
    {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        for (DetectionResult result : detectionResults)
        {
            // draw bounding box
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            RectF box = result.boundingBox;
            canvas.drawRect(box, pen);

            Rect tagSize = new  Rect(0, 0, 0, 0);

            // calculate the right font size
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);

            pen.setTextSize(96F);
            pen.getTextBounds(result.text, 0, result.text.length(), tagSize);
            Float fontSize = pen.getTextSize() * box.width() / tagSize.width();

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.getTextSize())
            {
                pen.setTextSize(fontSize);
            }

            Float margin = (box.width() - tagSize.width()) / 2.0F;

            if (margin < 0F) {
                margin = 0F;
            }
            canvas.drawText(
                    result.text, box.left + margin,
                    box.top + tagSize.height(), pen);
        }
        this.detectionResults = new ArrayList<>();
        return outputBitmap;
    }

    private void alertDialogShower()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(FoodDetection.this)
                .setTitle("Stop Timer")
                .setMessage("Confirm searching for the identified objects ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.timer)
                .show();
    }
}

class DetectionResult
{
    RectF boundingBox;
    String text;

    public DetectionResult(RectF boundingBox, String text) {
        this.boundingBox = boundingBox;
        this.text = text;
    }
}

//    DatabaseHandler db = new DatabaseHandler(getContext());
//
//    db.addRecipe(new Recipe(1,"Lazanya","Tomatao, potato, cheese, pasta","boil the water and cut the cheese"));
//
//    List<Recipe> recipes = db.getAllRecipes();
//
//        for (Recipe r : recipes) {
//                System.out.println(r.getRecipeName());
//                System.out.println(r.getRecipeIngredients());
//                System.out.println(r.getRecipePreparation());
//                }