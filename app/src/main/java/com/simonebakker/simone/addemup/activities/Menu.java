package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.database.DataSource;
import com.simonebakker.simone.addemup.models.Game;

public class Menu extends AppCompatActivity {

    private Button mNewGameButton;
    private Button mResumeGameButton;
    private Button mHighscoresButton;
    private Button mAccountButton;

    private Game mResumeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getSavedGame();
        setViews();
        setOnClicks();
        setResumeButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSavedGame();
        setResumeButton();
    }

    // gets the currently saved game from the db
    private void getSavedGame() {
        DataSource dataSource = new DataSource(Menu.this);
        mResumeGame = dataSource.getCurrentGame();
    }

    // sets resume game button depending on if there's a currently saved game
    private void setResumeButton() {
        if (mResumeGame.getmProgress() == -1) {
            mResumeGameButton.setText(getString(R.string.no_resume_game));
            mResumeGameButton.setEnabled(false);
        } else {
            mResumeGameButton.setText(getString(R.string.resume_game));
            mResumeGameButton.setEnabled(true);
        }
    }

    private void setViews() {
        mNewGameButton = findViewById(R.id.new_game_btn);
        mResumeGameButton = findViewById(R.id.resume_game_btn);
        mHighscoresButton = findViewById(R.id.highscores_btn);
        mAccountButton = findViewById(R.id.account_button);
    }

    private void setOnClicks() {
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
            }
        });

        mResumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeGame();
            }
        });

        mHighscoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, HighScores.class);
                startActivity(intent);
            }
        });

        mAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void newGame() {
        Game newGame = new Game(-1, 0, 1);
        Intent intent = new Intent(Menu.this, PreLevel.class);
        intent.putExtra("game", newGame);
        startActivity(intent);
    }

    private void resumeGame() {
        Intent intent = new Intent(Menu.this, PreLevel.class);
        intent.putExtra("game", mResumeGame);
        startActivity(intent);
    }
}
