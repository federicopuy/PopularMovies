package com.example.federico.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.model.MovieEntry;
import com.example.federico.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

// source code copied from https://inducesmile.com/android/android-gridview-vs-gridlayout-example-tutorial/\
public class CustomAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private List<MovieEntry> moviesList;

    public CustomAdapter(Context context, List<MovieEntry> moviesList) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.moviesList = moviesList;
    }


    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public Object getItem(int i) {
        return moviesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void refresh(List<MovieEntry> moviesList){
        this.moviesList = moviesList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder listViewHolder;
        if (view == null) {
            listViewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.each_grid_view, viewGroup, false);
            listViewHolder.imageView = view.findViewById(R.id.imageView);
            view.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) view.getTag();
        }
        String imagePath = moviesList.get(i).getPosterPath();
        Picasso.get()
                .load(Utils.getImageUrl(imagePath))
                .error(R.drawable.ic_baseline_error_24px)
                .into(listViewHolder.imageView);

        listViewHolder.imageView.setAdjustViewBounds(true);
        return view;
    }

    static class ViewHolder {
        ImageView imageView;
    }

}
