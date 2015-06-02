package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class TrackListActivity extends BaseActivity {

    private static final String EXTRA_ARTIST_ID = "TrackListActivity:ARTIST_ID";
    private static final String EXTRA_ARTIST_NAME = "TrackListActivity:ARTIST_NAME";

    public static Intent createIntent(
            Context context,
            String artistId,
            String artistName) {
        Intent intent = new Intent(context, TrackListActivity.class);
        intent.putExtra(EXTRA_ARTIST_ID, artistId);
        intent.putExtra(EXTRA_ARTIST_NAME, artistName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_activity);
        String artist = getIntent().getStringExtra(EXTRA_ARTIST_NAME);
        getToolbar().setSubtitle(artist);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.container,
                            TrackListFragment.newInstance(getIntent().getStringExtra(EXTRA_ARTIST_ID)))
                    .commit();
        }
    }
}
