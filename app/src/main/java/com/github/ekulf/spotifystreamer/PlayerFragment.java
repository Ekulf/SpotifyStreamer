package com.github.ekulf.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerFragment extends Fragment {

    private static final String ARG_TRACK = "PlayerFragment:TRACK";
    @InjectView(R.id.artist_name)
    TextView mArtistName;
    @InjectView(R.id.album_name)
    TextView mAlbumName;
    @InjectView(R.id.album_image)
    ImageView mAlbumImage;
    @InjectView(R.id.track_name)
    TextView mTrackName;

    public static PlayerFragment newInstance(TrackViewModel track) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACK, Parcels.wrap(track));
        fragment.setArguments(args);
        return fragment;
    }

    private TrackViewModel mTrackViewModel;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);
        mTrackViewModel = Parcels.unwrap(getArguments().getParcelable(ARG_TRACK));
        ButterKnife.inject(this, root);
        mArtistName.setText(mTrackViewModel.getArtistName());
        mAlbumName.setText(mTrackViewModel.getAlbumName());
        Picasso
                .with(getActivity())
                .load(mTrackViewModel.getLargeImageUrl())
                .centerInside()
                .resizeDimen(R.dimen.player_image_width, R.dimen.player_image_height)
                .into(mAlbumImage);

        mTrackName.setText(mTrackViewModel.getTrackName());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
