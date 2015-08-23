package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ekulf.spotifystreamer.service.AudioService;
import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import org.parceler.Parcels;

import java.util.List;

public class PlayerActivity
        extends SpotifyStreamerActivity {

    private static final String EXTRA_TRACKS = "PlayerActivity:TRACKS";
    private static final String EXTRA_START_IDX = "PlayerActivity:START_IDX";
    private static final String EXTRA_PLAY = "PlayerActivity:PLAY";

    private PlayerFragment mPlayerFragment;

    public static Intent createIntent(
            Context context,
            List<TrackViewModel> tracks,
            int startIdx,
            boolean play) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_TRACKS, Parcels.wrap(tracks));
        intent.putExtra(EXTRA_START_IDX, startIdx);
        intent.putExtra(EXTRA_PLAY, play);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPlayerFragment =
                (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
        if (mPlayerFragment == null) {
            List<TrackViewModel> tracks =
                    Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_TRACKS));

            int startIdx = getIntent().getIntExtra(EXTRA_START_IDX, 0);
            boolean play = getIntent().getBooleanExtra(EXTRA_PLAY, false);

            mPlayerFragment = PlayerFragment.newInstance(tracks, startIdx, play);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.container,
                            mPlayerFragment,
                            "PlayerFragment")
                    .commit();
        }
    }

    @Override
    protected boolean showPlayMenu() {
        return false;
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
}
