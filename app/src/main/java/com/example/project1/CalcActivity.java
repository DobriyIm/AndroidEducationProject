package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        this.tvHistory = findViewById(R.id.tvHistory);
        this.tvResult = findViewById(R.id.tvResult);

        this.tvHistory.setText("");
        this.tvResult.setText("0");

        for (int i = 0; i < 10; i++){
            findViewById(getResources().getIdentifier("btn" + i, "id", getPackageName())).
                setOnClickListener(this::digitClick);
        }

        findViewById(R.id.btnPlusMinus).setOnClickListener(this::pmClick);
        findViewById(R.id.btnDelimiter).setOnClickListener(this::delimiterClick);
        findViewById(R.id.btnBackspace).setOnClickListener(this::backspaceClick);}

    private void digitClick(View v){
        String result = this.tvResult.getText().toString();


        if(result.replaceAll("[-,]","").length() >= 10) return;

        String digit = ((Button)v).getText().toString();

        if(result.equals("0"))
            result = digit;
        else
            result += digit;

        this.tvResult.setText(result);
    }

    private void pmClick(View v){
        String result = this.tvResult.getText().toString();

        if(result.equals("0")) return;

        if(result.contains("-"))
            result = result.substring(1);
        else
            result = "-" + result;

        this.tvResult.setText(result);
    }

    private void delimiterClick(View v){
        String result = this.tvResult.getText().toString();

        if(!result.contains(",")){
            result += ",";
        }

        this.tvResult.setText(result);

    }

    private void backspaceClick(View v){
        String result = this.tvResult.getText().toString();

        if(result.equals("0"))
            return;
        if(result.replaceAll("[-,]","").length() == 1)
            result = "0";
        else
            result = result.substring(0,result.length() - 1);

        this.tvResult.setText(result);
    }
}