package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ekulf.spotifystreamer.viewmodels.ArtistViewModel;

import org.parceler.Parcels;

public class TrackListActivity extends BaseActivity {

    private static final String EXTRA_ARTIST = "TrackListActivity:ARTIST";

    public static Intent createIntent(
            Context context,
            ArtistViewModel artist) {
        Intent intent = new Intent(context, TrackListActivity.class);
        intent.putExtra(EXTRA_ARTIST, Parcels.wrap(artist));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_activity);
        ArtistViewModel artist = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_ARTIST));
        getToolbar().setSubtitle(artist.getArtistName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.container,
                            TrackListFragment.newInstance(artist.getArtistId()))
                    .commit();
        }
    }
}
