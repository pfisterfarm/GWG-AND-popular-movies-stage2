package com.pfisterfarm.popularmovies.retrofit;

import com.pfisterfarm.popularmovies.models.Movies;
import com.pfisterfarm.popularmovies.models.Reviews;
import com.pfisterfarm.popularmovies.models.Trailers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface tmdbInterface {
    @GET("movie/popular")
    Call<Movies> fetchPopularMovies(@Query("api_key") String api_key);

    @GET("movie/top_rated")
    Call<Movies> fetchTopRatedMovies(@Query("api_key") String api_key);

    @GET("movie/{id}/videos")
    Call<Trailers> fetchTrailers(@Path("id") String movieId, @Query("api_key") String api_key);
//
//    @GET("movie/{id}/reviews")
//    Call<Reviews> fetchReviews(@Path("id") long movieId, @Query("api_key") String api_key);

}