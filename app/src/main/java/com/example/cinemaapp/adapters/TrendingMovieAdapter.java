package com.example.cinemaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.Movie;

import java.util.List;

public class TrendingMovieAdapter extends RecyclerView.Adapter<TrendingMovieAdapter.TrendingViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> movieList;
    private OnMovieClickListener listener;

    public TrendingMovieAdapter(List<Movie> movieList, OnMovieClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    public static class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvRating;
        TextView tvBadge;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivTrendingPoster);
            tvTitle = itemView.findViewById(R.id.tvTrendingTitle);
            tvRating = itemView.findViewById(R.id.tvTrendingRating);
            tvBadge = itemView.findViewById(R.id.tvTrendingBadge);
        }
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending_movie, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(movie.getTitle());
        holder.tvBadge.setText("#" + (position + 1));

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        float rating = dbHelper.getAverageRating(movie.getId());
        if (rating > 0) {
            holder.tvRating.setText("★ " + String.format("%.1f", rating));
        } else {
            holder.tvRating.setText("★ —");
        }

        int resId = context.getResources().getIdentifier(
                movie.getPoster(), "drawable", context.getPackageName()
        );
        if (resId != 0) {
            holder.ivPoster.setImageResource(resId);
        } else {
            holder.ivPoster.setImageResource(R.drawable.ic_movie_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movieList = newMovies;
        notifyDataSetChanged();
    }
}