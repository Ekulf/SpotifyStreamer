package com.github.ekulf.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PlayerFragment
        extends DialogFragment
        implements AudioService.AudioServiceListener {

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
    @InjectView(R.id.prev_button)
    ImageButton mPrevButton;
    @InjectView(R.id.next_button)
    ImageButton mNextButton;


    public static PlayerFragment newInstance(List<TrackViewModel> tracks, int startIdx) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACKS, Parcels.wrap(tracks));
        args.putInt(ARG_START_IDX, startIdx);
        fragment.setArguments(args);
        return fragment;
    }

    private List<TrackViewModel> mTracks;
    private int mCurrentTrackIdx;
    private AudioService mAudioService;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, root);

        mTracks = Parcels.unwrap(getArguments().getParcelable(ARG_TRACKS));

        getActivity().bindService(
                new Intent(getActivity(), AudioService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);

        // Only does this the first time the fragment is loaded.
        if (savedInstanceState == null) {
            mCurrentTrackIdx = getArguments().getInt(ARG_START_IDX);
            mPlayButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mPrevButton.setEnabled(false);
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            AudioService.startNewPlaylist(getActivity(), mCurrentTrackIdx, mTracks);
        }

        updateTrackInfo();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        if (mAudioService != null) {
            mAudioService.setListener(null);
        }

        getActivity().unbindService(mServiceConnection);
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

    @Override
    public void onTimeChanged(int currentPosition) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(currentPosition);
        }
    }

    @Override
    public void onStateChanged(AudioService.State state) {
        switch (state) {
            case Playing:
                mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                mPlayButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(true);
                mSeekBar.setMax(mAudioService.getCurrentDuration());
                break;
            case Paused:
                mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                mPlayButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(true);
                break;
            default:
                mPlayButton.setEnabled(false);
                mNextButton.setEnabled(false);
                mPrevButton.setEnabled(false);
                break;
        }
    }

    @Override
    public void onTrackIndexChanged(int idx) {
        mCurrentTrackIdx = idx;
        updateTrackInfo();
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
            mAudioService.setListener(PlayerFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mAudioService != null) {
                mAudioService.setListener(null);
                mAudioService = null;
            }
        }
    };
}
