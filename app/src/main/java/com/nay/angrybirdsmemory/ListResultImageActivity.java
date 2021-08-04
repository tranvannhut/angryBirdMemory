package com.nay.angrybirdsmemory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result_image);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout = (TableLayout) findViewById(R.id.tableListImage);
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
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAmount(imageView, position);

                    }
                });

            }
            tableLayout.addView(tableRow); // add each image into column
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

    ;
}