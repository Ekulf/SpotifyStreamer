package com.github.ekulf.spotifystreamer.viewmodels;

import org.parceler.Parcel;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

@Parcel(Parcel.Serialization.BEAN)
public class ArtistViewModel {
    private String mArtistId;
    private String mArtistName;
    private String mArtistImageUrl;

    public ArtistViewModel() {
    }

    public ArtistViewModel(Artist artist) {
        mArtistId = artist.id;
        mArtistName = artist.name;
        if (artist.images != null && !artist.images.isEmpty()) {
            for (Image image : artist.images) {
                if (image.width == 200) {
                    mArtistImageUrl = image.url;
                }
            }

            if (mArtistImageUrl == null) {
                mArtistImageUrl = artist.images.get(0).url;
            }
        }
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setArtistId(String artistId) {
        mArtistId = artistId;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public String getArtistImageUrl() {
        return mArtistImageUrl;
    }

    public void setArtistImageUrl(String artistImageUrl) {
        mArtistImageUrl = artistImageUrl;
    }
}
