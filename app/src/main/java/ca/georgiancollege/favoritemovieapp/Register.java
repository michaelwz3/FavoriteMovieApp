package ca.georgiancollege.favoritemovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ca.georgiancollege.favoritemovieapp.databinding.ActivityLoginBinding;
import ca.georgiancollege.favoritemovieapp.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {

    // viewbinding
    ActivityRegisterBinding binding;

    // firebase auth for registering
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate layout
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // when register button is clicked, attempt to register user with input
        binding.registerButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               registerUser(binding.registerEmailEditText.getText().toString(),
                       binding.registerPasswordEditText.getText().toString());
           }
        });
        // closes register screen and goes back to log in
        binding.backToLoginText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerUser(String email, String password) {
        // if input is invalid, inform user
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Register.this, "Email and password cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(Register.this, "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // if all input is valid, attempt to create user with firebase API
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // grabs users firebase uid
                            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


                            // stores user email in firestore with uid
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Register", "User document created in" +
                                                " Firestore");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Register", "Error adding user to Firestore"
                                                , e);
                                    });

                            // if succesful tell user and go back to login page
                            Toast.makeText(Register.this, "Registration Succeeded." +
                                    " Bringing you back to the login page", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intentObj = new Intent(getApplicationContext(), Login.class);
                            startActivity(intentObj);
                        } else {
                            // if unsuccessful, tell the user why
                            Log.d("tag", "unsuccessful");
                            String error = task.getException() != null ? task.getException()
                                    .getMessage() : "Unknown error";
                            Toast.makeText(Register.this, "Registration failed: "
                                    + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
