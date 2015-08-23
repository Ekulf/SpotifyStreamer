package com.github.ekulf.spotifystreamer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ekulf.spotifystreamer.service.AudioService;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class SpotifyStreamerActivity
        extends AppCompatActivity
        implements AudioService.AudioServiceListener {

    private Toolbar mToolbar;
    private boolean mIsPlaying;
    private AudioService mAudioService;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public AudioService getAudioService() {
        return mAudioService;
    }

    protected boolean showPlayMenu() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showPlayMenu() && mIsPlaying) {
            getMenuInflater().inflate(R.menu.play_menu, menu);
            menu.findItem(R.id.action_player)
                    .setIcon(
                            new IconDrawable(this, FontAwesomeIcons.fa_play_circle_o)
                                    .colorRes(android.R.color.white)
                                    .actionBarSize());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_player) {
            if (mAudioService != null) {
                startActivity(
                        PlayerActivity.createIntent(this,
                                mAudioService.getTracks(),
                                mAudioService.getCurrentTrackIndex(),
                                false));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(
                new Intent(this, AudioService.class),
                mServiceConnection,
                0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAudioService != null) {
            mAudioService.setListener(null);
        }

        unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            mAudioService = binder.getService();
            mAudioService.setListener(SpotifyStreamerActivity.this);
            onStateChanged(mAudioService.getState());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mAudioService != null) {
                mAudioService.setListener(null);
                mAudioService = null;
            }
        }
    };

    @Override
    public void onTimeChanged(int currentPosition) {
    }

    @Override
    public void onStateChanged(AudioService.State state) {
        switch (state) {
            case Retrieving:
            case Playing:
            case Paused:
                mIsPlaying = true;
                break;
            default:
                mIsPlaying = false;
                break;
        }

        if (showPlayMenu()) {
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onTrackIndexChanged(int idx) {
    }
}
