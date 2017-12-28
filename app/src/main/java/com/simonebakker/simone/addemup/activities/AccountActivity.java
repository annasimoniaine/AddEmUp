package com.simonebakker.simone.addemup.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.simonebakker.simone.addemup.R;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    private String mName;
    private String mEmail;
    private Uri mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setToolbar();
        getUser();
        fillViews();
    }

    @Override
    protected void onDestroy() {
        if (mPhotoUrl != null) {
            Picasso.with(this).invalidate(mPhotoUrl);
        }
        super.onDestroy();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mName = user.getDisplayName();
            mEmail = user.getEmail();
            mPhotoUrl = user.getPhotoUrl();
        } else {
            backToLogin();
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

    private void fillViews() {
        TextView nameView = findViewById(R.id.name_view);
        nameView.setText(mName);

        TextView emailView = findViewById(R.id.email_view);
        emailView.setText(mEmail);
        bindImage();
    }

    private void bindImage() {
        ImageView imageView = findViewById(R.id.user_img);
        Picasso.with(this)
                .load(mPhotoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar)
                .fit()
                .centerInside()
                .into(imageView);
        imageView.setImageURI(mPhotoUrl);
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
//                Toast.makeText(this, getString(R.string.logged_out_success), Toast.LENGTH_SHORT).show();
                backToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
