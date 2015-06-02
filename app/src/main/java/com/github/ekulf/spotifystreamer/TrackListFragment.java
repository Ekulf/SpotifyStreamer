package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ekulf.spotifystreamer.viewmodels.TrackViewModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;

public class TrackListFragment extends ListFragment {
    private static final String LOG_TAG = TrackListFragment.class.getSimpleName();
    private SpotifyService mSpotifyService;

    private static final String ARG_ARTIST_ID = "TrackListFragment:ARTIST_ID";
    private static final String STATE_TRACKS = "TrackListFragment:TRACKS";

    private ArrayList<TrackViewModel> mTracks;

    public TrackListFragment() {
        SpotifyApi api =
                new SpotifyApi(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
        mSpotifyService = api.getService();
    }

    public static TrackListFragment newInstance(String artistId) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mTracks = Parcels.unwrap(savedInstanceState.getParcelable(STATE_TRACKS));
            setListAdapter(new TrackAdapter(getActivity(), mTracks));
        } else {
            Map<String, Object> params = new HashMap<>(1);
            params.put(SpotifyService.COUNTRY, "US");
            mSpotifyService.getArtistTopTrack(
                    getArguments().getString(ARG_ARTIST_ID),
                    params,
                    new Callback<Tracks>() {
                        @Override
                        public void success(Tracks tracks, Response response) {
                            if (getActivity() == null) return;

                            setEmptyText(getString(R.string.track_list_no_results));
                            mTracks = new ArrayList<>(tracks.tracks.size());
                            for (Track track : tracks.tracks) {
                                TrackViewModel vm = new TrackViewModel(track);
                                mTracks.add(vm);
                            }

                            setListAdapter(new TrackAdapter(getActivity(), mTracks));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (getActivity() == null) return;
                            setEmptyText(getString(R.string.error_loading_tracks));
                            Toast
                                    .makeText(
                                            getActivity(),
                                            R.string.error_loading_tracks,
                                            Toast.LENGTH_SHORT)
                                    .show();
                            Log.e(LOG_TAG, "Error loading tracks", error);
                        }
                    });
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        TrackAdapter.TrackViewHolder viewHolder = (TrackAdapter.TrackViewHolder) view.getTag();
        startActivity(PlayerActivity.createIntent(getActivity(), viewHolder.getTrackViewModel()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_TRACKS, Parcels.wrap(mTracks));
    }

    public static class TrackAdapter extends ArrayAdapter<TrackViewModel> {
        private final LayoutInflater mLayoutInflater;

        public TrackAdapter(Context context, List<TrackViewModel> tracks) {
            super(context, R.layout.list_item_track, tracks);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_track, parent, false);
            }

            TrackViewHolder viewHolder = (TrackViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new TrackViewHolder(convertView);
            }

            viewHolder.setTrackViewModel(getItem(position));
            return convertView;
        }

        static class TrackViewHolder {
            @InjectView(R.id.album_image)
            ImageView mAlbumImage;
            @InjectView(R.id.album_name)
            TextView mAlbumName;
            @InjectView(R.id.track_name)
            TextView mTrackName;

            private TrackViewModel mTrackViewModel;

            TrackViewHolder(View view) {
                ButterKnife.inject(this, view);
                view.setTag(this);
            }

            public TrackViewModel getTrackViewModel() {
                return mTrackViewModel;
            }

            public void setTrackViewModel(TrackViewModel trackViewModel) {
                mTrackViewModel = trackViewModel;
                mTrackName.setText(trackViewModel.getTrackName());
                mAlbumName.setText(trackViewModel.getAlbumName());
                if (!TextUtils.isEmpty(trackViewModel.getSmallImageUrl())) {
                    Picasso
                            .with(mAlbumImage.getContext())
                            .load(trackViewModel.getSmallImageUrl())
                            .centerInside()
                            .resizeDimen(R.dimen.artist_image_width, R.dimen.artist_image_height)
                            .into(mAlbumImage);
                } else {
                    mAlbumImage.setImageDrawable(null);
                }
            }
        }
    }
}
