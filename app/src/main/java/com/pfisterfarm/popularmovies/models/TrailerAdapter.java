package com.pfisterfarm.popularmovies.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.R;
import com.pfisterfarm.popularmovies.utils.helpers;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Activity context, ArrayList<Trailer> trailerArray) {
        super(context,0, trailerArray);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Trailer thisTrailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_element, parent, false);
        }

        ImageView trailerView = (ImageView) convertView.findViewById(R.id.trailer_thumbnail);
        Picasso.with(getContext()).
                load(helpers.makeTrailerURL(thisTrailer.getVideoKey())).
                fit().
                into(trailerView);

        TextView trailerName = (TextView) convertView.findViewById(R.id.trailer_name);
        trailerName.setText(thisTrailer.getVideoName());

        return convertView;
    }
}