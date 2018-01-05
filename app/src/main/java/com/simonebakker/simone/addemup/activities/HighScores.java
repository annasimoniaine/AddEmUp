package com.simonebakker.simone.addemup.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.adapter.HighScoreItemAdapter;
import com.simonebakker.simone.addemup.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores extends AppCompatActivity {

    private List<Game> mGameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        setToolbar();
        getHighscores();
    }

    /**
     * Sets up the toolbar as ActionBar with up button and title
     */
    public void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.high_scores));
        }
    }

    /**
     * Sets up the RecyclerView for the list of highscores
     */
    private void setRecyclerView() {
        RecyclerView mHighScoresRecyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mHighScoresRecyclerView.setLayoutManager(mLayoutManager);

        HighScoreItemAdapter mAdapter = new HighScoreItemAdapter(this, mGameList);
        mHighScoresRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Gets the list of highscores/games from firebase
     */
    private void getHighscores() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("game").orderByChild("score").limitToLast(25).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Game> gameList = new ArrayList<>();

                for (DataSnapshot gameSnapShot : dataSnapshot.getChildren()) {
                    Game newGame = new Game();
                    newGame.setmPoints(Integer.parseInt(gameSnapShot.child("score").getValue().toString()));
                    newGame.setmName(gameSnapShot.child("name").getValue().toString());
                    newGame.setmProgress(Integer.parseInt(gameSnapShot.child("level").getValue().toString()));
                    newGame.setmDate(gameSnapShot.child("date").getValue().toString());

                    gameList.add(newGame);
                }

                Collections.reverse(gameList);
                mGameList = gameList;
                setRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
