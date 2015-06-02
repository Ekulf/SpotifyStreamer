package com.github.ekulf.spotifystreamer.viewmodels;

import org.parceler.Parcel;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

@Parcel(Parcel.Serialization.BEAN)
public class TrackViewModel {
    private String mTrackId;
    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mSmallImageUrl;
    private String mLargeImageUrl;
    private String mPreviewUrl;

    public TrackViewModel() {
    }

    public TrackViewModel(Track track) {
        mTrackId = track.id;
        mTrackName = track.name;
        mAlbumName = track.album.name;
        mPreviewUrl = track.preview_url;
        mArtistName = track.artists.get(0).name;
        if (track.album.images != null && !track.album.images.isEmpty()) {
            for (Image image : track.album.images) {
                if (image.width == 200) {
                    mSmallImageUrl = image.url;
                } else if (image.width == 640) {
                    mLargeImageUrl = image.url;
                }
            }

            if (mSmallImageUrl == null) {
                mSmallImageUrl = track.album.images.get(0).url;
            }

            if (mLargeImageUrl == null) {
                mLargeImageUrl = track.album.images.get(0).url;
            }
        }
    }

    public String getTrackId() {
        return mTrackId;
    }

    public void setTrackId(String trackId) {
        mTrackId = trackId;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
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

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        mSmallImageUrl = smallImageUrl;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        mLargeImageUrl = largeImageUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        mPreviewUrl = previewUrl;
    }
}
