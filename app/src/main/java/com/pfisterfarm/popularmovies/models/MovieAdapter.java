package com.pfisterfarm.popularmovies.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import com.pfisterfarm.popularmovies.R;
import com.pfisterfarm.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, ArrayList<Movie> movieArray) {
        super(context,0, movieArray);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Movie thisMovie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_frame, parent, false);
        }

        ImageView movieView = (ImageView) convertView.findViewById(R.id.moviePoster_iv);
        Picasso.with(getContext()).
                load(thisMovie.makePosterURL()).
                error(R.drawable.placeholder).
                fit().
                into(movieView);

        return convertView;
    }

    private String makePosterURL(String posterPath) {

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w185/";

        return BASE_URL + SIZE + posterPath;
    }
}
