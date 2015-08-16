package com.github.ekulf.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

public class AudioService
        extends Service
        implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener {

    private static final String ACTION_START =
            "com.github.ekulf.spotifystreamer.service.action.START";
    private static final String ACTION_PLAY =
            "com.github.ekulf.spotifystreamer.service.action.PLAY";
    private static final String ACTION_PAUSE =
            "com.github.ekulf.spotifystreamer.service.action.PAUSE";
    private static final String ACTION_NEXT =
            "com.github.ekulf.spotifystreamer.service.action.NEXT";
    private static final String ACTION_PREVIOUS =
            "com.github.ekulf.spotifystreamer.service.action.PREVIOUS";

    private static final String EXTRA_TRACKS =
            "com.github.ekulf.spotifystreamer.service.TRACKS";
    private static final String EXTRA_START_TRACK_IDX =
            "com.github.ekulf.spotifystreamer.service.START_TRACK";


    private MediaPlayer mPlayer;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private List<TrackViewModel> mTracks;
    private int mCurrentTrack;

    public enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    }

    private State mState = State.Retrieving;


    public static void startNewPlaylist(Context context, int startingIdx, List<TrackViewModel> tracks) {
        Intent starter = new Intent(context, AudioService.class);
        starter.setAction(ACTION_START);
        starter.putExtra(EXTRA_START_TRACK_IDX, startingIdx);
        starter.putExtra(EXTRA_TRACKS, Parcels.wrap(tracks));
        context.startService(starter);
    }

    public static void pausePlaylist(Context context) {
        Intent starter = new Intent(context, AudioService.class);
        starter.setAction(ACTION_PAUSE);
        context.startService(starter);
    }

    public static void playNextTrack(Context context) {
        Intent starter = new Intent(context, AudioService.class);
        starter.setAction(ACTION_NEXT);
        context.startService(starter);
    }

    public static void playPreviousTrack(Context context) {
        Intent starter = new Intent(context, AudioService.class);
        starter.setAction(ACTION_PREVIOUS);
        context.startService(starter);
    }

    public static void resumePlaylist(Context context) {
        Intent starter = new Intent(context, AudioService.class);
        starter.setAction(ACTION_PLAY);
        context.startService(starter);
    }

    public AudioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_START)) processStartRequest(intent);
        else if (action.equals(ACTION_PLAY)) playTrack();
        else if (action.equals(ACTION_PAUSE)) pauseTrack();
        else if (action.equals(ACTION_NEXT)) playNextTrack();
        else if (action.equals(ACTION_PREVIOUS)) playPreviousTrack();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioBinder();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = State.Playing;
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrentTrack++;
        if (mCurrentTrack < mTracks.size() - 1) {
            playCurrentTrack();
        } else {
            mState = State.Stopped;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void playTrack() {
        mState = State.Playing;
        mPlayer.start();
    }

    public void pauseTrack() {
        mState = State.Paused;
        mPlayer.pause();
    }

    public void playNextTrack() {
        mCurrentTrack++;
        if (mCurrentTrack < mTracks.size() - 1) {
            playCurrentTrack();
        } else {
            mCurrentTrack = mTracks.size() - 1;
        }
    }

    public void playPreviousTrack() {
        mCurrentTrack--;
        if (mCurrentTrack > 0) {
            playCurrentTrack();
        } else {
            mCurrentTrack = 0;
        }
    }

    public State getState() {
        return mState;
    }

    public int getCurrentTrackIndex() {
        return mCurrentTrack;
    }

    private void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        } else {
            mPlayer.reset();
        }
    }

    private void processStartRequest(Intent intent) {
        mCurrentTrack = intent.getIntExtra(EXTRA_START_TRACK_IDX, 0);
        mTracks = Parcels.unwrap(intent.getParcelableExtra(EXTRA_TRACKS));
        playCurrentTrack();
    }

    private void playCurrentTrack() {
        mState = State.Retrieving;
        createMediaPlayerIfNeeded();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mPlayer.setDataSource(this, Uri.parse(mTracks.get(mCurrentTrack).getPreviewUrl()));
        } catch (IOException e) {
            // TODO: Log the error.
        }

        mPlayer.prepareAsync();
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
