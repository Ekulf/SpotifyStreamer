package com.github.ekulf.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class LicensesActivity extends SpotifyStreamerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_activity);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LicensesFragment())
                    .commit();
        }
    }
}
