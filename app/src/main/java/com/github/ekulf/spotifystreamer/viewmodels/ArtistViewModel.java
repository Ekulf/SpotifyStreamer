package com.github.ekulf.spotifystreamer.viewmodels;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class ArtistViewModel {
    private String mArtistId;
    private String mArtistName;
    private String mArtistImageUrl;

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
