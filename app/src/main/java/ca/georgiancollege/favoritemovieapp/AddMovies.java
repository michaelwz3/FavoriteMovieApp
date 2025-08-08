package ca.georgiancollege.favoritemovieapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ca.georgiancollege.favoritemovieapp.databinding.ActivityAddMoviesBinding;

public class AddMovies extends AppCompatActivity {

    // viewbinding
    ActivityAddMoviesBinding binding;

    // adapter
    MovieAdapter adapter;

    // viewmodel
    MovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate layout
        binding = ActivityAddMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // sets up adapter with empty list for adding movies
        adapter = new MovieAdapter(this, new ArrayList<>(), "add");
        binding.addMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.addMovieRecyclerView.setAdapter(adapter);

        // initialize viewmodel
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // observes available movies from livedata
        viewModel.getAvailableMovies().observe(this, movies -> {
            adapter.setMovieList(movies);
        });

        // load favorite and available movies
        viewModel.loadFavoritesThenAvailable();

        // back button brings user back to their favorites page
        binding.addMoviesBackButton.setOnClickListener(v -> finish());
    }
}