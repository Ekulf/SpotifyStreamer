package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import org.parceler.Parcels;

public class PlayerActivity extends BaseActivity {

    private static final String EXTRA_TRACK = "PlayerActivity:TRACK";

    public static Intent createIntent(Context context, TrackViewModel track) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_TRACK, Parcels.wrap(track));
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
            TrackViewModel trackViewModel =
                    Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_TRACK));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.container,
                            PlayerFragment.newInstance(trackViewModel))
                    .commit();
        }
    }
}
