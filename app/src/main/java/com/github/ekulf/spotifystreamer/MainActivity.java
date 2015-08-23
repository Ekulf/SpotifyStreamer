package com.github.ekulf.spotifystreamer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ekulf.spotifystreamer.service.AudioService;
import com.github.ekulf.spotifystreamer.viewmodels.ArtistViewModel;
import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MainActivity
        extends SpotifyStreamerActivity
        implements ArtistListFragment.ArtistListCallback,
        TrackListFragment.TrackListCallback,
        FragmentManager.OnBackStackChangedListener {

    @InjectView(R.id.detail_container)
    @Optional
    View mDetailContainer;

    private boolean mTwoPane;
    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mTwoPane = mDetailContainer != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(ArtistViewModel artist) {
        if (mTwoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.detail_container,
                            TrackListFragment.newInstance(artist.getArtistId()),
                            "TrackListFragment")
                    .commit();
        } else {
            startActivity(TrackListActivity.createIntent(this, artist));
        }
    }

    @Override
    public void onTrackSelected(List<TrackViewModel> tracks, int startTrack) {
        mPlayerFragment = PlayerFragment.newInstance(tracks, startTrack, true);
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction().addToBackStack(null);
        mPlayerFragment.show(ft, "PlayerFragment");
    }

    @Override
    public void onStateChanged(AudioService.State state) {
        super.onStateChanged(state);
        if (mPlayerFragment != null) {
            mPlayerFragment.onStateChanged(state);
        }
    }

    @Override
    public void onTrackIndexChanged(int idx) {
        super.onTrackIndexChanged(idx);
        if (mPlayerFragment != null) {
            mPlayerFragment.onTrackIndexChanged(idx);
        }
    }

    @Override
    public void onTimeChanged(int currentPosition) {
        super.onTimeChanged(currentPosition);
        if (mPlayerFragment != null) {
            mPlayerFragment.onTimeChanged(currentPosition);
        }
    }

    @Override
    public void onBackStackChanged() {
        mPlayerFragment = null;
    }
}
