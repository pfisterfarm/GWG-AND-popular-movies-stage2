package com.pfisterfarm.popularmovies.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pfisterfarm.popularmovies.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.reviewViewHolder> {

    private int mNumberReviews;

    private ArrayList<Review> mReviews;

    public ReviewAdapter(int numberOfReviews) {
        mNumberReviews = numberOfReviews;
    }

    @Override
    public reviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_element, parent, false);
        ReviewAdapter.reviewViewHolder viewHolder = new ReviewAdapter.reviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(reviewViewHolder holder, int position) {
        holder.bind(position, holder.reviewLink.getContext());
    }

    @Override
    public int getItemCount() {
        return mNumberReviews;
    }

    public void setReviews(ArrayList<Review> incomingReviews) {
        mReviews = incomingReviews;
        notifyDataSetChanged();
    }

    class reviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewLink;

        public reviewViewHolder(View itemView) {
            super(itemView);

            reviewLink = (TextView) itemView.findViewById(R.id.review_author);

        }

        void bind(int listIndex, Context context) {
            reviewLink.setText(mReviews.get(listIndex).getAuthorName());
            reviewLink.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }
}