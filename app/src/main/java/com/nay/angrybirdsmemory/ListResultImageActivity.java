package com.nay.angrybirdsmemory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Collections;

public class ListResultImageActivity extends Activity {

    // Properties
    TableLayout tableLayout;
    public ArrayList<String> listNameImage;
    SharedPreferences shareAmountImage; // save points
    public int countImage = 1;
    public int amountImage;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result_image);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout =  findViewById(R.id.tableListImage);
        shareAmountImage = getSharedPreferences("totalPoint", MODE_PRIVATE);
        amountImage = shareAmountImage.getInt("amountImage", 1);


        int row = 8;
        int column = 3;
        Collections.shuffle(QuestionAngryBirdActivity.arrayListImage);
        for (int i = 1; i <= row; i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 1; j <= column; j++) {
                ImageView imageView = new ImageView(this);
                // Converts dp into its equivalent px
                float dip = 130f;
                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dip,
                        r.getDisplayMetrics()
                );
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(px, px);
                imageView.setLayoutParams(layoutParams);
                int position = column * (i - 1) + j - 1; // get order  image in list image
                int idImage = getResources().getIdentifier(QuestionAngryBirdActivity.arrayListImage.get(position), "drawable", getPackageName());
                imageView.setImageResource(idImage);
                tableRow.addView(imageView); //add each image into row
                listNameImage = new ArrayList<>(); // Initial array
                // get event id of image
                imageView.setOnClickListener(v -> checkAmount(imageView, position));

            }
            tableLayout.addView(tableRow); // add each image into column
            setTimeResponseImage();
        }

    }

    private void checkAmount(ImageView imageView, int position) {
        if (countImage == 1) {
            imageView.setBackgroundResource(R.drawable.number_one);
        } else if (countImage == 2) {
            imageView.setBackgroundResource(R.drawable.number_two);
        } else if (countImage == 3) {
            imageView.setBackgroundResource(R.drawable.number_three);
        } else if (countImage == 4) {
            imageView.setBackgroundResource(R.drawable.number_four);
        }


        if (countImage == amountImage) {
            countDownTimer.cancel();
            imageView.setEnabled(false);
            imageView.setImageAlpha(70);
            listNameImage.add(QuestionAngryBirdActivity.arrayListImage.get((position)));
            Intent intent = new Intent();
            intent.putExtra("nameImage", listNameImage);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            countImage++;
            imageView.setEnabled(false);
            imageView.setImageAlpha(70);
            listNameImage.add(QuestionAngryBirdActivity.arrayListImage.get((position)));
        }
    }

    private void setTimeResponseImage() {
        int timeResponse = QuestionAngryBirdActivity.timeResponse;
        countDownTimer = new CountDownTimer(timeResponse, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //out of time to response image
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }.start();

    }
}