package com.pfisterfarm.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    ArrayList<Trailer> trailers;
    RecyclerView mTrailersRecycler;
    TrailerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";
        final String TRAILER_KEY = "trailer";
        final String REVIEW_KEY = "review";
        final String logTag = "POPMOVIES";
        final String movieIdStr = "movie_id";

        long movieId = 0;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTrailersRecycler = (RecyclerView) findViewById(R.id.trailers_rv);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        mTrailersRecycler.setLayoutManager(layoutMgr);
        mTrailersRecycler.setHasFixedSize(true);

        Movie detailMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        movieId = getIntent().getLongExtra(movieIdStr, 0);
        loadTrailers(movieId);

        Log.i(logTag, "right after call to loadTrailers, size is: "  + trailers.size());

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

    }

    protected void loadTrailers(long movieId) {
        Call<Trailers> call = tmdbService.fetchTrailers(Long.toString(movieId), sApiKey);
        trailers = new ArrayList<Trailer>();
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
                mAdapter = new TrailerAdapter(trailers.size());
                mAdapter.setTrailers(trailers);
                mTrailersRecycler.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });
    }
}
