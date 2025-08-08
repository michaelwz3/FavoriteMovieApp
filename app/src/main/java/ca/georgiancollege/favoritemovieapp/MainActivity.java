package ca.georgiancollege.favoritemovieapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ca.georgiancollege.favoritemovieapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // viewbinding
    ActivityMainBinding binding;

    // adapter
    MovieAdapter adapter;

    // viewmodel
    MovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup recyclerview for favorites
        adapter = new MovieAdapter(this, new ArrayList<>(), "favorites");
        binding.favoritedMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.favoritedMovieRecyclerView.setAdapter(adapter);

        // setup viewmodel
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // observe movies from livedata
        viewModel.getFavoriteMovies().observe(this, movies -> {
            adapter.setMovieList(movies);
        });

        // reload from firestore
        viewModel.loadFavorites();

        // go to addmovies when add movie is clicked
        binding.addMovieButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMovies.class);
            startActivity(intent);
        });
    }

    // reloads favorites when coming back from add movies
    @Override
    public void onResume(){
        super.onResume();
        viewModel.loadFavorites();
    }

}