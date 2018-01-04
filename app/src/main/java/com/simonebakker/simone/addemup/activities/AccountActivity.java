package com.simonebakker.simone.addemup.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

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
import com.simonebakker.simone.addemup.Manifest;
import com.simonebakker.simone.addemup.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AccountActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseUser mUser;

    private String mName;
    private String mEmail;
    private Uri mPhotoUrl;

    private LinearLayout mEditNameLayout;
    private LinearLayout mNameLayout;
    private TextView mNameView;
    private EditText mNameEdit;
    private ImageView mImageView;

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

    // called when the camera is used to take a new account picture in editImage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            saveAccountImage(imageBitmap);
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
        mImageView = findViewById(R.id.user_img);
        Picasso.with(this)
                .load(mPhotoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar)
                .fit()
                .centerInside()
                .into(mImageView);
        mImageView.setImageURI(mPhotoUrl);
    }

    private void setOnClicks() {
        ImageButton editNameButton = findViewById(R.id.edit_name_button);
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameLayout.setVisibility(View.GONE);
                mEditNameLayout.setVisibility(View.VISIBLE);
                mNameEdit.setText(mName);
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
        setEditImage();
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

    private void setEditImage() {
        // check whether the device has a camera function
        ImageView editImage = findViewById(R.id.edit_image);
        PackageManager packageManager = AccountActivity.this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePicture();
                }
            });
        } else {
            editImage.setVisibility(View.GONE);
        }
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void saveAccountImage(Bitmap imageBitmap) {
        final Uri NEW_URL = convertBitmapToUrl(imageBitmap);
//        final Uri NEW_URL = Uri.parse("http://cimg.tvgcdn.net/i/2017/07/19/8da32aef-513d-46c7-9497-8bf58e18b22e/9d69eb37ae4443d6915957ee46e565fe/170718-sarah-hyland-shadowhunters.png");

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(NEW_URL)
                .build();

        mUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mPhotoUrl = NEW_URL;
                            bindImage();
                        }
                    }
                });

    }

    private Uri convertBitmapToUrl(Bitmap imageBitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ContentResolver contentResolver = this.getContentResolver();
        String path = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null);
        if (path == null) {
            Toast.makeText(this, "image failed to be stored", Toast.LENGTH_LONG).show();
            return Uri.parse("http://images-production.global.ssl.fastly.net/uploads/posts/off_site_promo_image/127357/sarah-hyland-plastic-surgery.jpg");
        } else {
            return Uri.parse(path);
        }
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
