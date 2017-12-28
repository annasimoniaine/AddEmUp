package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.database.DataSource;
import com.simonebakker.simone.addemup.models.Game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EndOfLevel extends AppCompatActivity {

    private Button mQuitBtn, mNextBtn, mQuitNoSaveBtn;

    private Game mGame;
    private int mLevelPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_level);

        Intent intent = getIntent();
        mGame = (Game) intent.getSerializableExtra("game");
        mLevelPoints = intent.getIntExtra("points", 0);

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
        TextView mPassedLevelText = (TextView) findViewById(R.id.passed_level);
        TextView mTotalPointsText = (TextView) findViewById(R.id.total_points);
        TextView mLevelPointsText = (TextView) findViewById(R.id.level_points);
        mQuitBtn = (Button) findViewById(R.id.quit_btn);
        mNextBtn = (Button) findViewById(R.id.next_btn);
        mQuitNoSaveBtn = (Button) findViewById(R.id.quit_no_save_btn);

        mPassedLevelText.setText(getString(R.string.passed_level, String.valueOf(mGame.getmProgress() - 1)));
        mLevelPointsText.setText(getString(R.string.level_points, String.valueOf(mLevelPoints)));
        mTotalPointsText.setText(String.valueOf(mGame.getmPoints()));

        mNextBtn.setText(getString(R.string.next_level, String.valueOf(mGame.getmProgress())));
    }

    private void setOnClicks() {
        mQuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGame();
                toMenu();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EndOfLevel.this, PreLevel.class);
                intent.putExtra("game", mGame);
                startActivity(intent);
                finish();
            }
        });

        mQuitNoSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMenu();
            }
        });
    }

    private void saveGame() {
        // Set the game's date to now
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();
        String date = dateFormat.format(currentDate);
        mGame.setmDate(date);

        // Remove previously saved game(s) & save this one to the database
        DataSource dataSource = new DataSource(this);
        dataSource.removePrevious();
        dataSource.saveGame(mGame);
    }

    private void toMenu() {
        Intent intent = new Intent(EndOfLevel.this, Menu.class);
        startActivity(intent);
        finish();
    }
}
