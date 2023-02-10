package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.calcBtn).setOnClickListener(this::btnCalcClick);
        findViewById(R.id.exitBtn).setOnClickListener(this::btnExitClick);

    }

    private void btnCalcClick(View v){
        Intent calcIntent = new Intent(MainActivity.this, CalcActivity.class);
    }
    private void btnExitClick(View v){
        finish();
    }
}