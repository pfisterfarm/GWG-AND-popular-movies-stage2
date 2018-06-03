package com.pfisterfarm.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {

    @SerializedName("author")
    private String authorName;

    @SerializedName("content")
    private String reviewContent;

    protected Review(String authorName, String reviewContent) {
        this.authorName = authorName;
        this.reviewContent = reviewContent;
    }

    protected Review(Parcel in) {
        this.authorName = in.readString();
        this.reviewContent = in.readString();
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(authorName);
        parcel.writeString(authorName);
        parcel.writeString(reviewContent);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

}
