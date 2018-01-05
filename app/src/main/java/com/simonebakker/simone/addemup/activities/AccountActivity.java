package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import com.simonebakker.simone.addemup.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private String mCurrentPhotoPath;

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

    /**
     * Called when the camera is used to take a new account picture in editImage
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
        }
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
            ab.setHomeButtonEnabled(true);
            ab.setTitle(getString(R.string.account));
        }
    }

    /**
     * Gets the currently logged in user from firebase Auth
     */
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

    /**
     * Sets the layout/view variables
     */
    private void setVariables() {
        mEditNameLayout = findViewById(R.id.name_edit_layout);
        mNameLayout = findViewById(R.id.name_layout);
        mNameView = findViewById(R.id.name_view);
        mNameEdit = findViewById(R.id.name_edit);
    }

    /**
     * Fills the text views and initialises setting the image & stats
     */
    private void fillViews() {
        mNameView.setText(mName);

        TextView emailView = findViewById(R.id.email_view);
        emailView.setText(mEmail);

        bindImage();
        getStats();
    }

    /**
     * Fills the TextViews with statistics about the user's played games
     * @param amountOfGames: total amount of games user has played
     * @param highestScore: highest score user has achieved
     * @param highestLevel: highest level user has reached
     */
    private void fillStats(int amountOfGames, int highestScore, int highestLevel) {
        TextView highestScoreView = findViewById(R.id.highest_score);
        TextView gamesPlayedView = findViewById(R.id.amount_of_games);
        TextView highestLevelView = findViewById(R.id.highest_level);

        highestScoreView.setText(getString(R.string.highest_score, highestScore));
        gamesPlayedView.setText(getString(R.string.games_played, amountOfGames));
        highestLevelView.setText(getString(R.string.highest_level, highestLevel));
    }

    /**
     * Binds profile picture to the ImageView with Picasso
     * Called on create & when setting new image with camera
     */
    private void bindImage() {
        ImageView imageView = findViewById(R.id.user_img);
        Picasso.with(this)
                .load(mPhotoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar)
                .fit()
                .centerInside()
                .into(imageView);
    }

    /**
     * Sets the onclicks for the buttons
     */
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

    /**
     * Saves the changed name to firebase Auth
     */
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

    /**
     * Sets up the onclick listener for adding a new image with camera (if device has camera)
     */
    private void setEditImage() {
        // check whether the device has a camera function
        ImageView editImage = findViewById(R.id.edit_image);
        PackageManager packageManager = AccountActivity.this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });
        } else {
            editImage.setVisibility(View.GONE);
        }
    }

    /**
     * Creates file where new picture should go and starts intent to take picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "IOException", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.simonebakker.simone.addemup.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Creates a temporary image file in with a unique name
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Adds the image to the device's picture gallery
     * and passes the path when calling saveAccountImage
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        saveAccountImage(contentUri);
    }

    /**
     * Saves image to the current user's profile in firebase Auth
     * @param newUrl: the Uri of the image to save
     */
    private void saveAccountImage(Uri newUrl) {
        final Uri NEW_URL = newUrl;

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

    /**
     * Gets the user's game statistics from firebase
     */
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

    /**
     * Starts the Login activity
     */
    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
