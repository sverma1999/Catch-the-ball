package com.practice.shubham.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {


        private TextView scoreLbl;
        private TextView startLbl;
        private ImageView pacemanStkr;
        private ImageView orange;
        private ImageView pink;
        private ImageView black;

        //size
    private int frameHeight;
    private int pacemanSize;
    private int screenWidth;
    private int screenHeight;

        //position
        private int pacemanY;
        private int orangeX;
        private int orangeY;
        private int pinkX;
        private int pinkY;
        private int blackX;
        private int blackY;

        //speed
        private int pacemanSpeed;
        private int orangeSpeed;
        private int pinkSpeed;
        private int blackSpeed;

        //score
        private int score = 0;


        //initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;


//Status check(no movement in the paceman in the start).
    private boolean action_flg = false;
    private boolean start_flag = false;

    //Music
    MediaPlayer duringGameMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLbl = (TextView) findViewById(R.id.scoreLabel);
        startLbl = (TextView) findViewById(R.id.startLabel);
        pacemanStkr = (ImageView) findViewById(R.id.paceman);
        orange = (ImageView) findViewById(R.id.orangeball);
        pink = (ImageView) findViewById(R.id.pinkdote);
        black = (ImageView) findViewById(R.id.black);

        //Get screen size.
        WindowManager wM = getWindowManager();
        Display display = wM.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenHeight = size.y;
        screenWidth = size.x;


        //Now
        //Nexus4 width:768 height: 1184
        //speed paceman:20 orange:12 pink:20 black:16

        pacemanSpeed = Math.round(screenHeight/60F); //1184 / 60 = 19.733... => 20
        orangeSpeed = Math.round(screenWidth/60F); //768/60 = 12.8 => 13
        pinkSpeed = Math.round(screenWidth/36F); // 768/36 = 21.333 => 21
        blackSpeed = Math.round(screenWidth/45F); // 768/45 = 17.06 => 17


//        Log.v("SPEED_PACEMAN",pacemanSpeed + "");
//        Log.v("SPEED_ORANGE",orangeSpeed + "");
//        Log.v("SPEED_PINK",pinkSpeed + "");
//        Log.v("SPEED_BLACK",blackSpeed + "");


        orange.setX(-80);
        orange.setY(-80);
        pink.setX(-80);
        pink.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLbl.setText("Score: 0");



        duringGameMusic = MediaPlayer.create(Main.this, R.raw.majorlazer);
        duringGameMusic.setLooping(true);
        duringGameMusic.start();

    }



    public void changePos(){

        hitCheck();

        //Orange
        orangeX -=orangeSpeed;//affects on speed of oncoming orange ball.
        if(orangeX < 0) {
            orangeX = screenWidth + 20;// duration of ball's arrival
            //randomly anywhere in the frame(while Y-axis is zero and x-axis is changing).
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Black

        blackX -=blackSpeed;//affects on speed of oncoming black star.
        if(blackX <0){
            blackX = screenWidth+10;// duration of star's arrival
            //randomly anywhere in the frame(while Y-axis is zero and x-axis is changing).
            blackY = (int)Math.floor(Math.random()*(frameHeight-black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //Pink
        pinkX -=pinkSpeed;//affects on speed of oncoming pink ball.
        if(pinkX <0){
            pinkX = screenWidth+5000;// duration of ball's arrival
            //randomly anywhere in the frame(while Y-axis is zero and x-axis is changing).
            pinkY = (int)Math.floor(Math.random()*(frameHeight-pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //Move Box
        if(action_flg == false){
            //Releasing(it will go down)
            pacemanY+=pacemanSpeed;
        } else{
            // Touching(it will go up)
            pacemanY-=pacemanSpeed;
        }

        //Check paceman position.
        //creating boundary limit from the top for paceman sticker.
        if (pacemanY < 0)
            pacemanY = 0;
        //creating boundary limit from down for paceman sticker.
       if(pacemanY > frameHeight - pacemanSize)
            pacemanY = frameHeight - pacemanSize;
        // moving up and down on Y-axis with X is zero.
        pacemanStkr.setY(pacemanY);

        scoreLbl.setText("Score: "+ score);

    }//end of changing position.

    //0<= orangeCenterX <= pacemanWidth
    // pacemanY <= orangeCenterY <= pacemanHeight

    public void hitCheck(){
        // if the center of the ball is in the box, it counts as a hit.

        //orange
        int orangeCenterX = orangeX + orange.getWidth()/2;
        int orangeCenterY = orangeY + orange.getHeight()/2;

        if(0 <= orangeCenterX && orangeCenterX <= pacemanSize && pacemanY <= orangeCenterY && orangeCenterY <= pacemanY + pacemanSize){

            score +=10;
            orangeX = -10;
            sound.playHitSound();
        }

        //Pink
        int pinkCenterX = pinkX + pink.getWidth()/2;
        int pinkCenterY = pinkY + pink.getHeight()/2;

        if(0 <= pinkCenterX && pinkCenterX <= pacemanSize && pacemanY <= pinkCenterY && pinkCenterY <= pacemanY + pacemanSize){

            score +=30;
            pinkX = -10;
            sound.playHitSound();
        }

        //Black
        int blackCenterX = blackX + black.getWidth()/2;
        int blackCenterY = blackY + black.getHeight()/2;

        if(0 <= blackCenterX && blackCenterX <= pacemanSize && pacemanY <= blackCenterY && blackCenterY <= pacemanY + pacemanSize){

            //Stop Timer!
            timer.cancel();
            timer = null;
            sound.playOverSound();

            // Show Result
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("Score", score);
            startActivity(intent);

        }
    }
public boolean onTouchEvent(MotionEvent mE){
    //-->
           //if the paceman is not moving then...
        if(start_flag ==false){
            //...then move the paceman.
            start_flag = true;

            //Why get frame height and paceman height here ?
            //Because the UI has not been set on the screen in OnCreate()!!
            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            pacemanY = (int) pacemanStkr.getY();

            //The paceman is a Square.(height and width are same)!!
            pacemanSize = pacemanStkr.getHeight();

            startLbl.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                           changePos();
                        }// end of second run with changing position.
                    }); //end of post of Handler
                }// end of first run.
            },0,20); // end of schedual timer.
//-->
        }// end of if statement.
        else {
           if (mE.getAction()== MotionEvent.ACTION_UP){
               action_flg = false;
            }//end of if statement.
            else if (mE.getAction()== MotionEvent.ACTION_DOWN){
                action_flg = true;
            }// end of else if statement.
        }//end of else statement.

    return true;
}// end of touch event method.
    //Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause(){
        super.onPause();
        duringGameMusic.release();

    }
}