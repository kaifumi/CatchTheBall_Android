package com.kainolearn.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.*;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    // サイズ
    private int frameHeight;
    private int boxSize;

    // Handler & Timer
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean action_flg = false;
    private boolean start_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        black = findViewById(R.id.black);
        pink = findViewById(R.id.pink);


        orange.setX(-80.0f);
        orange.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);

        boxY = 500.0f;
    }


    public void changePos() {

        if (action_flg) {
            // タッチしてる
            boxY -= 20;
        } else {
            // タッチしてない
            boxY+= 20;
        }

        if (boxY < 0) boxY = 0;

        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (start_flg == false) {
            start_flg = true;

            FrameLayout flame = findViewById(R.id.frame);
            frameHeight = flame.getHeight();
            Log.d("kaino", "frameHeight:"+frameHeight);

            boxY = box.getY();
            boxSize = box.getHeight();
            Log.d("kaino", "boxY:"+boxY);
            Log.d("kaino", "boxSize:"+boxSize);

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
}