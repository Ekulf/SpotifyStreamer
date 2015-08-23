package com.github.ekulf.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
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
        extends DialogFragment {

    private static final String ARG_TRACKS = "PlayerFragment:TRACKS";
    private static final String ARG_START_IDX = "PlayerFragment:START_IDX";
    private static final String ARG_PLAY = "PlayerFragment:PLAY";

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

    private SpotifyStreamerActivity mSpotifyStreamerActivity;
    private List<TrackViewModel> mTracks;
    private int mCurrentTrackIdx;
    private boolean mScrubbing;

    public static PlayerFragment newInstance(
            List<TrackViewModel> tracks,
            int startIdx,
            boolean play) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACKS, Parcels.wrap(tracks));
        args.putInt(ARG_START_IDX, startIdx);
        args.putBoolean(ARG_PLAY, play);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, root);

        mTracks = Parcels.unwrap(getArguments().getParcelable(ARG_TRACKS));

        // Only does this the first time the fragment is loaded.
        if (savedInstanceState == null) {
            mCurrentTrackIdx = getArguments().getInt(ARG_START_IDX);
            mPlayButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mPrevButton.setEnabled(false);
            mSeekBar.setEnabled(false);
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);

            if (getArguments().getBoolean(ARG_PLAY)) {
                AudioService.startNewPlaylist(getActivity(), mCurrentTrackIdx, mTracks);
            } else {
                AudioService service = getAudioService();
                AudioService.State state;
                if (service != null) {
                    state = getAudioService().getState();
                    mSeekBar.setMax(service.getCurrentDuration());
                    onTimeChanged(service.getCurrentTrackIndex());
                } else {
                    state = AudioService.State.Stopped;
                }

                onStateChanged(state);
            }
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mScrubbing = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AudioService audioService = getAudioService();
                if (audioService != null) {
                    audioService.setPosition(seekBar.getProgress());
                }

                mScrubbing = false;
            }
        });

        updateTrackInfo();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSpotifyStreamerActivity = (SpotifyStreamerActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSpotifyStreamerActivity = null;
    }

    @OnClick(R.id.play_button)
    void play() {
        AudioService audioService = getAudioService();
        if (audioService != null) {
            switch (audioService.getState()) {
                case Paused:
                    audioService.playTrack();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case Playing:
                    audioService.pauseTrack();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }
        }
    }

    @OnClick(R.id.prev_button)
    void prev() {
        AudioService audioService = getAudioService();
        if (audioService != null) {
            audioService.playPreviousTrack();
        }
    }

    @OnClick(R.id.next_button)
    void next() {
        AudioService audioService = getAudioService();
        if (audioService != null) {
            audioService.playNextTrack();
        }
    }

    public void onTimeChanged(int currentPosition) {
        if (!mScrubbing && mSeekBar != null) {
            mSeekBar.setProgress(currentPosition);
        }
    }

    public void onStateChanged(AudioService.State state) {
        switch (state) {
            case Playing:
                mSeekBar.setEnabled(true);
                mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                mPlayButton.setEnabled(true);

                if (mCurrentTrackIdx > 0) {
                    mPrevButton.setEnabled(true);
                } else {
                    mPrevButton.setEnabled(false);
                }

                if (mCurrentTrackIdx < mTracks.size() - 1) {
                    mNextButton.setEnabled(true);
                } else {
                    mNextButton.setEnabled(false);
                }

                AudioService audioService = getAudioService();
                if (audioService != null) {
                    mSeekBar.setMax(audioService.getCurrentDuration());
                }

                break;
            case Paused:
                mSeekBar.setEnabled(true);
                mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                mPlayButton.setEnabled(true);
                if (mCurrentTrackIdx > 0) {
                    mPrevButton.setEnabled(true);
                } else {
                    mPrevButton.setEnabled(false);
                }

                if (mCurrentTrackIdx < mTracks.size() - 1) {
                    mNextButton.setEnabled(true);
                } else {
                    mNextButton.setEnabled(false);
                }

                break;
            default:
                mSeekBar.setEnabled(false);
                mPlayButton.setEnabled(false);
                mNextButton.setEnabled(false);
                mPrevButton.setEnabled(false);
                break;
        }
    }

    public void onTrackIndexChanged(int idx) {
        mCurrentTrackIdx = idx;
        updateTrackInfo();
        AudioService.State state;
        if (getAudioService() != null) {
            state = getAudioService().getState();
        } else {
            state = AudioService.State.Stopped;
        }

        onStateChanged(state);
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

    private AudioService getAudioService() {
        if (mSpotifyStreamerActivity != null) {
            return mSpotifyStreamerActivity.getAudioService();
        }

        return null;
    }
}
