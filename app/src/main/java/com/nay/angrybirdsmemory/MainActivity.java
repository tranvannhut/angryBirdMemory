package com.nay.angrybirdsmemory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textAngryBird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reflection();
        textAngryBird.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,QuestionAngryBirdActivity.class)));
    }

    /* Sub method */

    // reflection for view
    private void reflection() {
        textAngryBird = (TextView) findViewById(R.id.textViewAngryBird);
    }

}