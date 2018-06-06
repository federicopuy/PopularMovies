package com.example.federico.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.model.Video;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder>{

    private List<Video> videoList;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    public VideosAdapter(List<Video> videoList, Context mContext, ListItemClickListener mOnClickListener) {
        this.videoList = videoList;
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.each_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = mContext.getString(R.string.trailer) + position;
        holder.tvTrailerNumber.setText(text);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTrailerNumber;

        private ViewHolder(View itemView) {
        super(itemView);
        tvTrailerNumber = itemView.findViewById(R.id.tvTrailerNumber);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickedPosition = getAdapterPosition();
        Video clickedVideo = videoList.get(clickedPosition);
        mOnClickListener.onListItemClick(clickedPosition, clickedVideo);

    }
}

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, Video videClicked);
    }

}
