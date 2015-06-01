package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;

public class MainFragment extends ListFragment {
    private SpotifyService mSpotifyService;
    private ArtistsAdapter mArtistsAdapter;

    public MainFragment() {
        SpotifyApi api = new SpotifyApi(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
        mSpotifyService = api.getService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, root);
        mArtistsAdapter = new ArtistsAdapter(getActivity());
        setListAdapter(mArtistsAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnEditorAction(R.id.text_input)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search(v.getText().toString());
        }

        // Return false so the keyboard will automatically close.
        return false;
    }

    private void search(String searchText) {
        mArtistsAdapter.clear();
        mSpotifyService.searchArtists(searchText, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                // TODO: Implement paging
                mArtistsAdapter.addAll(artistsPager.artists.items);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private static class ArtistsAdapter extends ArrayAdapter<Artist> {
        private final LayoutInflater mLayoutInflater;

        public ArtistsAdapter(Context context) {
            // TODO: Create custom layout for artist items
            super(context, android.R.layout.simple_list_item_1);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =
                        mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            ((TextView) convertView).setText(getItem(position).name);

            return convertView;
        }
    }
}
