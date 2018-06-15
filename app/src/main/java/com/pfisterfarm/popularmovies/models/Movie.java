package com.pfisterfarm.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorites")
public class Movie implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String movieTitle;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("overview")
    private String plotSynopsis;

    public Movie(long id, String movieTitle, String releaseDate, String posterPath, float voteAverage, String plotSynopsis) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    @Ignore
    protected Movie(Parcel in) {
        id = in.readLong();
        movieTitle = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readFloat();
        plotSynopsis = in.readString();
    }

    public String makePosterURL() {

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w185/";

        return BASE_URL + SIZE + this.posterPath;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(movieTitle);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeFloat(voteAverage);
        dest.writeString(plotSynopsis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
