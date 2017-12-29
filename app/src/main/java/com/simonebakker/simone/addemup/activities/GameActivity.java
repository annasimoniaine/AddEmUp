package com.simonebakker.simone.addemup.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

    private TextView mLevelView;
    private TextView mPointsView;
    private TextView mTimeView;
    private TextView mGoalView;
    private TextView mExplanationView;

    private ImageView mRightWrongImage;

    private Button mSubmitBtn;
    private ToggleButton[] mNumberButtons;

    private Game mGame;
    private Level mLevel;

    private int[] mNumbers;
    private int mGoalNumber;
    private int mCurrentSum;
    private int mButtonsClicked;

    private int mPointsLevel;

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
        mLevelView = findViewById(R.id.level_view);
        mPointsView = findViewById(R.id.points_view);
        mTimeView = findViewById(R.id.time_view);
        mGoalView = findViewById(R.id.goal_view);
        mExplanationView = findViewById(R.id.explanation_view);
        mRightWrongImage = findViewById(R.id.rightWrongImage);

        mSubmitBtn = findViewById(R.id.submit_btn);
        mNumberButtons = new ToggleButton[]{
                findViewById(R.id.number_1),
                findViewById(R.id.number_2),
                findViewById(R.id.number_3),
                findViewById(R.id.number_4),
                findViewById(R.id.number_5),
                findViewById(R.id.number_6)
        };
    }

    private void setLevelVariables() {
        mPointsLevel = 0;
        mLevel = new Level(mGame.getmProgress());
    }

    // sets a new goal and updates the views, called when answer is submitted
    private void setGoal() {
        setGameVariables();
        fillViews();
    }

    // chooses 6 random numbers in the mLevel's range
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
            mNumbers[i] = rand.nextInt(mLevel.getmMaxRange()) + mLevel.getmMinRange();
        }

        mGoalNumber = 0;
        for (int i = 0; i < mLevel.getmAmountOfNumbers(); i++) {
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
        mExplanationView.setText(getString(R.string.instruction, String.valueOf(mLevel.getmAmountOfNumbers()), String.valueOf(mGoalNumber)));
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
            mPointsLevel += mLevel.getmPointsForCorrect();
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
                    if (mButtonsClicked > mLevel.getmAmountOfNumbers()) {
                        disableSubmitButton();
                    }
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    mCurrentSum -= parseInt(view.getText().toString());
                    mButtonsClicked--;
                    if (mButtonsClicked < mLevel.getmAmountOfNumbers()) {
                        disableSubmitButton();
                    }
                }

                if (mButtonsClicked == mLevel.getmAmountOfNumbers()) {
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
        mSubmitBtn.setText(getString(R.string.wrong_amount, String.valueOf(mLevel.getmAmountOfNumbers())));
        mSubmitBtn.setEnabled(false);
    }

    // sets countdown for the length of the mLevel
    // shows the timer counting down in mm:ss format
    // checks if user got enough points and ends mLevel when countdown is done
    private void setCountdown() {
        new CountDownTimer(mLevel.getmLevelTime(), 1000) {
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
                if (mPointsLevel >= mLevel.getmNeededPoints()) {
                    passLevel();
                } else {
                    failLevel();
                }
            }
        }.start();
    }

    // called when not enough points at end of mLevel
    // if enough points throughout the game, sets as highscore in db
    // else removes the game from db
    // starts the GameOver activity
    private void failLevel() {
        mGame.setCurrentDate();
        saveHighscore();

        // TODO: better way of checking if it's currently saved game, if it is remove game from here
        if (mGame.getmID() == -1) {
            DataSource dataSource = new DataSource(GameActivity.this);
            dataSource.removeGame(mGame.getmID());
        }

        Intent intent = new Intent(GameActivity.this, GameOver.class);
        intent.putExtra("game", mGame);
        intent.putExtra("points", mPointsLevel);
        intent.putExtra("needed_points", mLevel.getmNeededPoints());
        startActivity(intent);
        finish();
    }

    // call when enough points to pass the mLevel
    // ups the progress by one mLevel
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

    // saves the game as a high score in firebase
    private void saveHighscore() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("game");

        HashMap<String, Object> valuesToPut = new HashMap<>();
        valuesToPut.put("score", mGame.getmPoints());
        valuesToPut.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        valuesToPut.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        valuesToPut.put("level", mGame.getmProgress());
        valuesToPut.put("date", mGame.getmDate());

        databaseReference.push().setValue(valuesToPut);
    }
}
