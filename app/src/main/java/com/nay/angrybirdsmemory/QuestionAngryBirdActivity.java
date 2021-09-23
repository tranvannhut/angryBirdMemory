package com.nay.angrybirdsmemory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
    public static int amountImage = 0; // Amount image what user want to remember
    public static int timeRemember = 10000; // Time to remember origin image
    public static int timeResponse = 10000; // Time to find image match with origin image
    public MediaPlayer mediaPlayerFail;
    public MediaPlayer mediaPlayerCongratulation;


    // Items
    TextView textViewPoint;
    RelativeLayout relativeLayout;
    LinearLayout linearLayoutQuestion;
    LinearLayout linearLayoutResult;
    Button btnReplay, btnApply, btnCancel;
    TextView informAmountImage, txtTimeRememberImage, txtTimeResponseImage;
    MediaPlayer mediaPlayer;

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
    boolean flagBtnReplay, flagCountDown;
    SeekBar seekBarAmountImage, timeRememberImage, timeResponseImage;
    CountDownTimer countDownTimer;
    boolean isTurnSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_angry_bird);
        isTurnSound = true;
        flagCountDown = false;
        shareTotalPoint = getSharedPreferences("totalPoint", MODE_PRIVATE);
        // reflection item
        this.reflection();
        // set image question animation
        animationScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        // set display point
        totalPoint = shareTotalPoint.getInt("point", 100);
        amountImage = shareTotalPoint.getInt("amountImage", 1);
        timeRemember = shareTotalPoint.getInt("timeRemember", 10000);
        timeResponse = shareTotalPoint.getInt("timeResponse", 10000);
        textViewPoint.setText(String.valueOf(totalPoint));
        // setting and reload image when run the first time

        // start music when user play
        Log.d("Start", "App start first");
        playMedia();

        this.displayAmountImage(amountImage);

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
            if (isTurnSound) {
                if (mediaPlayerFail == null && (!isFinishing())) {
                    mediaPlayerFail = MediaPlayer.create(this, R.raw.fail);
                }

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(1000);
                }
                mediaPlayerFail.start();

                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (mediaPlayerFail != null && mediaPlayerFail.isPlaying()) {
                            mediaPlayerFail.stop();
                            mediaPlayerFail.release();
                            mediaPlayerFail = null;
                            mediaPlayerCongratulation = null;
                        }
                        if (flagBtnReplay) {
                            btnReplay.setVisibility(View.VISIBLE);
                        }
                        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                    }
                }.start();
            } else {
                new CountDownTimer(1200, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (flagBtnReplay) {
                            btnReplay.setVisibility(View.VISIBLE);
                        }
                    }
                }.start();
            }
            countDownTimer.start();
            Toast.makeText(this, "Require choose image🥵🤑", Toast.LENGTH_SHORT).show();

        }
        // setting value for item point
        progressBar.setProgress(0);
        calculationPoint();
        textViewPoint.setText(String.valueOf(totalPoint));
        super.onActivityResult(requestCode, resultCode, data);
    }

    // reflection for item
    private void reflection() {
        linearLayoutQuestion = findViewById(R.id.listImage);
        linearLayoutResult = findViewById(R.id.listImageResult);
        relativeLayout = findViewById(R.id.layoutForScreen);
        imageQuestion = findViewById(R.id.imageQuestion);
        imageQuestion2 = findViewById(R.id.imageQuestion2);
        imageQuestion3 = findViewById(R.id.imageQuestion3);
        imageQuestion4 = findViewById(R.id.imageQuestion4);
        imageResult = findViewById(R.id.imageViewResult);
        progressBar = findViewById(R.id.progressBar);
        listImage = getResources().getStringArray(R.array.array_image);
        arrayListImage = new ArrayList<>(Arrays.asList(listImage));
        textViewPoint = findViewById(R.id.textViewPoint);
        imageResult2 = findViewById(R.id.imageViewResult2);
        imageResult3 = findViewById(R.id.imageViewResult3);
        imageResult4 = findViewById(R.id.imageViewResult4);
        btnReplay = findViewById(R.id.btnReplay);
    }


    private void calculationPoint() {
        SharedPreferences.Editor shaEditor = shareTotalPoint.edit();
        shaEditor.putInt("point", totalPoint);
        shaEditor.putInt("amountImage", amountImage);
        shaEditor.putInt("timeResponse", timeResponse);
        shaEditor.putInt("timeRemember", timeRemember);
        shaEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_setting, menu);
        return true;
    }

    // when user choose optionMenu setting
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                // reload the question image
                this.displayAmountImage(amountImage);
                return true;
            case R.id.sound:
                isTurnSound = !isTurnSound;
                if (!isTurnSound) {
                    item.setIcon(R.drawable.soundon);
                } else {
                    item.setIcon(R.drawable.soundoff);
                }
                this.turnSound();
                return true;
            case R.id.setting:
                customSetting();
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
        if (amountImage == 0) {
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
        if (amountImage == 1) {
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
        if (amountImage == 2) {
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
        if (amountImage == 3) {
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
        if ((amountImage == 0) && (listImage.size() == 1)) {
            linearLayoutResult.setWeightSum(1);
            nameImage = listImage.get(0);
            int idImage = getResources().getIdentifier(nameImage, "drawable", getPackageName());
            imageResult.setImageResource(idImage); // set image after user choose image
            imageResult2.setVisibility(View.GONE);
            imageResult3.setVisibility(View.GONE);
            imageResult4.setVisibility(View.GONE);
        }
        if ((amountImage == 1) && (listImage.size() == 2)) {
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
        if ((amountImage == 2) && (listImage.size() == 3)) {
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
        if ((amountImage == 3) && (listImage.size() == 4)) {
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
        if (isTurnSound) {
            mediaPlayerCongratulation = MediaPlayer.create(this, R.raw.congratulation);
            if (mediaPlayerCongratulation == null && (!isFinishing())) {
                mediaPlayerCongratulation = MediaPlayer.create(this, R.raw.congratulation);
            }
            mediaPlayerCongratulation.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayerCongratulation.start();
                }
            });
            new CountDownTimer(3000, 1500) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (mediaPlayerCongratulation != null && mediaPlayerCongratulation.isPlaying()) {
                        mediaPlayerCongratulation.stop();
                        mediaPlayerCongratulation.release();
                        mediaPlayerCongratulation = null;
                        mediaPlayerFail = null;
                    }
                    // continue play music
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    displayAmountImage(amountImage);
                }
            }.start();
        } else {
            new CountDownTimer(1500, 1500) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Log.d("Exist activity", "Media not start");
                    displayAmountImage(amountImage);
                }
            }.start();
        }
    }

    // compare result for the returned image
    private void compareResultImage() {
        boolean flagCheck = false;
        if ((amountImage == 0) && matchImage1.equals(nameImage)) {
            flagCheck = true;
            this.matchImageOk();
        }
        if ((amountImage == 1) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2)) {
            flagCheck = true;
            totalPoint += 5;
            this.matchImageOk();
        }
        if ((amountImage == 2) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2) && matchImage3.equals(nameImage3)) {
            flagCheck = true;
            totalPoint += 10;
            this.matchImageOk();
        }
        if ((amountImage == 3) && matchImage1.equals(nameImage) && matchImage2.equals(nameImage2) && matchImage3.equals(nameImage3) && matchImage4.equals(nameImage4)) {
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
        if (isTurnSound) {
            if (mediaPlayerFail == null && (!isFinishing())) {
                mediaPlayerFail = MediaPlayer.create(this, R.raw.fail);
            }
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(1000);
            }
            mediaPlayerFail.start();

            new CountDownTimer(2000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (mediaPlayerFail != null && mediaPlayerFail.isPlaying()) {
                        mediaPlayerFail.stop();
                        mediaPlayerFail.release();
                        mediaPlayerFail = null;
                        mediaPlayerCongratulation = null;
                    }
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    btnReplay.setVisibility(View.VISIBLE);
                    btnReplay.setAnimation(animationScale);
                }
            }.start();
        } else {
            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    btnReplay.setVisibility(View.VISIBLE);
                    btnReplay.setAnimation(animationScale);
                }
            }.start();
        }
        countDownTimer.start();
        totalPoint -= 5;
        imageResult.setEnabled(false);
        Toast.makeText(this, "Sorry😂! Please choose another image!", Toast.LENGTH_SHORT).show();
        flagBtnReplay = true;
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
        countDownTimer.cancel();
        int savedTimeRemember = timeRemember;  // saved temporary about time remember origin image
        int savedTimeResponse = timeResponse;  // saved temporary about time response image
        int savedAmountImage = amountImage;    // saved temporary about amount image to display

        Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.show();
        informAmountImage = dialog.findViewById(R.id.informAmountImage);
        txtTimeRememberImage = dialog.findViewById(R.id.informTimeRememberOrigin);
        txtTimeResponseImage = dialog.findViewById(R.id.informTimeResponseImage);
        seekBarAmountImage = dialog.findViewById(R.id.seekBarAmountImage);
        timeRememberImage = dialog.findViewById(R.id.seekBarTimeOriginImage);
        timeResponseImage = dialog.findViewById(R.id.seekBarTimeResponseImage);
        btnApply = dialog.findViewById(R.id.btnApply);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        int countAmountImage = amountImage + 1;
        informAmountImage.setText(String.valueOf(countAmountImage) + " image");
        txtTimeRememberImage.setText(String.valueOf(timeRemember / 1000) + " seconds");
        txtTimeResponseImage.setText(String.valueOf(timeResponse / 1000) + " seconds");
        seekBarAmountImage.setProgress(amountImage);
        timeRememberImage.setProgress(timeRemember / 1000);
        timeResponseImage.setProgress(timeResponse / 1000);

        seekBarAmountImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amountImage = progress;
                informAmountImage.setText(String.valueOf(progress + 1) + " image");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }
        });
        timeRememberImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeRemember = progress * 1000;
                txtTimeRememberImage.setText(String.valueOf(progress) + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }
        });
        timeResponseImage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeResponse = progress * 1000;
                txtTimeResponseImage.setText(String.valueOf(progress) + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                disableBtnCancel();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountImage = savedAmountImage;
                timeRemember = savedTimeRemember;
                timeResponse = savedTimeResponse;
                displayAmountImage(amountImage);
                dialog.dismiss();
            }
        });


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAmountImage(amountImage);
                dialog.dismiss();
            }
        });

    }

    // method setting time to play game
    private void setCountDownTimer() {
        int maxProgress = 1;
        if (timeRemember > 0) {
            maxProgress = timeRemember / 1000;
        }
        progressBar.setMax(maxProgress);
        progressBar.setProgress(0);
        countDownTimer = new CountDownTimer(timeRemember, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                flagCountDown = true;
                int progress = progressBar.getProgress();
                if (progress >= progressBar.getMax()) {
                    progress = 0;
                }
                progress += 1;
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

                if (!isFinishing()) {
                    alertBuilder.show();
                }
            }
        }.start();
    }

    // start mediaplayer
    private void playMedia() {
        if (mediaPlayer == null && (!isFinishing())) {
            mediaPlayer = MediaPlayer.create(this, R.raw.music_focus);
        }
        mediaPlayer.start();

        // when music off then continue play again
        mediaPlayer.setLooping(true);
    }


    // when exist this activity
    protected void onPause() {
        //stop mediaPlayer:
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d("Exist activity", "Media pause");
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                public void onCompletion(MediaPlayer mp) {
//                    mp.release();
//                }
//            });
//            mediaPlayer.stop();
//            mediaPlayer = null;
//            Log.d("Exist activity","Media pause");
        }
        if (mediaPlayerFail != null && mediaPlayerFail.isPlaying()) {
            mediaPlayerFail.pause();
            mediaPlayerFail.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayerFail.stop();
            mediaPlayerFail.release();
            mediaPlayerFail = null;
        }
        if (mediaPlayerCongratulation != null && mediaPlayerCongratulation.isPlaying()) {
            mediaPlayerCongratulation.pause();
            mediaPlayerCongratulation.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayerCongratulation.stop();
            mediaPlayerCongratulation.release();
            mediaPlayerCongratulation = null;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to exit this game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    // turn on/off music on menu taskbar
    private void turnSound() {
        if (!isTurnSound) {
//            this.onPause();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }
}