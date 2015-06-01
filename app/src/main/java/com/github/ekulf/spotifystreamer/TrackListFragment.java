package com.github.ekulf.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;

public class TrackListFragment extends ListFragment {
    private SpotifyService mSpotifyService;

    private static final String ARG_ARTIST_ID = "TrackListFragment:ARTIST_ID";

    public TrackListFragment() {
        SpotifyApi api = new SpotifyApi(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
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
        Map<String, Object> params = new HashMap<>(1);
        params.put(SpotifyService.COUNTRY, "US");
        mSpotifyService.getArtistTopTrack(
                getArguments().getString(ARG_ARTIST_ID),
                params,
                new Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO: Show error
                    }
                });
    }
}
