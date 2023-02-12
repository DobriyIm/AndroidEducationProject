package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;
    private String minusSign;
    private String delimiterSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        this.tvHistory = findViewById(R.id.tvHistory);
        this.tvResult = findViewById(R.id.tvResult);

        this.tvHistory.setText("");
        this.tvResult.setText("0");
        this.minusSign = getApplicationContext().getString(R.string.calc_minus);
        this.delimiterSign = getApplicationContext().getString(R.string.calc_delimiter);

        for (int i = 0; i < 10; i++){
            findViewById(getResources().getIdentifier("btn" + i, "id", getPackageName())).
                setOnClickListener(this::digitClick);
        }

        findViewById(R.id.btnPlusMinus).setOnClickListener(this::pmClick);
        findViewById(R.id.btnDelimiter).setOnClickListener(this::delimiterClick);
        findViewById(R.id.btnBackspace).setOnClickListener(this::backspaceClick);
        findViewById(R.id.btnInverse).setOnClickListener(this::inverseClick);
        findViewById(R.id.btnRoot).setOnClickListener(this::rootClick);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("history", tvHistory.getText());
        outState.putCharSequence("result", tvResult.getText());
        Log.d(CalcActivity.class.getName(), "Saved!");
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvHistory.setText(savedInstanceState.getCharSequence("history"));
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        Log.d(CalcActivity.class.getName(), "Loaded!");
    }

    private void digitClick(View v){
        String result = this.tvResult.getText().toString();


        if(result.replaceAll("[" + this.delimiterSign + this.minusSign +"]","").length() >= 10) return;

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

        if(result.contains(this.minusSign))
            result = result.substring(1);
        else
            result = this.minusSign + result;

        this.tvResult.setText(result);
    }
    private void delimiterClick(View v){
        String result = this.tvResult.getText().toString();

        if(!result.contains(this.delimiterSign)){
            result += this.delimiterSign;
        }

        this.tvResult.setText(result);

    }
    private void backspaceClick(View v){
        String result = this.tvResult.getText().toString();

        if(result.equals("0"))
            return;
        if(result.replaceAll("[" + this.delimiterSign + this.minusSign +"]","").length() == 1)
            result = "0";
        else
            result = result.substring(0,result.length() - 1);

        this.tvResult.setText(result);
    }
    private void inverseClick(View v){
        String result = this.tvResult.getText().toString();

        double d = this.toDoubleParser(result);

        if (d == 0) {
            Toast.makeText(CalcActivity.this, R.string.calc_divide_by_zero, Toast.LENGTH_LONG).show();
            return;
        }



        this.tvHistory.setText(String.format("1/%s =", result));
        result = this.toStringParser(1/d);
        this.tvResult.setText(result);

    }
    private void rootClick(View v){
        String result = this.tvResult.getText().toString();

        double d = this.toDoubleParser(result);

        if(d < 0) {
            Toast.makeText(CalcActivity.this, R.string.calc_sqr_minus, Toast.LENGTH_LONG).show();
            return;
        }

        this.tvHistory.setText(String.format("√(%s) =", result));
        result = this.toStringParser(Math.sqrt(d));
        this.tvResult.setText(result);

    }

    private double toDoubleParser(String str){
        if (str.contains(this.delimiterSign))
            str = str.replace(this.delimiterSign, ".");
        if (str.contains(this.minusSign))
            str = str.replace(this.minusSign, "-");
        Log.d("toDoubleParser", str);
        return Double.parseDouble(str);
    }
    private String toStringParser(double d){
        String str = String.valueOf(d);
        int maxLength = 10;

        if(str.contains(".")) {
            str = str.replace(".", this.delimiterSign);
            maxLength++;
        }
        if(str.startsWith("-")) {
            str = str.replace("-", this.minusSign);
            maxLength++;
        }
        if(str.length() > maxLength)
            str = str.substring(0, maxLength);
        Log.d("toStringParser", str);
        return str;
    }
}