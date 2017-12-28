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

public class GameOver extends AppCompatActivity {

    private Button mMenuBtn;
    private Button mHighscoreBtn;
    private Button mSaveBtn;
    private EditText mNameInput;

    private Game mGame;
    private int mLevelPoints;
    private int mNeededPoints;
    private int mLastLevel;

    private boolean mNewHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        mGame = (Game) intent.getSerializableExtra("game");
        mLevelPoints = intent.getIntExtra("points", 0);
        mNeededPoints = intent.getIntExtra("needed_points", 0);
        mLastLevel = intent.getIntExtra("last_level", 0);

        // boolean that's true if the finished game is a new high score
        mNewHighScore = intent.getBooleanExtra("new_highscore", false);

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

    // blocks the back button
    @Override
    public void onBackPressed() {
    }

    private void setViews() {
        TextView mPassedLevelText = (TextView) findViewById(R.id.failed_level);
        TextView mTotalPointsText = (TextView) findViewById(R.id.total_points);
        TextView mLevelPointsText = (TextView) findViewById(R.id.level_points);
        TextView mClaimScoreText = (TextView) findViewById(R.id.claim_score);
        mMenuBtn = (Button) findViewById(R.id.menu_btn);
        mHighscoreBtn = (Button) findViewById(R.id.highscore_btn);
        mNameInput = (EditText) findViewById(R.id.name_input);
        mSaveBtn = (Button) findViewById(R.id.save_btn);

        mPassedLevelText.setText(getString(R.string.failed_level, String.valueOf(mLastLevel)));
        mLevelPointsText.setText(getString(R.string.less_level_points, String.valueOf(mLevelPoints), String.valueOf(mNeededPoints)));
        mTotalPointsText.setText(String.valueOf(mGame.getmPoints()));

        if (mNewHighScore) {
            mClaimScoreText.setText(getString(R.string.claim_score));
        } else {
            mClaimScoreText.setText(getString(R.string.no_high_score));
            mNameInput.setVisibility(View.GONE);
            mSaveBtn.setVisibility(View.GONE);
        }
    }

    private void setOnClicks() {
        if (mNewHighScore) {
            // updates the name attached to the game in the db and goes to high score screen
            mSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mNameInput.getText().toString();

                    DataSource dataSource = new DataSource(GameOver.this);
                    dataSource.setNameHighscore(mGame, name);

                    goToHighScores();
                }
            });
        }

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

    private void goToHighScores() {
        Intent intent = new Intent(GameOver.this, HighScores.class);
        intent.putExtra("lastGameID", mGame.getmID());
        startActivity(intent);
        finish();
    }
}
