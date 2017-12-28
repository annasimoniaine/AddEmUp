package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.adapter.HighScoreItemAdapter;
import com.simonebakker.simone.addemup.database.DataSource;
import com.simonebakker.simone.addemup.models.Game;

import java.util.List;

public class HighScores extends AppCompatActivity {

    private DataSource mDataSource;
    private List<Game> mGameList;
    private RecyclerView mHighScoresRecyclerView;
    private HighScoreItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        mDataSource = new DataSource(HighScores.this);
        mGameList = mDataSource.getHighScores();

        setRecyclerView();
//        setItemTouchHelper();
        setToolbar();
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.high_scores_top));
        }
    }

    private void setRecyclerView() {
        mHighScoresRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mHighScoresRecyclerView.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();

        // lastGameID is used to highlight the high score that was just achieved
        int lastGameID = intent.getIntExtra("lastGameID", -1);

        mAdapter = new HighScoreItemAdapter(this, mGameList, lastGameID);
        mHighScoresRecyclerView.setAdapter(mAdapter);
    }
//
//    private void setItemTouchHelper() {
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
//                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return true;
//            }
//
//            // Remove record from high scores (database & list) on swipe
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                mDataSource.removeGame(mGameList.get(viewHolder.getAdapterPosition()).getmID());
//
//                mGameList.remove(viewHolder.getAdapterPosition());
//                mAdapter.notifyDataSetChanged();
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(mHighScoresRecyclerView);
//    }
}
