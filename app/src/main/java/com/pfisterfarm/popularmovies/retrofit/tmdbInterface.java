package com.pfisterfarm.popularmovies.retrofit;

import com.pfisterfarm.popularmovies.models.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface tmdbInterface {
    @GET("movie/popular")
    Call<Movies> fetchPopularMovies(@Query("api_key") String api_key);

    @GET("movie/top_rated")
    Call<Movies> fetchTopRatedMovies(@Query("api_key") String api_key);

}