package com.pfisterfarm.popularmovies.models;

import com.google.gson.annotations.SerializedName;
import com.pfisterfarm.popularmovies.models.Trailer;

import java.util.List;

public class Trailers {

    @SerializedName("results")
    List<Trailer> results;

    public List<Trailer> getResults() { return results; }
}
