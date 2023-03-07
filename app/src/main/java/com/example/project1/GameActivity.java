
package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    //region Fields
    private int[][] cells = new int[4][4];
    private TextView[][] tvCells = new TextView[4][4];

    private int[][] cellsCopy = new int[4][4];
    private TextView[][] tvCellsCopy = new TextView[4][4];
    private int scoreCopy;

    private final Random random = new Random();

    private Animation spawnCellAnimation;

    //region Score
    private int score;
    private TextView tvScore;
    private int bestScore;
    private TextView tvBestScore;
    private final String bestScoreFilename = "best_score.txt";
    //endregion

    private boolean isContinuePlaying;
    //endregion


    //region Click
    private void btnNewGameClick(View v){
        this.showNewGameDialog();
    }
    private void btnUndoClick(View v){
        this.loadFiledFromCopy();
    }
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        this.tvScore = findViewById(R.id.tv_score);
        this.bestScore = loadBestScore();
        this.tvBestScore = findViewById(R.id.tv_best_score);
        this.tvBestScore.setText(getString(R.string.tv_best_score_pattern, bestScore));


        this.spawnCellAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.spawn_cell);
        this.spawnCellAnimation.reset();

        for(int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                this.tvCells[i][j] = findViewById(getResources().getIdentifier("game_cell_" + i + j, "id", getPackageName()));
            }
        }

        findViewById(R.id.game_layout).setOnTouchListener(new OnSwipeListener(GameActivity.this){
            @Override
            public void onSwipeLeft() {
                if(moveLeft()) spawnCell();
                else  Toast.makeText(GameActivity.this, "Not left move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeRight() {
                if(moveRight()) spawnCell();
                else  Toast.makeText(GameActivity.this, "Not right move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeTop() {
                if(moveTop()) spawnCell();
                else  Toast.makeText(GameActivity.this, "Not top move", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeBottom() {
                if(moveBottom()) spawnCell();
                else  Toast.makeText(GameActivity.this, "Not bottom move", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.game_start_new).setOnClickListener(this::btnNewGameClick);
        findViewById(R.id.game_undo).setOnClickListener(this::btnUndoClick);

        this.newGame();
    }

    private void newGame(){
        this.score = 0;
        this.scoreCopy = 0;
        this.isContinuePlaying = false;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                this.cells[i][j] = 0;
            }
        }

        findViewById(R.id.game_undo).setEnabled(false);

        this.spawnCell();
        this.spawnCell();
    }

    //region UndoSystem
    private void setFiledCopy(){
        for(int i = 0; i < 4; i++){
            this.cellsCopy[i] = this.cells[i].clone();
        }
        this.scoreCopy = this.score;
        this.undoButtonState(true);
    }
    private void loadFiledFromCopy(){
        for(int i = 0; i < 4; i++){
            this.cells[i] = this.cellsCopy[i].clone();
        }
        this.score = this.scoreCopy;

        this.showField();

        this.undoButtonState(false);
    }
    private void undoButtonState(boolean isEnable){
        findViewById(R.id.game_undo).setEnabled(isEnable);
    }
    //endregion

    //region ScoreSystem
    private boolean saveBestScore(){
        try(FileOutputStream fos = openFileOutput(this.bestScoreFilename, Context.MODE_PRIVATE)){
            DataOutputStream writer = new DataOutputStream(fos);
            writer.writeInt(bestScore);
            writer.flush();
            writer.close();
        }catch (Exception ex){
            Log.d("saveSaveBestScore", ex.getMessage());
            return false;
        }
        return true;
    }
    private int loadBestScore(){
        int best = 0;
        try(FileInputStream fis = openFileInput(this.bestScoreFilename)) {
            DataInputStream reader = new DataInputStream(fis);
            best = reader.readInt();
            reader.close();
        }catch (Exception ex){
            Log.d("loadBestScore", ex.getMessage());
            return 0;
        }
        return best;
    }
    //endregion

    //region EndGameSystem
    private boolean isWin(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(this.cells[i][j] == 2048){
                    return true;
                }
            }
        }
        return false;
    }
    private void showWinDialog(){
        new AlertDialog.Builder(GameActivity.this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(R.string.win_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(R.string.win_message)
                .setCancelable(false)
                .setPositiveButton(R.string.win_btn_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isContinuePlaying = true;
                    }
                }).setNegativeButton(R.string.win_btn_exit  , (dialog,whichButton) ->{
                    finish();
                })
                .setNeutralButton(R.string.win_btn_new_game, (dialog, whichButton) -> {
                    this.newGame();
                })
                .show();

    }
    //endregion

    //region StartNewGameSystem
    private void showNewGameDialog(){
        new AlertDialog.Builder(GameActivity.this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(R.string.new_game_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(R.string.new_game_message)
                .setCancelable(false)
                .setPositiveButton(R.string.new_game_btn_Yes, (dialog, whichButton) ->{
                    this.newGame();
                }).setNegativeButton(R.string.new_game_btn_No  , (dialog,whichButton) ->{
                    isContinuePlaying = true;
                })
                .show();

    }
    //endregionS

    //region ViewSystem
    private void showField(){

        Resources resources = getResources();

        for ( int i = 0; i < 4; i++){
            for( int j = 0; j < 4; j++){
                this.tvCells[i][j].setText(String.valueOf(this.cells[i][j]));
                this.tvCells[i][j].setTextAppearance(resources.getIdentifier("GameCell_" + this.cells[i][j], "style", getPackageName()));

                tvCells[i][j].setBackgroundColor(
                    resources.getColor(resources.getIdentifier(
                            "game_bg_" + this.cells[i][j],"color", getPackageName()), getTheme()
                    )
                );
            }
        }

        this.tvScore.setText(getString(R.string.tv_score_pattern, score));

        if(this.score > this.bestScore){
            this.bestScore = this.score;
            if(this.saveBestScore()){
                this.tvBestScore.setText(getString(R.string.tv_best_score_pattern, this.bestScore));
            }
        }

        if(!this.isContinuePlaying){
            if(this.isWin()){
                this.showWinDialog();
            }
        }
    }
    private boolean spawnCell(){

        List<Integer> freeCellIndexes = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            for(int j  = 0; j < 4; j++){
                if(this.cells[i][j] == 0){
                    freeCellIndexes.add(i * 10 + j);
                }
            }
        }

        int cnt = freeCellIndexes.size();

        if(cnt == 0)
            return false;


        int randIndex = random.nextInt(cnt);
        Log.wtf("rnd",String.valueOf(randIndex));

        Log.wtf("xx",String.valueOf(freeCellIndexes.get(randIndex)));
        int x = freeCellIndexes.get(randIndex) / 10;
        int y = freeCellIndexes.get(randIndex) % 10;


        Log.wtf("x",String.valueOf(x));
        Log.wtf("y",String.valueOf(y));

        this.cells[x][y] = random.nextInt(10) < 9 ? 2 : 4;

        this.tvCells[x][y].startAnimation(this.spawnCellAnimation);

        this.showField();

        return true;
    }
    //endregion

    //region MoveSystem
    private boolean moveLeft(){

        this.setFiledCopy();

        boolean result = false;
        boolean needRepeat;

        for( int i = 0; i < 4; i++){
            do {
                needRepeat = false;
                for (int j = 0; j < 3; j++) {
                    if (cells[i][j] == 0) {
                        for (int k = j + 1; k < 4; k++) {
                            if (cells[i][k] != 0) {
                                cells[i][j] = cells[i][k];
                                cells[i][k] = 0;
                                needRepeat = true;
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }while (needRepeat);

            for(int j = 0; j < 3; j++){
                if(this.cells[i][j] != 0 && cells[i][j] == cells[i][j+1]){
                    cells[i][j] *= 2;
                    for(int k = j + 1; k < 3; k++){
                        this.cells[i][k] = this.cells[i][k + 1];
                    }
                    cells[i][3] = 0;
                    result = true;
                    this.score += cells[i][j];
                }
            }
        }

        return result;
    }
    private boolean moveRight(){

        this.setFiledCopy();

        boolean result = false;
        boolean needRepeat;

        for( int i = 0; i < 4; i++){
            do {
                needRepeat = false;
                for (int j = 3; j > 0; --j) {
                    if (cells[i][j] == 0) {
                        for (int k = j - 1; k >= 0; --k) {
                            if (cells[i][k] != 0) {
                                cells[i][j] = cells[i][k];
                                cells[i][k] = 0;
                                needRepeat = true;
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }while (needRepeat);

            for(int j = 3; j > 0; --j){
                if(this.cells[i][j] != 0 && cells[i][j] == cells[i][j - 1]){
                    cells[i][j] *= 2;
                    for(int k = j - 1; k > 0; --k){
                        this.cells[i][k] = this.cells[i][k - 1];
                    }
                    cells[i][0] = 0;
                    result = true;
                    this.score += cells[i][j];
                }
            }
        }

        return result;
    }
    private boolean moveTop(){

        this.setFiledCopy();

        boolean result = false;
        boolean needRepeat;

        for( int i = 0; i < 4; i++){
            do {
                needRepeat = false;
                for (int j = 0; j < 3; j++) {
                    if (cells[j][i] == 0) {
                        for (int k = j + 1; k < 4; k++) {
                            if (cells[k][i] != 0) {
                                cells[j][i] = cells[k][i];
                                cells[k][i] = 0;
                                needRepeat = true;
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }while (needRepeat);

            for(int j = 0; j < 3; j++){
                if(this.cells[j][i] != 0 && cells[j][i] == cells[j+1][i]){
                    cells[j][i] *= 2;
                    for(int k = j + 1; k < 3; k++){
                        this.cells[k][i] = this.cells[k+1][i];
                    }
                    cells[3][i] = 0;
                    result = true;
                    this.score += cells[j][i];
                }
            }

        }

        return result;
    }
    private boolean moveBottom(){

        this.setFiledCopy();

        boolean result = false;
        boolean needRepeat;

        for( int i = 0; i < 4; i++){
            do {
                needRepeat = false;
                for (int j = 3; j > 0; --j) {
                    if (cells[j][i] == 0) {
                        for (int k = j - 1; k >= 0; --k) {
                            if (cells[k][i] != 0) {
                                cells[j][i] = cells[k][i];
                                cells[k][i] = 0;
                                needRepeat = true;
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }while (needRepeat);

            for(int j = 3; j > 0; --j){
                if(this.cells[j][i] != 0 && cells[j][i] == cells[j - 1][i]){
                    cells[j][i] *= 2;
                    for(int k = j - 1; k > 0; --k){
                        this.cells[k][i] = this.cells[k - 1][i];
                    }
                    cells[0][i] = 0;
                    result = true;
                    this.score += cells[j][i];
                }
            }
        }

        return result;
    }
    //endregion
}