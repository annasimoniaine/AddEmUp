package com.simonebakker.simone.addemup.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import com.simonebakker.simone.addemup.models.ShakeDetector;

import java.util.HashMap;
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

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

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
        setOnShake();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registers the Session Manager Listener
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Unregisters the Sensor Manager Listener
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // blocks the back button
    }

    /**
     * Sets the view variables to the layout elements
     */
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

    /**
     * Sets the variables for this level
     */
    private void setLevelVariables() {
        mPointsLevel = 0;
        mLevel = new Level(mGame.getmProgress());
    }

    /**
     * Sets a new goal and updates the views, called when answer is submitted
     */
    private void setGoal() {
        setGameVariables();
        fillViews();
    }

    /**
     * Chooses 6 random numbers in the mLevel's range
     * Makes the goal by adding some of the numbers
     * Shuffles the array
     */
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

    /**
     * Shuffles the array and refills numbers in the button views
     * Shows the animation of changing the color to notify the shuffle
     * Called on shake
     */
    private void shuffleNumberViews() {
        for (ToggleButton btn : mNumberButtons) {
            final ToggleButton BTN = btn;
            final float defaultX = BTN.getX();
            BTN.setChecked(false);
            BTN.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            BTN.animate().translationXBy(50).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    BTN.setX(defaultX);
                    BTN.setBackgroundColor(getColor(R.color.colorPrimary));
                }
            });
        }
        mButtonsClicked = 0;
        mCurrentSum = 0;

        shuffleArray(mNumbers);
        int i = 0;
        for (ToggleButton btn : mNumberButtons) {
            btn.setText(String.valueOf(mNumbers[i]));
            i++;
        }
    }

    /**
     * Fills the text views and the buttons
     */
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

    /**
     * Sets the onclick listeners for the buttons and the submit button
     * Also starts out the submit button as disabled
     */
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

    /**
     * Sets up the onshake listener
     */
    private void setOnShake() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {
                shuffleNumberViews();
            }
        });
    }

    /**
     * Checks if the answer is correct, shows feedback, gives points, sets new goal
     */
    private void submitAnswer() {
        if (mGoalNumber != mCurrentSum) {
            mRightWrongImage.setImageResource(R.drawable.cross);
        } else {
            mRightWrongImage.setImageResource(R.drawable.check);
            mPointsLevel += mLevel.getmPointsForCorrect();
        }
        showAnimation();
        setGoal();
    }

    /**
     * Shows check/cross image, moves it to the upper right corner, hides it, replaces it
     * Animated using a ViewPropertyAnimator
     */
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

    /**
     * Sets on checked change listener for button
     * Keeps track of how many buttons are clicked on and what their sum is and highlights them
     * @param btn: the ToggleButton that the listener is attached to
     */
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

    /**
     * Sets countdown for the length of the mLevel
     * Shows the timer counting down in mm:ss format
     * Checks if user got enough points and ends mLevel when countdown is done
     */
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

    /**
     * Called when not enough points at end of mLevel
     * Saves the game as a new highscore in firebase
     * Also clears the saved game from the sqlite db
     * Starts the GameOver activity
     */
    private void failLevel() {
        mGame.setCurrentDate();
        saveHighscore();

        DataSource dataSource = new DataSource(GameActivity.this);
        dataSource.removePrevious();

        Intent intent = new Intent(GameActivity.this, GameOver.class);
        intent.putExtra("game", mGame);
        intent.putExtra("points", mPointsLevel);
        intent.putExtra("needed_points", mLevel.getmNeededPoints());
        startActivity(intent);
        finish();
    }

    /**
     * Called when enough points to pass the mLevel
     * Ups the progress by one mLevel
     * Starts the EndOfLevel activity
     */
    private void passLevel() {
        mGame.setmPoints(mGame.getmPoints() + mPointsLevel);
        mGame.setmProgress(mGame.getmProgress() + 1);
        Intent intent = new Intent(GameActivity.this, EndOfLevel.class);
        intent.putExtra("game", mGame);
        intent.putExtra("points", mPointsLevel);
        startActivity(intent);
        finish();
    }

    /**
     * Saves the game as a high score in firebase
     */
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

    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
