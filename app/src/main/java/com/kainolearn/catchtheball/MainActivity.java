package com.kainolearn.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.*;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView black;
    private ImageView pink;

    // 位置
    private float boxY;
    private float orangeX;
    private float orangeY;
    private float blackX;
    private float blackY;
    private float pinkX;
    private float pinkY;

    // スピード
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    // Score
    private int score = 0;

    // サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    // Handler & Timer
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean action_flg = false;
    private boolean start_flg = false;


    // Sound
    private SoundPlayer soundPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        black = findViewById(R.id.black);
        pink = findViewById(R.id.pink);

        // Screen Size
        WindowManager wm = getWindowManager();
//        Context context = this;
//        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.d("kainotest", "wm:"+wm);
        Log.d("kainotest", "display:"+display);
        Log.d("kainotest", "size:"+size);

        screenWidth = size.x;
        screenHeight = size.y;

        boxSpeed = Math.round(screenHeight/ 60f);
        orangeSpeed = Math.round(screenWidth/ 60f);
        pinkSpeed = Math.round(screenWidth/ 36f);
        blackSpeed = Math.round(screenWidth/ 45f);

        Log.v("kkk", "boxSpeed:"+boxSpeed);
        Log.v("kkk", "orangeSpeed:"+orangeSpeed);
        Log.v("kkk", "pinkSpeed:"+pinkSpeed);
        Log.v("kkk", "blackSpeed:"+blackSpeed);


        orange.setX(-80.0f);
        orange.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);

        scoreLabel.setText("Score : 0");
    }


    public void changePos() {

        hitCheck();

        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (float)Math.floor(Math.random() * (frameHeight -orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX += screenWidth + 10;
            blackY = (float)Math.floor(Math.random() * (frameHeight -black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX += screenWidth + 5000;
            pinkY = (float)Math.floor(Math.random() * (frameHeight -pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        if (action_flg) {
            // タッチしてる
            boxY -= boxSpeed;
        } else {
            // タッチしてない
            boxY+= boxSpeed;
        }

        if (boxY < 0) boxY = 0;

        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck() {

        // orange
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        // 0 <= orangeCenterX <= boxWidth
        // boxY <= orangeCenterY <= boxY + boxHeight

        if (0 <= orangeCenterX && orangeCenterX <= boxSize &&
            boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize) {

            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        // pink
        float pinkCenterX = pinkX + pink.getWidth() / 2;
        float pinkCenterY = pinkY + pink.getHeight() / 2;

        if (0 <= pinkCenterX && pinkCenterX <= boxSize &&
                boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize) {

            pinkX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        // black
        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if (0 <= blackCenterX && blackCenterX <= boxSize &&
                boxY <= blackCenterY && blackCenterY <= boxY + boxSize) {

            soundPlayer.playOverSound();

            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            Log.d("kkk", "timer:"+timer);
            // 結果画面へ
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            Log.d("kkk", "score:"+score);
            Log.d("kkk", "intent:"+intent);
            startActivity(intent);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (start_flg == false) {
            start_flg = true;

            FrameLayout flame = findViewById(R.id.frame);
            frameHeight = flame.getHeight();
            Log.d("kkk", "frameHeight:"+frameHeight);

            boxY = box.getY();
            boxSize = box.getHeight();
            Log.d("kkk", "boxY:"+boxY);
            Log.d("kkk", "boxSize:"+boxSize);

            startLabel.setVisibility(View.GONE);

            // タイマースタート
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
    }
}