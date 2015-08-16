package com.github.ekulf.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.ekulf.spotifystreamer.service.AudioService;
import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PlayerFragment extends DialogFragment {

    private static final String ARG_TRACKS = "PlayerFragment:TRACKS";
    private static final String ARG_START_IDX = "PlayerFragment:START_IDX";

    @InjectView(R.id.artist_name)
    TextView mArtistName;
    @InjectView(R.id.album_name)
    TextView mAlbumName;
    @InjectView(R.id.album_image)
    ImageView mAlbumImage;
    @InjectView(R.id.track_name)
    TextView mTrackName;
    @InjectView(R.id.seekBar)
    SeekBar mSeekBar;
    @InjectView(R.id.play_button)
    ImageButton mPlayButton;


    public static PlayerFragment newInstance(List<TrackViewModel> tracks, int startIdx) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACKS, Parcels.wrap(tracks));
        args.putInt(ARG_START_IDX, startIdx);
        fragment.setArguments(args);
        return fragment;
    }

    private final MediaPlayer mMediaPlayer = new MediaPlayer();
    private ScheduledExecutorService mScheduledExecutorService;
    private List<TrackViewModel> mTracks;
    private int mCurrentTrackIdx;
    private Handler mHandler;
    private AudioService mAudioService;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, root);

//        mHandler = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                updateProgress();
//                return true;
//            }
//        });
//
//        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        mTracks = Parcels.unwrap(getArguments().getParcelable(ARG_TRACKS));


        getActivity().bindService(
                new Intent(getActivity(), AudioService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);

        // Only does this the first time the fragment is loaded.
        if (savedInstanceState == null) {
            mCurrentTrackIdx = getArguments().getInt(ARG_START_IDX);
            mPlayButton.setEnabled(false);
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            AudioService.startNewPlaylist(getActivity(), mCurrentTrackIdx, mTracks);
        }

        updateTrackInfo();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mScheduledExecutorService.shutdown();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity()
                .bindService(
                        new Intent(getActivity(), AudioService.class),
                        mServiceConnection,
                        0);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
        mAudioService = null;
    }

    @OnClick(R.id.play_button)
    void play() {
        if (mAudioService != null) {
            switch (mAudioService.getState()) {
                case Paused:
                    mAudioService.playTrack();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case Playing:
                    mAudioService.pauseTrack();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }
        }

//        mScheduledExecutorService.scheduleWithFixedDelay(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.sendMessage(mHandler.obtainMessage());
//                    }
//                },
//                100,
//                200,
//                TimeUnit.MILLISECONDS);
    }

    @OnClick(R.id.prev_button)
    void prev() {
        if (mAudioService != null) {
            mAudioService.playPreviousTrack();
        }
    }

    @OnClick(R.id.next_button)
    void next() {
        if (mAudioService != null) {
            mAudioService.playNextTrack();
        }
    }

    private void updateProgress() {
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
    }

    private void updateTrackInfo() {
        TrackViewModel track = mTracks.get(mCurrentTrackIdx);
        mArtistName.setText(track.getArtistName());
        mAlbumName.setText(track.getAlbumName());
        Picasso
                .with(getActivity())
                .load(track.getLargeImageUrl())
                .centerInside()
                .resizeDimen(R.dimen.player_image_width, R.dimen.player_image_height)
                .into(mAlbumImage);

        mTrackName.setText(track.getTrackName());
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayButton.setEnabled(true);
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            mAudioService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAudioService = null;
        }
    };
}
