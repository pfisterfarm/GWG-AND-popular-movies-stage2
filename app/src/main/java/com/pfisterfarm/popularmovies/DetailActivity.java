package com.pfisterfarm.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.models.Review;
import com.pfisterfarm.popularmovies.models.Reviews;
import com.pfisterfarm.popularmovies.models.Trailer;
import com.pfisterfarm.popularmovies.models.TrailerAdapter;
import com.pfisterfarm.popularmovies.models.Trailers;
import com.pfisterfarm.popularmovies.retrofit.tmdbClient;
import com.pfisterfarm.popularmovies.retrofit.tmdbInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pfisterfarm.popularmovies.MainActivity.trailers;


public class DetailActivity extends AppCompatActivity {

    final String logTag = "POPMOVIES";
    final String sApiKey = BuildConfig.API_KEY;

    tmdbInterface tmdbService = tmdbClient.getClient().create(tmdbInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";
        final String TRAILER_KEY = "trailer";
        final String REVIEW_KEY = "review";
        final String logTag = "POPMOVIES";
        final String movieIdStr = "movie_id";

        long movieId = 0;

        ArrayList<Trailer> trailers;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie detailMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        movieId = getIntent().getLongExtra(movieIdStr, 0);
        Log.i(logTag, "movie id received is; " +  movieId);
        Log.i(logTag, "key used to recieve: " + movieIdStr);
        trailers = loadTrailers(movieId);
        Log.i(logTag, "at the start of the detail activity, trailers size is: "  + trailers.size());

        setTitle(detailMovie.getMovieTitle());

        ImageView iv_poster = (ImageView) findViewById(R.id.poster_detail_iv);
        Picasso.with(this).
                load(detailMovie.makePosterURL()).
                error(R.drawable.placeholder).
                fit().
                into(iv_poster);

        TextView tv_release_date = (TextView) findViewById(R.id.release_date_tv);
        tv_release_date.setText(detailMovie.getReleaseDate());

        RatingBar ratingBar = (RatingBar) findViewById(R.id.votes_rb);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(detailMovie.getVoteAverage());

        TextView tv_overview = (TextView) findViewById(R.id.overview_tv);
        tv_overview.setText(detailMovie.getPlotSynopsis());

        Log.i(logTag, "at end of detail activity, size; " +  trailers.size());
        ListView trailersList = (ListView) findViewById(R.id.trailers_listview);
        TrailerAdapter trailersAdapter = new TrailerAdapter(this, trailers);
        trailersList.setAdapter(trailersAdapter);
    }

    protected ArrayList<Trailer> loadTrailers(long movieId) {
        Call<Trailers> call = tmdbService.fetchTrailers(Long.toString(movieId), sApiKey);
        Log.i(logTag, "early on in loadTrailers,");
        Log.i(logTag, "sApiKey is: " + sApiKey);
        Log.i(logTag, "movieId is: " + movieId);
        final ArrayList<Trailer> trailers = new ArrayList<Trailer>();
        call.enqueue(new Callback<Trailers>() {

            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                Log.i(logTag, "got to start of onresponse");
                List<Trailer> returnList = response.body().getResults();
                Log.i(logTag, "size of returnList is: " + returnList.size());
                trailers.clear();
                for (Trailer oneTrailer : returnList) {
                    Log.i(logTag, "video name is: " + oneTrailer.getVideoName());
                    Log.i(logTag, "video type is: " + oneTrailer.getVideoType());
                    if (oneTrailer.getVideoType().contains("Trailer")) {
                        Log.i(logTag, "this trailer got added!");
                        trailers.add(oneTrailer);
                    }
                };
                Log.i(logTag, "at the end of the loop, size is: " + trailers.size());
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });

        Log.i(logTag, "size of trailers array is: " + trailers.size());
        return trailers;
    }
}
