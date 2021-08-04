package com.nay.angrybirdsmemory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuestionAngryBirdActivity extends AppCompatActivity {

    // Constants
    private static final int REQUEST_CODE = 1234;

    // Items
    TextView textViewPoint;
    RelativeLayout relativeLayout;
    LinearLayout linearLayoutQuestion;
    LinearLayout linearLayoutResult;
    Button btnReplay, btnApply, btnCancel;

    // Properties
    ProgressBar progressBar;
    ImageView imageQuestion, imageQuestion2, imageQuestion3, imageQuestion4, imageResult, imageResult2, imageResult3, imageResult4;
    public static ArrayList<String> arrayListImage; // array contain image
    String[] listImage;
    static Animation animationScale;
    SharedPreferences shareTotalPoint; // save points
    String matchImage1, matchImage2, matchImage3, matchImage4;
    String nameImage, nameImage2, nameImage3, nameImage4;//name of result returned image
    int totalPoint = 100;
    public static int amountImage = 1; // Amount image what user want to remember
    public static int timeRemember = 1000; // Time to remember origin image
    public static int timeResponse = 1000; // Time to find image match with origin image
    boolean flagBtnReplay, flagCountDown;
    SeekBar seekBarAmountImage, timeRememberImage, timeResponseImage;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_angry_bird);
        flagCountDown = false;
        shareTotalPoint = getSharedPreferences("totalPoint", MODE_PRIVATE);
        // reflection item
        this.reflection();
        // set image question animation
        animationScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        // set display point
        totalPoint = shareTotalPoint.getInt("point", 100);
        amountImage = shareTotalPoint.getInt("amountImage", 1);
        textViewPoint.setText(String.valueOf(totalPoint));
        // setting and reload image when run the first time
        this.displayAmountImage(amountImage);
  /*      countDownTimer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = progressBar.getProgress();
                progress += 1;
                if (progress >= progressBar.getMax()) {
                    progress = 0;
                }
                progressBar.setProgress(progress);

            }

            @Override
            public void onFinish() {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(QuestionAngryBirdActivity.this);
                alertBuilder.setTitle("Warning about time");
                alertBuilder.setMessage("Time out to remember image!Are you ready to play this game?");
                alertBuilder.setNegativeButton("Ready,GO!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(QuestionAngryBirdActivity.this, ListResultImageActivity.class), REQUEST_CODE);
                    }
                });
                alertBuilder.setPositiveButton("Not ready!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.start();
                    }
                });
                alertBuilder.show();

            }
        };
        countDownTimer.start();*/


        // find result image
        findImageMatch();

    }


    // catch event find image match for origin image
    private void findImageMatch() {
        // move a new screen when click result image
        imageResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                startActivityForResult(new Intent(QuestionAngryBirdActivity.this, ListResultImageActivity.class), REQUEST_CODE);

            }
        });
    }

    // get Result return from another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> listImage = data.getStringArrayListExtra("nameImage");
            // setting display for result returned image
            this.displayResultImage(listImage);

            // compare result returned of image
            compareResultImage();
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED) {
            totalPoint -= 10;
            if (flagBtnReplay) {
                btnReplay.setVisibility(View.VISIBLE);
            }
            countDownTimer.start();
            Toast.makeText(this, "Require choose image🥵🤑", Toast.LENGTH_SHORT).show();
        }
        // setting value for item point
        calculationPoint();
        textViewPoint.setText(String.valueOf(totalPoint));
        super.onActivityResult(requestCode, resultCode, data);
    }

    // reflection for item
    private void reflection() {
        linearLayoutQuestion = (LinearLayout) findViewById(R.id.listImage);
        linearLayoutResult = (LinearLayout) findViewById(R.id.listImageResult);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutForScreen);
        imageQuestion = (ImageView) findViewById(R.id.imageQuestion);
        imageQuestion2 = (ImageView) findViewById(R.id.imageQuestion2);
        imageQuestion3 = (ImageView) findViewById(R.id.imageQuestion3);
        imageQuestion4 = (ImageView) findViewById(R.id.imageQuestion4);
        imageResult = (ImageView) findViewById(R.id.imageViewResult);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listImage = getResources().getStringArray(R.array.array_image);
        arrayListImage = new ArrayList<>(Arrays.asList(listImage));
        textViewPoint = (TextView) findViewById(R.id.textViewPoint);
        imageResult2 = (ImageView) findViewById(R.id.imageViewResult2);
        imageResult3 = (ImageView) findViewById(R.id.imageViewResult3);
        imageResult4 = (ImageView) findViewById(R.id.imageViewResult4);
        btnReplay = (Button) findViewById(R.id.btnReplay);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnCancel = (Button) findViewById(R.id.btnCancel);

    }


    private void calculationPoint() {
        SharedPreferences.Editor shaEditor = shareTotalPoint.edit();
        shaEditor.putInt("point", totalPoint);
        shaEditor.putInt("amountImage", amountImage);
        shaEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.amountImage:
                //not do thing
                return true;
            case R.id.reload:
                // reload the question image
                this.displayAmountImage(amountImage);
                return true;
            case R.id.setting:
                customSetting();
                return true;
            case R.id.subOneImage:
                amountImage = 1;
                this.displayAmountImage(amountImage);
                return true;
            case R.id.subTwoImage:
                amountImage = 2;
                this.displayAmountImage(amountImage);
                return true;
            case R.id.subThreeImage:
                amountImage = 3;
                this.displayAmountImage(amountImage);
                return true;
            case R.id.subFourImage:
                amountImage = 4;
                this.displayAmountImage(amountImage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // method about amount image need display by user
    // setting visibility, imageResource when reload
    private void displayAmountImage(int amountImage) {
        if (flagCountDown) {
            countDownTimer.cancel();
        }
        Collections.shuffle(arrayListImage);
        flagBtnReplay = false;
        imageResult.setImageResource(R.drawable.question_image); // set image resource
        imageResult.startAnimation(animationScale); // set animation for image result
        imageResult2.setVisibility(View.GONE);
        imageResult3.setVisibility(View.GONE);
        imageResult4.setVisibility(View.GONE);
        // display none for button replay
        btnReplay.setVisibility(View.GONE);
        btnReplay.clearAnimation();
        // restart event click for result image to choose image
        imageResult.setEnabled(true);
        if (amountImage == 1) {
            //setting attribute for sum weight of LinearLayout
            linearLayoutQuestion.setWeightSum(1);
            //setting visibility for image display
            imageQuestion.setVisibility(View.VISIBLE);
            imageQuestion2.setVisibility(View.GONE);
            imageQuestion3.setVisibility(View.GONE);
            imageQuestion4.setVisibility(View.GONE);

            // setting random image for the first question
            matchImage1 = arrayListImage.get(4); // get a position in array after mix array
            int idImageQuestion = getResources().getIdentifier(matchImage1, "drawable", getPackageName()); // get id of image
            imageQuestion.setImageResource(idImageQuestion);  // normal when call method setImageResource
        }
        if (amountImage == 2) {
            //setting attribute for sum weight of LinearLayout
            linearLayoutQuestion.setWeightSum(2);
            //setting visibility for image display
            imageQuestion.setVisibility(View.VISIBLE);
            imageQuestion2.setVisibility(View.VISIBLE);
            imageQuestion3.setVisibility(View.GONE);
            imageQuestion4.setVisibility(View.GONE);

            // setting random image for the first question
            matchImage1 = arrayListImage.get(4); // get a position in array after mix array
            matchImage2 = arrayListImage.get(5);
            // setting random image for the first question
            int idImageQuestion = getResources().getIdentifier(matchImage1, "drawable", getPackageName()); // get id of image
            imageQuestion.setImageResource(idImageQuestion);  // normal when call method setImageResource
            // setting random image for the second question
            int idImageQuestion2 = getResources().getIdentifier(matchImage2, "drawable", getPackageName()); // get id of image
            imageQuestion2.setImageResource(idImageQuestion2);  // normal when call method setImageResource
        }
        if (amountImage == 3) {
            //setting attribute for sum weight of LinearLayout
            linearLayoutQuestion.setWeightSum(3);
            //setting visibility for image display
            imageQuestion.setVisibility(View.VISIBLE);
            imageQuestion2.setVisibility(View.VISIBLE);
            imageQuestion3.setVisibility(View.VISIBLE);
            imageQuestion4.setVisibility(View.GONE);

            // setting random image for the first question
            matchImage1 = arrayListImage.get(4); // get a position in array after mix array
            matchImage2 = arrayListImage.get(5);
            matchImage3 = arrayListImage.get(6);
            // setting random image for the first question
            int idImageQuestion = getResources().getIdentifier(matchImage1, "drawable", getPackageName()); // get id of image
            imageQuestion.setImageResource(idImageQuestion);  // normal when call method setImageResource
            // setting random image for the second question
            int idImageQuestion2 = getResources().getIdentifier(matchImage2, "drawable", getPackageName()); // get id of image
            imageQuestion2.setImageResource(idImageQuestion2);  // normal when call method setImageResource
            // setting random image for the three question
            int idImageQuestion3 = getResources().getIdentifier(matchImage3, "drawable", getPackageName()); // get id of image
            imageQuestion3.setImageResource(idImageQuestion3);  // normal when call method setImageResource
        }
        if (amountImage == 4) {
            //setting attribute for sum weight of LinearLayout
            linearLayoutQuestion.setWeightSum(4);
            //setting visibility for image display
            imageQuestion.setVisibility(View.VISIBLE);
            imageQuestion2.setVisibility(View.VISIBLE);
            imageQuestion3.setVisibility(View.VISIBLE);
            imageQuestion4.setVisibility(View.VISIBLE);

            // setting random image for the first question
            matchImage1 = arrayListImage.get(4); // get a position in array after mix array
            matchImage2 = arrayListImage.get(5);
            matchImage3 = arrayListImage.get(6);
            matchImage4 = arrayListImage.get(7);
            // setting random image for the first question
            int idImageQuestion = getResources().getIdentifier(matchImage1, "drawable", getPackageName()); // get id of image
            imageQuestion.setImageResource(idImageQuestion);  // normal when call method setImageResource
            // setting random image for the second question
            int idImageQuestion2 = getResources().getIdentifier(matchImage2, "drawable", getPackageName()); // get id of image
            imageQuestion2.setImageResource(idImageQuestion2);  // normal when call method setImageResource
            // setting random image for the three question
            int idImageQuestion3 = getResources().getIdentifier(matchImage3, "drawable", getPackageName()); // get id of image
            imageQuestion3.setImageResource(idImageQuestion3);  // normal when call method setImageResource
            // setting random image for the three question
            int idImageQuestion4 = getResources().getIdentifier(matchImage4, "drawable", getPackageName()); // get id of image
            imageQuestion4.setImageResource(idImageQuestion4);  // normal when call method setImageResource
        }
        setCountDownTimer();
        calculationPoint();
    }

    // setting display for result returned of image
    private void displayResultImage(ArrayList<String> listImage) {
        btnReplay.clearAnimation();
        btnReplay.setVisibility(View.GONE);
        if ((amountImage == 1) && (listImage.size() == 1)) {
            linearLayoutResult.setWeightSum(1);
            nameImage = listImage.get(0);
            int idImage = getResources().getIdentifier(nameImage, "drawable", getPackageName());
            imageResult.setImageResource(idImage); // set image after user choose image
            imageResult2.setVisibility(View.GONE);
            imageResult3.setVisibility(View.GONE);
            imageResult4.setVisibility(View.GONE);
        }
        if ((amountImage == 2) && (listImage.size() == 2)) {
            linearLayoutResult.setWeightSum(2);
            nameImage = listImage.get(0);
            int idImage = getResources().getIdentifier(nameImage, "drawable", getPackageName());
            imageResult.setImageResource(idImage); // set image after user choose image
            //setting for first image
            nameImage2 = listImage.get(1);
            int idImage2 = getResources().getIdentifier(nameImage2, "drawable", getPackageName());
            imageResult2.setImageResource(idImage2); // set image after user choose image
            imageResult2.setVisibility(View.VISIBLE);
            imageResult3.setVisibility(View.GONE);
            imageResult4.setVisibility(View.GONE);
        }
        if ((amountImage == 3) && (listImage.size() == 3)) {
            linearLayoutResult.setWeightSum(3);
            nameImage = listImage.get(0);
            int idImage = getResources().getIdentifier(nameImage, "drawable", getPackageName());
            imageResult.setImageResource(idImage); // set image after user choose image
            //setting for first image
            nameImage2 = listImage.get(1);
            int idImage2 = getResources().getIdentifier(nameImage2, "drawable", getPackageName());
            imageResult2.setImageResource(idImage2); // set image after user choose image
            nameImage3 = listImage.get(2);
            int idImage3 = getResources().getIdentifier(nameImage3, "drawable", getPackageName());
            imageResult3.setImageResource(idImage3); // set image after user choose image
            imageResult2.setVisibility(View.VISIBLE);
            imageResult3.setVisibility(View.VISIBLE);
            imageResult4.setVisibility(View.GONE);
        }
        if ((amountImage == 4) && (listImage.size() == 4)) {
            linearLayoutResult.setWeightSum(4);
            nameImage = listImage.get(0);
            int idImage = getResources().getIdentifier(nameImage, "drawable", getPackageName());
            imageResult.setImageResource(idImage); // set image after user choose image
            //setting for first image
            nameImage2 = listImage.get(1);
            int idImage2 = getResources().getIdentifier(nameImage2, "drawable", getPackageName());
            imageResult2.setImageResource(idImage2); // set image after user choose image
            nameImage3 = listImage.get(2);
            int idImage3 = getResources().getIdentifier(nameImage3, "drawable", getPackageName());
            imageResult3.setImageResource(idImage3); // set image after user choose
            nameImage4 = listImage.get(3);
            int idImage4 = getResources().getIdentifier(nameImage4, "drawable", getPackageName());
            imageResult4.setImageResource(idImage4); // set image after user choose image
            imageResult2.setVisibility(View.VISIBLE);
            imageResult3.setVisibility(View.VISIBLE);
            imageResult4.setVisibility(View.VISIBLE);
        }

        imageResult.clearAnimation(); // only clear animation for result of the first image
    }

    // when result returned match with origin image
    private void matchImageOk() {
        totalPoint += 5; // increase 5 point
        imageResult.setEnabled(false); // when choose image ok then disable image result
        Toast.makeText(this, "Exactly!", Toast.LENGTH_SHORT).show();
        new CountDownTimer(2000, 500) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                displayAmountImage(amountImage);
            }
        }.start();
    }

    // compare result for the returned image
    private void compareResultImage() {
        boolean flagCheck = false;
        if ((amountImage == 1) && matchImage1.equals(nameImage)) {
            flagCheck = true;
            this.matchImageOk();
        }
        if ((amountImage == 2) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2)) {
            flagCheck = true;
            totalPoint += 5;
            this.matchImageOk();
        }
        if ((amountImage == 3) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2) && matchImage3.equals(nameImage3)) {
            flagCheck = true;
            totalPoint += 10;
            this.matchImageOk();
        }
        if ((amountImage == 4) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2) && matchImage3.equals(nameImage3) && matchImage4.equals(nameImage4)) {
            flagCheck = true;
            totalPoint += 15;
            this.matchImageOk();
        }

        // case not the origin image not match with result image
        if (!flagCheck) {
            this.matchImageNotOk();
        }
    }

    private void matchImageNotOk() {
        countDownTimer.start();
        totalPoint -= 5;
        imageResult.setEnabled(false);
        Toast.makeText(this, "Sorry😂! Please choose another image!", Toast.LENGTH_SHORT).show();
        flagBtnReplay = true;
        btnReplay.setVisibility(View.VISIBLE);
        btnReplay.setAnimation(animationScale);
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                startActivityForResult(new Intent(QuestionAngryBirdActivity.this, ListResultImageActivity.class), REQUEST_CODE);
            }
        });

    }

    // custom dialog to setting time and amount image
    private void customSetting() {
        Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.show();
        seekBarAmountImage = dialog.findViewById(R.id.seekBarAmountImage);
        timeRememberImage = dialog.findViewById(R.id.seekBarTimeOriginImage);
        timeResponseImage = dialog.findViewById(R.id.seekBarTimeResponseImage);

        seekBarAmountImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amountImage = ++progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        timeRememberImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Time setting remember", String.valueOf(progress));
                timeRemember = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        timeResponseImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Time setting response", String.valueOf(progress));
                timeResponse = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // method setting time to play game
    private void setCountDownTimer() {
        countDownTimer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                flagCountDown = true;
                int progress = progressBar.getProgress();
                progress += 1;
                if (progress >= progressBar.getMax()) {
                    progress = 0;
                }
                progressBar.setProgress(progress);

            }

            @Override
            public void onFinish() {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(QuestionAngryBirdActivity.this);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Warning about time");
                alertBuilder.setMessage("Time out to remember image!Are you ready to play this game?");
                alertBuilder.setNegativeButton("Ready,GO!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.cancel();
                        startActivityForResult(new Intent(QuestionAngryBirdActivity.this, ListResultImageActivity.class), REQUEST_CODE);
                    }
                });
                alertBuilder.setPositiveButton("Not ready!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.start();
                    }
                });
                alertBuilder.show();
            }
        }.start();
    }
}