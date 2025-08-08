package ca.georgiancollege.favoritemovieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // context
    private Context context;

    // movie list
    private ArrayList<MovieModel> movieList;

    // add or delete
    private String mode;

    public MovieAdapter(Context context, ArrayList<MovieModel> movieList, String mode) {
        this.context = context;
        this.movieList = movieList;
        this.mode = mode;
    }

    // custom viewholder class for each item
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, yearTextView, ratingTextView;
        ImageView posterImageView;
        Button addToFavoritesButton;

        public MovieViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            yearTextView = view.findViewById(R.id.yearTextView);
            ratingTextView = view.findViewById(R.id.ratingTextView);
            posterImageView = view.findViewById(R.id.posterImageView);
            addToFavoritesButton = view.findViewById(R.id.addToFavoritesButton);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate layout for each row
        View layout = LayoutInflater.from(context).inflate(R.layout.movie_items,
                parent, false);
        return new MovieViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // bind data to view
        MovieModel movie = movieList.get(position);

        holder.titleTextView.setText(movie.Title);
        holder.yearTextView.setText(movie.ReleaseDate);
        holder.ratingTextView.setText("Critic Rating: " + movie.Rating);

        // load poster image
        Glide.with(context)
                .load(movie.Poster)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.posterImageView);

        // set up firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        // if in favorites, button will add movie to user's favorites
        if (mode.equals("add")) {
            holder.addToFavoritesButton.setText("♡");
            holder.addToFavoritesButton.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                MovieModel selectedMovie = movieList.get(currentPosition);

                db.collection("users")
                        .document(uid)
                        .collection("favorites")
                        .document(selectedMovie.Title)
                        .set(selectedMovie)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Added to favorites",
                                    Toast.LENGTH_SHORT).show();

                            // remove from list and update ui
                            movieList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                        })
                        // if failed to add, notify user
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to add",
                                        Toast.LENGTH_SHORT).show());
            });
        }
        // if in favorites, button removes movies from user's favorites
        else if (mode.equals("favorites")) {
            holder.addToFavoritesButton.setText("ㄨ");
            holder.addToFavoritesButton.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                db.collection("users")
                        .document(uid)
                        .collection("favorites")
                        .document(movie.Title)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            movieList.remove(pos);
                            notifyItemRemoved(pos);
                            Toast.makeText(context, "Removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to remove",
                                        Toast.LENGTH_SHORT).show());
            });
        }
    }

    // get movie list size
    @Override
    public int getItemCount() {
        return movieList.size();
    }


    // replace movie list and update ui
    public void setMovieList(ArrayList<MovieModel> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }
}