package com.github.ekulf.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;

public class MainFragment extends ListFragment {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private SpotifyService mSpotifyService;
    private ArtistsAdapter mArtistsAdapter;
    private int mTotalCount;
    private String mSearchTerm;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(new EndlessScrollListener(5) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount < mTotalCount) {
                    search(mSearchTerm, totalItemsCount);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        ArtistsAdapter.ArtistViewHolder vh = (ArtistsAdapter.ArtistViewHolder) view.getTag();
        Artist artist = vh.getArtist();

        startActivity(TrackListActivity.createIntent(getActivity(), artist.id, artist.name));
    }

    @OnEditorAction(R.id.text_input)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mArtistsAdapter.clear();
            mSearchTerm = v.getText().toString();
            search(mSearchTerm, 0);
        }

        // Return false so the keyboard will automatically close.
        return false;
    }

    private void search(String searchText, int offset) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(SpotifyService.OFFSET, offset);
        mSpotifyService.searchArtists(searchText, params, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                mTotalCount = artistsPager.artists.total;
                if (mTotalCount == 0) {
                    Toast.makeText(
                            getActivity(),
                            R.string.search_artist_no_results,
                            Toast.LENGTH_LONG).show();
                } else {
                    mArtistsAdapter.addAll(artistsPager.artists.items);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(
                        getActivity(),
                        R.string.error_search_artist,
                        Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Error searching for artist", error);
            }
        });
    }

    static class ArtistsAdapter extends ArrayAdapter<Artist> {
        private final LayoutInflater mLayoutInflater;

        public ArtistsAdapter(Context context) {
            super(context, R.layout.list_item_artist);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =
                        mLayoutInflater.inflate(R.layout.list_item_artist, parent, false);
            }

            ArtistViewHolder viewHolder = (ArtistViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new ArtistViewHolder(convertView);
            }

            viewHolder.setArtist(getItem(position));
            return convertView;
        }

        static class ArtistViewHolder {
            @InjectView(R.id.artist_image)
            ImageView mArtistImage;
            @InjectView(R.id.artist_name)
            TextView mArtistName;

            private Artist mArtist;

            ArtistViewHolder(View view) {
                ButterKnife.inject(this, view);
                view.setTag(this);
            }

            public void setArtist(Artist artist) {
                mArtist = artist;
                mArtistName.setText(artist.name);
                if (artist.images != null && !artist.images.isEmpty()) {
                    // TODO: should make choosing the image more robust
                    Picasso
                            .with(mArtistImage.getContext())
                            .load(artist.images.get(artist.images.size() - 1).url)
                            .resizeDimen(R.dimen.artist_image_width, R.dimen.artist_image_height)
                            .centerInside()
                            .into(mArtistImage);
                } else {
                    mArtistImage.setImageDrawable(null);
                }
            }

            public Artist getArtist() {
                return mArtist;
            }
        }
    }
}
