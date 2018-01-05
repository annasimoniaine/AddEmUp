package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.database.DataSource;
import com.simonebakker.simone.addemup.models.Game;

/**
 * Activity that's shown when a level is failed to show the result
 */
public class GameOver extends AppCompatActivity {

    private Button mMenuBtn;
    private Button mHighscoreBtn;

    private Game mGame;
    private int mLevelPoints;
    private int mNeededPoints;
    private int mLastLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        mGame = (Game) intent.getSerializableExtra("game");
        mLevelPoints = intent.getIntExtra("points", 0);
        mNeededPoints = intent.getIntExtra("needed_points", 0);
        mLastLevel = mGame.getmProgress();

        setViews();

        // on clicks are delayed by a second to prevent someone from clicking through
        // when they tried to submit an answer as the screen changed
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setOnClicks();
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        // blocks the back button
    }

    /**
     * Sets the Text and Button views
     */
    private void setViews() {
        TextView passedLevelText = findViewById(R.id.failed_level);
        TextView totalPointsText = findViewById(R.id.total_points);
        TextView levelPointsText = findViewById(R.id.level_points);
        mMenuBtn = findViewById(R.id.menu_btn);
        mHighscoreBtn = findViewById(R.id.highscore_btn);

        passedLevelText.setText(getString(R.string.failed_level, String.valueOf(mLastLevel)));
        levelPointsText.setText(getString(R.string.less_level_points, String.valueOf(mLevelPoints), String.valueOf(mNeededPoints)));
        totalPointsText.setText(String.valueOf(mGame.getmPoints()));
    }

    /**
     * Sets the onclicks for the buttons
     */
    private void setOnClicks() {
        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOver.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });

        mHighscoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHighScores();
            }
        });
    }

    /**
     * Starts the HighScores activity
     */
    private void goToHighScores() {
        Intent intent = new Intent(GameOver.this, HighScores.class);
        startActivity(intent);
        finish();
    }
}
