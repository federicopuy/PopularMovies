package com.example.federico.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> reviewList;
    private Context mContext;


    public ReviewsAdapter(List<Review> reviewList, Context mContext) {
        this.reviewList = reviewList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.each_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Review review = reviewList.get(position);

        String reviewAuthor = "";
        String reviewContent = "";

        try{
            reviewAuthor = review.getAuthor();
            reviewContent = review.getContent();
        } catch (Exception e){
            e.printStackTrace();
        }

        holder.tvReviewAuthor.setText(reviewAuthor);
        holder.tvReviewContent.setText(reviewContent);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{


        TextView tvReviewAuthor, tvReviewContent;

        private ViewHolder(View itemView) {
            super(itemView);

            tvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);

        }
    }

}
