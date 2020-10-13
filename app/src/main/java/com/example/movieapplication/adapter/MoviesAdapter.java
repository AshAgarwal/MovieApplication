package com.example.movieapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapplication.DetailActivity;
import com.example.movieapplication.R;
import com.example.movieapplication.model.Movie;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movieList;

    public MoviesAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_card, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.movieTitle.setText(movieList.get(i).getTitle());
        String vote = Double.toString(movieList.get(i).getVoteAverage());
        viewHolder.movieRating.setText(vote);
        viewHolder.movieReleaseDate.setText(movieList.get(i).getReleaseDate());

        Glide.with(context)
                .load(movieList.get(i).getPosterPath())
                .placeholder(R.drawable.image_12)
                .into(viewHolder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView movieTitle, movieReleaseDate, movieRating;
        public ImageView movieImage;

        public ViewHolder(View view) {
            super(view);
            movieTitle = view.findViewById(R.id.movie_title);
            movieReleaseDate = view.findViewById(R.id.movie_releaseDate);
            movieRating = view.findViewById(R.id.movie_rating);
            movieImage = view.findViewById(R.id.movie_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("id", movieList.get(pos).getId());
                        intent.putExtra("original_title", movieList.get(pos).getOriginalTitle());
                        intent.putExtra("poster_path", movieList.get(pos).getPosterPath());
                        intent.putExtra("overview", movieList.get(pos).getOverview());
                        intent.putExtra("vote_average", Double.toString(movieList.get(pos).getVoteAverage()));
                        intent.putExtra("release_date", movieList.get(pos).getReleaseDate());
                        intent.putExtra("popularity", Double.toString(movieList.get(pos).getPopularity()));
                        intent.putExtra("adult", Boolean.toString(movieList.get(pos).isAdult()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        Snackbar.make(view, "Welcome in " + clickedDataItem.getTitle(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}
