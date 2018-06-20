package com.pfisterfarm.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.models.FavDatabase;
import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.models.Review;
import com.pfisterfarm.popularmovies.models.ReviewAdapter;
import com.pfisterfarm.popularmovies.models.Reviews;
import com.pfisterfarm.popularmovies.models.Trailer;
import com.pfisterfarm.popularmovies.models.TrailerAdapter;
import com.pfisterfarm.popularmovies.models.Trailers;
import com.pfisterfarm.popularmovies.retrofit.tmdbClient;
import com.pfisterfarm.popularmovies.retrofit.tmdbInterface;
import com.pfisterfarm.popularmovies.utils.AppExecutors;
import com.pfisterfarm.popularmovies.utils.helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pfisterfarm.popularmovies.MainActivity.favMovies;
import static com.pfisterfarm.popularmovies.MainActivity.trailers;


public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    final String logTag = "POPMOVIES";
    final String sApiKey = BuildConfig.API_KEY;
    final String trailersStr = "trailers";
    final String reviewsStr = "reviews";

    tmdbInterface tmdbService = tmdbClient.getClient().create(tmdbInterface.class);

    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;
    RecyclerView mTrailersRecycler;
    RecyclerView mReviewsRecycler;
    TrailerAdapter mAdapter;
    ReviewAdapter mReviewsAdapter;
    private FavDatabase mDb;
    CheckBox favoritesButton;
    boolean favoritedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";
        final String logTag = "POPMOVIES";
        final String movieIdStr = "movie_id";

        long movieId = 0;
        mDb = FavDatabase.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        favoritesButton = (CheckBox) findViewById(R.id.favButton);

        mTrailersRecycler = (RecyclerView) findViewById(R.id.trailers_rv);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        mTrailersRecycler.setLayoutManager(layoutMgr);
        mTrailersRecycler.setHasFixedSize(true);

        mReviewsRecycler = (RecyclerView) findViewById(R.id.reviews_rv);
        LinearLayoutManager layoutMgrRev = new LinearLayoutManager(this);
        mReviewsRecycler.setLayoutManager(layoutMgrRev);
        mReviewsRecycler.setHasFixedSize(true);

        final Movie detailMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        movieId = getIntent().getLongExtra(movieIdStr, 0);

        favoritedFlag = checkIfFavorite(movieId);

        if (savedInstanceState == null) {
            loadTrailers(movieId, this);
            loadReviews(movieId);
        } else {
            trailers = savedInstanceState.getParcelableArrayList(trailersStr);
            addTrailersToUI(this);
            reviews = savedInstanceState.getParcelableArrayList(reviewsStr);
            addReviewToUI();
        }

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
        ratingBar.setRating((detailMovie.getVoteAverage() / 2));

        TextView tv_overview = (TextView) findViewById(R.id.overview_tv);
        tv_overview.setText(detailMovie.getPlotSynopsis());

        if (!favoritedFlag) {
            favoritesButton.setChecked(false);
        } else {
            favoritesButton.setChecked(true);
        }
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (!favoritedFlag) {
                            mDb.movieDao().insertFavorite(detailMovie);
//                            favoritesButton.setChecked(false);
                            favoritedFlag = true;
                        } else {
                            mDb.movieDao().deleteFavorite(detailMovie);
//                            favoritesButton.setChecked(true);
                            favoritedFlag = false;
                        }
                    }
                });
        }

    });
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
                addTrailersToUI(listener);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });
    }

    private void addTrailersToUI(TrailerAdapter.ListItemClickListener listener) {
        mAdapter = new TrailerAdapter(trailers.size(), listener);
        mAdapter.setTrailers(trailers);
        mTrailersRecycler.setAdapter(mAdapter);
        if (trailers.size() == 0) {     // if no trailers, don't show the 'Trailers:' label
            TextView trailersLabel = (TextView) findViewById(R.id.trailers_label);
            trailersLabel.setVisibility(View.GONE);
        }
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

                addReviewToUI();
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Log.e(logTag, "onFailure trying to fetch trailers");
                t.printStackTrace();
            }
        });
    }

    private void addReviewToUI() {
        mReviewsAdapter = new ReviewAdapter(reviews.size());
        mReviewsAdapter.setReviews(reviews);
        mReviewsRecycler.setAdapter(mReviewsAdapter);
        if (reviews.size() == 0) {  // if no reviews, don't show the 'Reviews:' label
            TextView reviewsLabel = (TextView) findViewById(R.id.reviews_tv);
            reviewsLabel.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(trailersStr, trailers);
        outState.putParcelableArrayList(reviewsStr, reviews);
    }

    public boolean checkIfFavorite(long movieId) {
        // I had initially set up this method to work by querying the favorites database and
        // returning true/false if the movieId was found. I can't seem to make it work reliably.
        // The flag gets set when the database look up completes, which may be after the button text
        // gets set. I'd rather do it that way, but I don't know how to work out the timing issues.
        // Unless I found out another way, just looping through the favMovies arrayList
        for (int i = 0; i < favMovies.size(); i++) {
            if (favMovies.get(i).getId() == movieId) {
                return true;
            }
        }
        return false;
    }
//    public void checkIfFavorite(final long movieId) {
//        // This method queries the favorites database to see if the movie has already been favorited.
//        // Ideally, it would return a boolean value, but I can't figure out how to return a value from
//        // an AppExecutor call. I have to call a separate method which modifies a member variable. This
//        // seems like a kludge. Hopefully, I'll be able to correct this before submitting this project
//        boolean result = false;
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//
//            @Override
//            public void run() {
//                Movie checkMovie = mDb.movieDao().getMovie(movieId);
//                setFavoriteFlag(checkMovie != null);
//            }
//        });
//    }
//
//    public void setFavoriteFlag(boolean valueToSet) {
//        favoritedFlag = valueToSet;
//    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpers.makeYoutubeURL(trailers.get(clickItemIndex).getVideoKey())));
        startActivity(youtubeIntent);
    }
}
