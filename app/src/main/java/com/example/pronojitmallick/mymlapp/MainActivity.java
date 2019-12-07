package com.example.pronojitmallick.mymlapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int TEXT_RECO_REQ_CODE=100;
    private final int IMAGE_RECO_REQ_CODE=200;

    private InterstitialAd mInterstitialAd;

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-9739907103296154~1527136022");



        /*MobileAds.initialize(this, "ca-app-pub-9739907103296154~4375309570");*/

        /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    public void TextReco(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,TEXT_RECO_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==TEXT_RECO_REQ_CODE){
            if (resultCode==RESULT_OK){
                Bitmap photo = (Bitmap)data.getExtras().get("data");
                textRecognition(photo);
            }

            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Operation cancelled By User",Toast.LENGTH_LONG).show();
            }

            else {
                Toast.makeText(this,"Failed to capture Image!",Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode==IMAGE_RECO_REQ_CODE){
            if (resultCode==RESULT_OK){
                Bitmap photo = (Bitmap)data.getExtras().get("data");
                imageRecognition(photo);
            }
        }
    }

    private void imageRecognition(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);

        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                .getVisionLabelDetector();

        Task<List<FirebaseVisionLabel>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        for (FirebaseVisionLabel label: labels) {
                                            final String text = label.getLabel();
                                            String entityId = label.getEntityId();
                                            float confidence = label.getConfidence();

                                            /*Toast.makeText(MainActivity.this,"The detected image is: "+text,Toast.LENGTH_LONG).show();*/

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (!isFinishing()){
                                                        new AlertDialog.Builder(MainActivity.this)
                                                                .setTitle("Result")
                                                                .setMessage("The Recognized Image is: "+text)
                                                                .setCancelable(false)
                                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        // Whatever...
                                                                    }
                                                                }).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"Something Went Wrong!",Toast.LENGTH_LONG).show();
                                    }
                                });

        FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.5f)
                        .build();

    }

    private void textRecognition(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);

        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        Task<FirebaseVisionText> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    final String text = block.getText();

                                    /*Toast.makeText(MainActivity.this,"Recognized Text is: "+text,Toast.LENGTH_LONG).show();*/
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (!isFinishing()){
                                                new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle("Result")
                                                        .setMessage("The Recognized Text is: "+text)
                                                        .setCancelable(false)
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // Whatever...
                                                            }
                                                        }).show();
                                            }
                                        }
                                    });
                                    for (FirebaseVisionText.Line line: block.getLines()) {
                                        // ...
                                        for (FirebaseVisionText.Element element: line.getElements()) {
                                            // ...
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"FAILED TO RECOGNIZE TEXT",Toast.LENGTH_LONG).show();
                                    }
                                });
    }

    public void ImagetReco(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGE_RECO_REQ_CODE);
    }


  /*  FirebaseVisionCloudDetectorOptions options =
            new FirebaseVisionCloudDetectorOptions.Builder()
                    .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                    .setMaxResults(1)
                    .build();*/



}
