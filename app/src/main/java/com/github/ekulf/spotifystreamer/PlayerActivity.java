package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import org.parceler.Parcels;

import java.util.List;

public class PlayerActivity extends BaseActivity {

    private static final String EXTRA_TRACKS = "PlayerActivity:TRACKS";
    private static final String EXTRA_START_IDX = "PlayerActivity:START_IDX";

    public static Intent createIntent(Context context, List<TrackViewModel> tracks, int startIdx) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_TRACKS, Parcels.wrap(tracks));
        intent.putExtra(EXTRA_START_IDX, startIdx);
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

        if (savedInstanceState == null) {
            List<TrackViewModel> tracks =
                    Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_TRACKS));

            int startIdx = getIntent().getIntExtra(EXTRA_START_IDX, 0);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.container,
                            PlayerFragment.newInstance(tracks, startIdx))
                    .commit();
        }
    }
}
