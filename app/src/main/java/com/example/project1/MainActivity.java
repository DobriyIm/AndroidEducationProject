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
        findViewById(R.id.gameBtn).setOnClickListener(this::btnGameClick);
        findViewById(R.id.ratesBtn).setOnClickListener(this::btnRatesClick);
        findViewById(R.id.chatBtn).setOnClickListener(this::btnChatClick);

    }

    private void btnCalcClick(View v){
        Intent calcIntent = new Intent(MainActivity.this, CalcActivity.class);
        startActivity( calcIntent ) ;
    }
    private void btnGameClick(View v){
        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
        startActivity( gameIntent ) ;
    }
    private void btnRatesClick(View v){
        Intent ratesIntent = new Intent(MainActivity.this, RatesActivity.class);
        startActivity( ratesIntent ) ;
    }
    private void btnChatClick(View v){
        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity( chatIntent ) ;
    }

    private void btnExitClick(View v){
        finish();
    }
}