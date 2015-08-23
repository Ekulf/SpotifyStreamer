package com.github.ekulf.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ekulf.spotifystreamer.viewmodels.ArtistViewModel;
import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MainActivity
        extends SpotifyStreamerActivity
        implements ArtistListFragment.ArtistListCallback,
        TrackListFragment.TrackListCallback {

    @InjectView(R.id.detail_container)
    @Optional
    View mDetailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

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
        showPlayerDialog(tracks, startTrack, true);
    }
}
