package com.pfisterfarm.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Review> results;

    public List<Review> getResults() { return results; }
}
