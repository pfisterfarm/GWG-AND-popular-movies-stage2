package com.pfisterfarm.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.models.Review;
import com.pfisterfarm.popularmovies.models.Reviews;
import com.pfisterfarm.popularmovies.models.Trailer;
import com.pfisterfarm.popularmovies.models.Trailers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";
        final String TRAILER_KEY = "trailer";
        final String REVIEW_KEY = "review";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie detailMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        ArrayList<Trailer> trailers = getIntent().getParcelableArrayListExtra(TRAILER_KEY);
        ArrayList<Review> reviews = getIntent().getParcelableArrayListExtra(REVIEW_KEY);

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
}
