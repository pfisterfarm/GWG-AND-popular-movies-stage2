package com.pfisterfarm.popularmovies.models;

import com.google.gson.annotations.SerializedName;
import com.pfisterfarm.popularmovies.models.Movie;

import java.util.List;

public class Movies {
    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Movie> results;

    public List<Movie> getResults() { return results; }
}
