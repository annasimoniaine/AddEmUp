package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        setButtonVariables();
        setOnClicks();
        setResumeButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSavedGame();
        setResumeButton();
    }

    /**
     * Gets the currently saved game from the (sqlite) db
     */
    private void getSavedGame() {
        DataSource dataSource = new DataSource(Menu.this);
        mResumeGame = dataSource.getCurrentGame();
    }

    /**
     * Sets resume game button depending on whether there's a currently saved game
     */
    private void setResumeButton() {
        if (mResumeGame.getmProgress() == -1) {
            mResumeGameButton.setText(getString(R.string.no_resume_game));
            mResumeGameButton.setEnabled(false);
        } else {
            mResumeGameButton.setText(getString(R.string.resume_game));
            mResumeGameButton.setEnabled(true);
        }
    }

    /**
     * Sets the variables for the buttons
     */
    private void setButtonVariables() {
        mNewGameButton = findViewById(R.id.new_game_btn);
        mResumeGameButton = findViewById(R.id.resume_game_btn);
        mHighscoresButton = findViewById(R.id.highscores_btn);
        mAccountButton = findViewById(R.id.account_button);
    }

    /**
     * Sets the onclicks for the buttons
     */
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

    /**
     * Starts a new game
     */
    private void newGame() {
        Game newGame = new Game(-1, 0, 1);
        Intent intent = new Intent(Menu.this, PreLevel.class);
        intent.putExtra("game", newGame);
        startActivity(intent);
    }

    /**
     * Starts the currently saved game
     */
    private void resumeGame() {
        Intent intent = new Intent(Menu.this, PreLevel.class);
        intent.putExtra("game", mResumeGame);
        startActivity(intent);
    }
}
