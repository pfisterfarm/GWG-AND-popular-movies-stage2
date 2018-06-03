package com.pfisterfarm.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.models.Review;
import com.pfisterfarm.popularmovies.models.ReviewAdapter;
import com.pfisterfarm.popularmovies.models.Reviews;
import com.pfisterfarm.popularmovies.models.Trailer;
import com.pfisterfarm.popularmovies.models.TrailerAdapter;
import com.pfisterfarm.popularmovies.models.Trailers;
import com.pfisterfarm.popularmovies.retrofit.tmdbClient;
import com.pfisterfarm.popularmovies.retrofit.tmdbInterface;
import com.pfisterfarm.popularmovies.utils.helpers;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pfisterfarm.popularmovies.MainActivity.trailers;


public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    final String logTag = "POPMOVIES";
    final String sApiKey = BuildConfig.API_KEY;

    tmdbInterface tmdbService = tmdbClient.getClient().create(tmdbInterface.class);

    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;
    RecyclerView mTrailersRecycler;
    RecyclerView mReviewsRecycler;
    TrailerAdapter mAdapter;
    ReviewAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";
        final String logTag = "POPMOVIES";
        final String movieIdStr = "movie_id";

        long movieId = 0;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTrailersRecycler = (RecyclerView) findViewById(R.id.trailers_rv);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        mTrailersRecycler.setLayoutManager(layoutMgr);
        mTrailersRecycler.setHasFixedSize(true);

        mReviewsRecycler = (RecyclerView) findViewById(R.id.reviews_rv);
        LinearLayoutManager layoutMgrRev = new LinearLayoutManager(this);
        mReviewsRecycler.setLayoutManager(layoutMgrRev);
        mReviewsRecycler.setHasFixedSize(true);

        Movie detailMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        movieId = getIntent().getLongExtra(movieIdStr, 0);
        loadTrailers(movieId, this);
        loadReviews(movieId);

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

    }

    protected void loadTrailers(long movieId, final TrailerAdapter.ListItemClickListener listener) {
        Call<Trailers> call = tmdbService.fetchTrailers(Long.toString(movieId), sApiKey);
        trailers = new ArrayList<Trailer>();
        call.enqueue(new Callback<Trailers>() {

            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                List<Trailer> returnList = response.body().getResults();
                trailers.clear();
                for (Trailer oneTrailer : returnList) {
                    if (oneTrailer.getVideoType().contains("Trailer")) {
                        trailers.add(oneTrailer);
                    }
                };
                mAdapter = new TrailerAdapter(trailers.size(), listener);
                mAdapter.setTrailers(trailers);
                mTrailersRecycler.setAdapter(mAdapter);
                if (trailers.size() == 0) {     // if no trailers, don't show the 'Trailers:' label
                    TextView trailersLabel = (TextView) findViewById(R.id.trailers_label);
                    trailersLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });
    }

    protected void loadReviews(long movieId) {
        Call<Reviews> call = tmdbService.fetchReviews(Long.toString(movieId), sApiKey);
        reviews = new ArrayList<Review>();
        call.enqueue(new Callback<Reviews>() {

            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                List<Review> returnList = response.body().getResults();
                reviews.clear();
                reviews.addAll(returnList);

                mReviewsAdapter = new ReviewAdapter(reviews.size());
                mReviewsAdapter.setReviews(reviews);
                mReviewsRecycler.setAdapter(mReviewsAdapter);
                if (reviews.size() == 0) {  // if no reviews, don't show the 'Reviews:' label
                    TextView reviewsLabel = (TextView) findViewById(R.id.reviews_tv);
                    reviewsLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpers.makeYoutubeURL(trailers.get(clickItemIndex).getVideoKey())));
        startActivity(youtubeIntent);
    }
}
