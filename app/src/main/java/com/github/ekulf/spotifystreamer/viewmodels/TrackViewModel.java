package com.github.ekulf.spotifystreamer.viewmodels;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class TrackViewModel {
    private String mTrackId;
    private String mTrackName;
    private String mAlbumName;
    private String mImageUrl;

    public String getTrackId() {
        return mTrackId;
    }

    public void setTrackId(String trackId) {
        mTrackId = trackId;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String trackName) {
        mTrackName = trackName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
