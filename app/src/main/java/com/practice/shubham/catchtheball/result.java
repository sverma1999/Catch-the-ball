package com.practice.shubham.catchtheball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.media.MediaPlayer;

import org.w3c.dom.Text;

public class result extends AppCompatActivity {
    MediaPlayer applauseSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);

        int score =  getIntent().getIntExtra("Score",0);
        scoreLabel.setText(score + "");

        SharedPreferences setting = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore= setting.getInt("HIGH_SCORE",0);

        if (score > highScore){
            highScoreLabel.setText("High Score: " + score);

            // Save
            SharedPreferences.Editor editor = setting.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        }
        else{
            highScoreLabel.setText("High Score: "+ highScore);
        }

        applauseSound = MediaPlayer.create(result.this, R.raw.applause);
        applauseSound.setLooping(true);
        applauseSound.start();

    }

 @Override
   protected void onPause() {
     super.onPause();
     applauseSound.release();
 }
     public void tryAgain(View view){
    startActivity(new Intent(getApplicationContext(), start.class));


    }
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
}
