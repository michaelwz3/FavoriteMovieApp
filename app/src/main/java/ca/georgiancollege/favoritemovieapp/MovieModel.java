package ca.georgiancollege.favoritemovieapp;

import com.google.firebase.firestore.PropertyName;

public class MovieModel {

    // declaration
    public String Title;
    public String Rating;
    public String Poster;

    public String ReleaseDate;

    // blank constructor
    public MovieModel() {}

    // setters
    public MovieModel(String Title, String Rating, String Poster, String ReleaseDate) {
        this.Title = Title;
        this.Rating = Rating;
        this.Poster = Poster;
        this.ReleaseDate = ReleaseDate;
    }
}

