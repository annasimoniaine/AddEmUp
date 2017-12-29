package com.simonebakker.simone.addemup.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.database.DataSource;
import com.simonebakker.simone.addemup.models.Game;
import com.simonebakker.simone.addemup.models.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class GameActivity extends AppCompatActivity {

    private DataSource dataSource;

    private TextView mLevelView;
    private TextView mPointsView;
    private TextView mTimeView;
    private TextView mGoalView;
    private TextView mExplanationView;

    private ImageView mRightWrongImage;

    private Button mSubmitBtn;
    private ToggleButton[] mNumberButtons;

    private Game mGame;
    private Level level;

    private int[] mNumbers;
    private int mGoalNumber;
    private int mCurrentSum;
    private int mButtonsClicked;

    private int mPointsLevel;
    private int lastLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        mGame = (Game) intent.getSerializableExtra("game");

        setViews();
        setLevelVariables();
        setGoal();
        setOnClicks();
        setCountdown();
    }

    // blocks the back button
    @Override
    public void onBackPressed() {
    }

    private void setViews() {
        mLevelView = (TextView) findViewById(R.id.level_view);
        mPointsView = (TextView) findViewById(R.id.points_view);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mGoalView = (TextView) findViewById(R.id.goal_view);
        mExplanationView = (TextView) findViewById(R.id.explanation_view);
        mRightWrongImage = (ImageView) findViewById(R.id.rightWrongImage);

        mSubmitBtn = (Button) findViewById(R.id.submit_btn);
        mNumberButtons = new ToggleButton[]{
                (ToggleButton) findViewById(R.id.number_1),
                (ToggleButton) findViewById(R.id.number_2),
                (ToggleButton) findViewById(R.id.number_3),
                (ToggleButton) findViewById(R.id.number_4),
                (ToggleButton) findViewById(R.id.number_5),
                (ToggleButton) findViewById(R.id.number_6)
        };
    }

    private void setLevelVariables() {
        mPointsLevel = 0;
        level = new Level(mGame.getmProgress());
    }

    // sets a new goal and updates the views, called when answer is submitted
    private void setGoal() {
        setGameVariables();
        fillViews();
    }

    // chooses 6 random numbers in the level's range
    // makes the goal by adding some of the numbers
    // shuffles the array
    private void setGameVariables() {
        int numberOfNumbers = 6;
        for (ToggleButton btn : mNumberButtons) {
            btn.setChecked(false);
        }
        mButtonsClicked = 0;
        mCurrentSum = 0;

        mNumbers = new int[numberOfNumbers];
        for (int i = 0; i < numberOfNumbers; i++) {
            Random rand = new Random();
            mNumbers[i] = rand.nextInt(level.getmMaxRange()) + level.getmMinRange();
        }

        mGoalNumber = 0;
        for (int i = 0; i < level.getmAmountOfNumbers(); i++) {
            mGoalNumber += mNumbers[i];
        }
        shuffleArray(mNumbers);
    }

    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void fillViews() {
        int i = 0;
        for (ToggleButton btn : mNumberButtons) {
            btn.setText(String.valueOf(mNumbers[i]));
            i++;
        }

        mGoalView.setText(String.valueOf(mGoalNumber));
        mExplanationView.setText(getString(R.string.instruction, String.valueOf(level.getmAmountOfNumbers()), String.valueOf(mGoalNumber)));
        mPointsView.setText(getString(R.string.points, String.valueOf(mPointsLevel)));
        mLevelView.setText(getString(R.string.level, String.valueOf(mGame.getmProgress())));
    }

    private void setOnClicks() {
        for (ToggleButton btn : mNumberButtons) {
            onToggleNumber(btn);
        }

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer();
            }
        });

        disableSubmitButton();
    }

    // checks if the answer is correct, shows feedback, gives points, sets new goal
    private void submitAnswer() {
        if (mGoalNumber != mCurrentSum) {
            mRightWrongImage.setImageResource(R.drawable.cross);
            showAnimation();
            setGoal();
        } else {
            mRightWrongImage.setImageResource(R.drawable.check);
            showAnimation();
            mPointsLevel += level.getmPointsForCorrect();
            setGoal();
        }
    }

    // shows check/cross image, moves it to the upper right corner, hides it, replaces it
    // animated using a ViewPropertyAnimator
    private void showAnimation() {
        final float defaultY = mRightWrongImage.getY();
        final float defaultX = mRightWrongImage.getX();
        mRightWrongImage.setVisibility(View.VISIBLE);

        mRightWrongImage.animate().translationYBy(-200).translationXBy(200).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRightWrongImage.setVisibility(View.GONE);
                mRightWrongImage.setY(defaultY);
                mRightWrongImage.setX(defaultX);
            }
        });
    }

    // keeps track of how many buttons are clicked on and what their sum is and highlights them
    private void onToggleNumber(ToggleButton btn) {
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if (isChecked) {
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCurrentSum += parseInt(view.getText().toString());
                    mButtonsClicked++;
                    if (mButtonsClicked > level.getmAmountOfNumbers()) {
                        disableSubmitButton();
                    }
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    mCurrentSum -= parseInt(view.getText().toString());
                    mButtonsClicked--;
                    if (mButtonsClicked < level.getmAmountOfNumbers()) {
                        disableSubmitButton();
                    }
                }

                if (mButtonsClicked == level.getmAmountOfNumbers()) {
                    enableSubmitButton();
                }
            }
        });
    }

    private void enableSubmitButton() {
        mSubmitBtn.setText(getString(R.string.submit));
        mSubmitBtn.setEnabled(true);
    }

    private void disableSubmitButton() {
        mSubmitBtn.setText(getString(R.string.wrong_amount, String.valueOf(level.getmAmountOfNumbers())));
        mSubmitBtn.setEnabled(false);
    }

    // sets countdown for the length of the level
    // shows the timer counting down in mm:ss format
    // checks if user got enough points and ends level when countdown is done
    private void setCountdown() {
        new CountDownTimer(level.getmLevelTime(), 1000) {
            public void onTick(long msLeft) {
                String minutes = String.valueOf(msLeft / 60000);
                if (parseInt(minutes) < 10) {
                    minutes = "0" + minutes;
                }
                String seconds = String.valueOf((msLeft % 60000) / 1000);
                if (parseInt(seconds) < 10) {
                    seconds = "0" + seconds;
                }
                String time = minutes + ":" + seconds;
                mTimeView.setText(getString(R.string.time, time));
            }
            public void onFinish() {
                if (mPointsLevel >= level.getmNeededPoints()) {
                    passLevel();
                } else {
                    failLevel();
                }
            }
        }.start();
    }

    // called when not enough points at end of level
    // if enough points throughout the game, sets as highscore in db
    // else removes the game from db
    // starts the GameOver activity
    private void failLevel() {
        lastLevel = mGame.getmProgress();
        mGame.setmProgress(-1);
        mGame.setCurrentDate();

        dataSource = new DataSource(GameActivity.this);
        boolean newHighScore = newHighScore();
        if (newHighScore) {
            saveHighscore();
        } else if (mGame.getmID() == -1) {
            dataSource.removeGame(mGame.getmID());
        }

        Intent intent = new Intent(GameActivity.this, GameOver.class);
        intent.putExtra("game", mGame);
        intent.putExtra("points", mPointsLevel);
        intent.putExtra("needed_points", level.getmNeededPoints());
        intent.putExtra("last_level", lastLevel);
        intent.putExtra("new_highscore", newHighScore);
        startActivity(intent);
        finish();
    }

    // call when enough points to pass the level
    // ups the progress by one level
    // starts the EndOfLevel activity
    private void passLevel() {
        mGame.setmPoints(mGame.getmPoints() + mPointsLevel);
        mGame.setmProgress(mGame.getmProgress() + 1);
        Intent intent = new Intent(GameActivity.this, EndOfLevel.class);
        intent.putExtra("game", mGame);
        intent.putExtra("points", mPointsLevel);
        startActivity(intent);
        finish();
    }

    // Checks whether the total score for the game is high enough to make the high score list
    public boolean newHighScore() {
        List<Game> highScores = dataSource.getHighScores();
        int highScoreListSize = 8;

        if (highScores.size() == highScoreListSize) {
            Game lastScore = highScores.get(highScoreListSize - 1);
            return mGame.getmPoints() > lastScore.getmPoints();
        }

        return true;
    }

    // saves the game as a high score in db, either by adding or by updating db record
    private void saveHighscore() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");

        HashMap<String, Object> valuesToPut = new HashMap<>();
        valuesToPut.put("score", mGame.getmPoints());
        valuesToPut.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        valuesToPut.put("level", lastLevel);
        valuesToPut.put("date", mGame.getmDate());

        myRef.push().setValue(valuesToPut);
        // points, userID, level (progress), date
        // valuesToPut.put(name_of_row, value);


        // TODO: remove all this when firebase works entirely (except saved game, make that work locally)
        // if id is -1, it's a new game, else it was retrieved from the db, so needs to be updated
        if (mGame.getmID() == -1) {
            int newGameID = dataSource.saveGame(mGame);
            mGame.setmID(newGameID);
        } else {
            dataSource.finishGame(mGame);
        }
    }
}
