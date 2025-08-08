package ca.georgiancollege.favoritemovieapp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MovieViewModel extends ViewModel {

    // firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // livedata to store movie lists
    private final MutableLiveData<ArrayList<MovieModel>> favoriteMovies
            = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<MovieModel>> availableMovies
            = new MutableLiveData<>(new ArrayList<>());

    // favorites movies for livedata
    public LiveData<ArrayList<MovieModel>> getFavoriteMovies() {
        return favoriteMovies;
    }

    // available movies for livedata
    public LiveData<ArrayList<MovieModel>> getAvailableMovies() {
        return availableMovies;
    }

    // loads favorite movies from firestore and updates livedata
    public void loadFavorites() {
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<MovieModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            list.add(doc.toObject(MovieModel.class));
                        }
                        favoriteMovies.setValue(list);
                    } else {
                        Log.e("MovieViewModel", "Could not get favorites",
                                task.getException());
                    }
                });
    }


    // when adding to favorites, only loads movies that were not already favorited
    public void loadAvailableMovies(ArrayList<String> favoriteTitles) {
        db.collection("movies")
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<MovieModel> available = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String title = doc.getString("Title");
                        if (favoriteTitles.contains(title)) continue;

                        String rating = doc.getString("Rating");
                        String poster = doc.getString("Poster");
                        String releaseDate = doc.getString("ReleaseDate");

                        available.add(new MovieModel(title, rating, poster, releaseDate));
                    }
                    availableMovies.setValue(available);
                })
                .addOnFailureListener(e -> Log.e("MovieViewModel",
                        "Failed to load available movies", e));
    }

    // loads favorites for the user first, then any non favorite movies
    public void loadFavoritesThenAvailable() {
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> favoriteTitles = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        favoriteTitles.add(doc.getString("Title"));
                    }
                    loadAvailableMovies(favoriteTitles);
                })
                .addOnFailureListener(e -> Log.e("MovieViewModel",
                        "Failed to load favorites", e));
    }
}