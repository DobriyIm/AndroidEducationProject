package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
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
    private boolean needClearResult;
    private boolean needClearHistory;
    private double operand1;
    private String operation;

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
        findViewById(R.id.btnClearE).setOnClickListener(this::clearEClick);
        findViewById(R.id.btnClearAll).setOnClickListener(this::clearAllClick);
        findViewById(R.id.btnEquals).setOnClickListener(this::equalClick);


        findViewById(R.id.btnDivision).setOnClickListener(this::functionalBtnCluck);
        findViewById(R.id.btnMultiplication).setOnClickListener(this::functionalBtnCluck);
        findViewById(R.id.btnSubtraction).setOnClickListener(this::functionalBtnCluck);
        findViewById(R.id.btnAddition).setOnClickListener(this::functionalBtnCluck);

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

    //region Click

    private void digitClick(View v){
        String result = this.tvResult.getText().toString();

        if(this.needClearResult){
            this.needClearResult = false;
            result = "0";
        }

        if(result.replaceAll("[" + this.delimiterSign + this.minusSign +"]","").length() >= 10) return;

        String digit = ((Button)v).getText().toString();

        if(result.equals("0")) {
            result = digit;
        }
        else
            result += digit;

        if(this.needClearHistory){
            this.tvHistory.setText("");
            this.needClearHistory = false;

        }

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
        if(this.needClearHistory){
            this.tvHistory.setText("");
            this.needClearHistory = false;
        }
        if(this.needClearResult)
            this.needClearResult = false;

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
            this.Alert(R.string.calc_divide_by_zero);
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
            this.Alert(R.string.calc_sqr_minus);
            return;
        }

        this.tvHistory.setText(String.format("√(%s) =", result));
        result = this.toStringParser(Math.sqrt(d));
        this.tvResult.setText(result);

    }
    private void clearEClick(View v){
        this.tvResult.setText("0");
    }
    private void clearAllClick(View v){
        this.tvResult.setText("0");
        this.tvHistory.setText("");
    }
    private void functionalBtnCluck(View v){
        String fn = ((Button) v).getText().toString();
        String result = this.tvResult.getText().toString();

        String history = String.format("%s %s", result, fn);
        this.tvHistory.setText(history);

        this.needClearResult = true;
        this.needClearHistory = false;

        operation = fn;
        operand1 = this.toDoubleParser(result);
    }
    private void equalClick(View v){
        String result = this.tvResult.getText().toString();
        String history = this.tvHistory.getText().toString();

        tvHistory.setText(String.format("%s %s =", history, result));

        double operand2 = this.toDoubleParser(result);

        if(operation.equals(getString(R.string.btn_addition)))
            this.tvResult.setText(this.toStringParser(operand1 + operand2));
        else if(operation.equals(getString(R.string.btn_subtraction)))
            this.tvResult.setText(this.toStringParser(operand1 - operand2));
        else if(operation.equals(getString(R.string.btn_multiplication)))
            this.tvResult.setText(this.toStringParser(operand1 * operand2));
        else if(operation.equals(getString(R.string.btn_division)))
            if(operand2 == 0)
                this.Alert(R.string.calc_divide_by_zero);
            else
                this.tvResult.setText(this.toStringParser(operand1 / operand2));

        this.needClearResult = true;
        this.needClearHistory = true;
    }

    //endregion

    //region Alert
    private void Alert(int stringId){

        Toast.makeText(CalcActivity.this, stringId, Toast.LENGTH_LONG).show();

        Vibrator vibrator;

        long[] vibrationPattern = {0,200,100,200};

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.S){
            VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        }
        else
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern,-1));
        else
            vibrator.vibrate(vibrationPattern, -1);
    }
    //endregion

    //region Parsers

    private double toDoubleParser(String str){
        if (str.contains(this.delimiterSign))
            str = str.replace(this.delimiterSign, ".");
        if (str.contains(this.minusSign))
            str = str.replace(this.minusSign, "-");
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
        return str;
    }

    //endregion
}