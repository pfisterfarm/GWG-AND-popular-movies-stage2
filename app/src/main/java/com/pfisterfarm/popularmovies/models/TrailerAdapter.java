package com.pfisterfarm.popularmovies.models;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.trailerViewHolder> {

    private int mNumberTrailers;

    private ArrayList<Trailer> mTrailers;

    public TrailerAdapter(int numberOfTrailers) {
        mNumberTrailers = numberOfTrailers;
    }

    @Override
    public trailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_element, parent, false);
        TrailerAdapter.trailerViewHolder viewHolder = new TrailerAdapter.trailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(trailerViewHolder holder, int position) {
        holder.bind(position, holder.trailerThumb.getContext());
    }

    @Override
    public int getItemCount() {
        return mNumberTrailers;
    }

    public void setTrailers(ArrayList<Trailer> incomingTrailers) {
        mTrailers = incomingTrailers;
        notifyDataSetChanged();
    }

    class trailerViewHolder extends RecyclerView.ViewHolder {
        ImageView trailerThumb;
        TextView trailerName;

        public trailerViewHolder(View itemView) {
            super(itemView);

            trailerThumb = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            trailerName = (TextView) itemView.findViewById(R.id.trailer_name);

        }

        void bind(int listIndex, Context context) {
            trailerName.setText(mTrailers.get(listIndex).getVideoName());
            Picasso.with(context).
                    load(helpers.makeTrailerURL(mTrailers.get(listIndex).getVideoKey())).
                    fit().
                    into(trailerThumb);
        }
    }
}