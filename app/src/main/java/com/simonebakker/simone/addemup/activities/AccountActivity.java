package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonebakker.simone.addemup.R;
import com.simonebakker.simone.addemup.models.Game;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    private String mName;
    private String mEmail;
    private Uri mPhotoUrl;

    private LinearLayout mEditNameLayout;
    private LinearLayout mNameLayout;
    private TextView mNameView;
    private EditText mNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setToolbar();
        getUser();
        setVariables();
        fillViews();
        setOnClicks();
    }

    @Override
    protected void onDestroy() {
        if (mPhotoUrl != null) {
            Picasso.with(this).invalidate(mPhotoUrl);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                backToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle(getString(R.string.account));
        }
    }

    private void getUser() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mName = mUser.getDisplayName();
            mEmail = mUser.getEmail();
            mPhotoUrl = mUser.getPhotoUrl();
        } else {
            backToLogin();
        }
    }

    private void setVariables() {
        mEditNameLayout = findViewById(R.id.name_edit_layout);
        mNameLayout = findViewById(R.id.name_layout);
        mNameView = findViewById(R.id.name_view);
        mNameEdit = findViewById(R.id.name_edit);
    }

    private void fillViews() {
        mNameView.setText(mName);

        TextView emailView = findViewById(R.id.email_view);
        emailView.setText(mEmail);

        bindImage();
        getStats();
    }

    private void fillStats(int amountOfGames, int highestScore, int highestLevel) {
        TextView highestScoreView = findViewById(R.id.highest_score);
        TextView gamesPlayedView = findViewById(R.id.amount_of_games);
        TextView highestLevelView = findViewById(R.id.highest_level);

        highestScoreView.setText(getString(R.string.highest_score, highestScore));
        gamesPlayedView.setText(getString(R.string.games_played, amountOfGames));
        highestLevelView.setText(getString(R.string.highest_level, highestLevel));
    }

    private void bindImage() {
        ImageView imageView = findViewById(R.id.user_img);
        Picasso.with(this)
                .load(mPhotoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar)
                .fit()
                .centerInside()
                .into(imageView);
        imageView.setImageURI(mPhotoUrl);

        // TODO: make clickable and use camera/files phone to add new image
    }

    private void setOnClicks() {
        ImageButton editNameButton = findViewById(R.id.edit_name_button);
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameLayout.setVisibility(View.GONE);
                mEditNameLayout.setVisibility(View.VISIBLE);
                mNameEdit.setEnabled(true);
            }
        });

        ImageButton saveNameButton = findViewById(R.id.save_name_button);
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveName();
            }
        });
    }

    private void saveName() {
        final String NEW_NAME = mNameEdit.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(NEW_NAME)
                .build();

        mUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mNameEdit.setEnabled(false);
                            mEditNameLayout.setVisibility(View.GONE);
                            mNameLayout.setVisibility(View.VISIBLE);
                            mNameView.setText(NEW_NAME);
                        }
                    }
                });
    }

    private void getStats() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("game")
                .orderByChild("userID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int amountOfGames = 0;
                int highestScore = 0;
                int highestLevel = 0;

                for (DataSnapshot gameSnapShot : dataSnapshot.getChildren()) {
                    amountOfGames++;
                    if (Integer.parseInt(gameSnapShot.child("score").getValue().toString()) > highestScore) {
                        highestScore = Integer.parseInt(gameSnapShot.child("score").getValue().toString());
                    }
                    if (Integer.parseInt(gameSnapShot.child("level").getValue().toString()) > highestLevel) {
                        highestLevel = Integer.parseInt(gameSnapShot.child("level").getValue().toString());
                    }
                }

                fillStats(amountOfGames, highestScore, highestLevel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Errorrr", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
