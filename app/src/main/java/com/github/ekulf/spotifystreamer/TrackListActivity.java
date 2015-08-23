package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ekulf.spotifystreamer.viewmodels.ArtistViewModel;
import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import org.parceler.Parcels;

import java.util.List;

public class TrackListActivity
        extends SpotifyStreamerActivity
        implements TrackListFragment.TrackListCallback {

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

    @Override
    public void onTrackSelected(List<TrackViewModel> tracks, int startTrack) {
        startActivity(PlayerActivity.createIntent(this, tracks, startTrack, true));
    }
}
