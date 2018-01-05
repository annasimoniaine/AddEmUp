package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.models.Game;
import com.simonebakker.simone.addemup.models.Level;

import static java.lang.Integer.parseInt;

/**
 * Activity that's shown before a level starts to announce the setting & variables
 */
public class PreLevel extends AppCompatActivity {

    private Game mGame;
    private Level mLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_level);

        Intent intent = getIntent();
        mGame = (Game) intent.getSerializableExtra("game");
        mLevel = new Level(mGame.getmProgress());

        setViews();
        setCountdown();
    }

    // blocks the back button
    @Override
    public void onBackPressed() {
    }

    /**
     * Sets the text views
     */
    private void setViews() {
        TextView levelView = findViewById(R.id.level_view);
        TextView timeForLevelView = findViewById(R.id.time_for_level_view);
        TextView amountOfNumbersView = findViewById(R.id.amount_of_numbers_view);
        TextView pointsForCorrectView = findViewById(R.id.points_for_correct_view);
        TextView pointsNeededView = findViewById(R.id.points_needed_view);

        levelView.setText(getString(R.string.level, String.valueOf(mGame.getmProgress())));
        timeForLevelView.setText(getString(R.string.time_for_level, getLevelTime()));
        amountOfNumbersView.setText(getString(R.string.amount_to_add, String.valueOf(mLevel.getmAmountOfNumbers())));
        pointsForCorrectView.setText(getString(R.string.points_for_correct, String.valueOf(mLevel.getmPointsForCorrect())));
        pointsNeededView.setText(getString(R.string.points_to_pass_level, String.valueOf(mLevel.getmNeededPoints())));
    }

    /**
     * Sets up the countdown
     * Counts down from 5 seconds before continuing to the game
     */
    private void setCountdown() {
        final TextView countdownView = findViewById(R.id.countdown_view);

        new CountDownTimer(5000, 1000) {
            public void onTick(long msLeft) {
                String seconds = String.valueOf(msLeft / 1000);
                countdownView.setText(seconds);
            }
            public void onFinish() {
                Intent intent = new Intent(PreLevel.this, GameActivity.class);
                intent.putExtra("game", mGame);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    /**
     * Formats milliseconds left into mm:ss
     */
    private String getLevelTime() {
        int msInLevel = mLevel.getmLevelTime();
        String minutes = String.valueOf(msInLevel / 60000);
        if (parseInt(minutes) < 10) {
            minutes = "0" + minutes;
        }
        String seconds = String.valueOf((msInLevel % 60000) / 1000);
        if (parseInt(seconds) < 10) {
            seconds = "0" + seconds;
        }

        return minutes + ":" + seconds;
    }
}
