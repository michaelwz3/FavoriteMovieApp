package ca.georgiancollege.favoritemovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.georgiancollege.favoritemovieapp.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // firebase to handle auth
        mAuth = FirebaseAuth.getInstance();

        // sends user to register screen when clicking register
        binding.registerText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), Register.class);
                startActivity(intentObj);
            }
        });


        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // grabs user input
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                // checks if fields are filled
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Email and password are required",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // attempts to log in through firebase
                signIn(email, password);
            }
        });
    }

    private void signIn(String email, String password){
        // firebase sign in API
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                (this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if works, goes to main activity, closes login,
                        // and tells the user it was successful
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Login succeeded.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intentObj = new Intent(getApplicationContext(),
                                    MainActivity.class);
                            startActivity(intentObj);
                            finish();
                        }
                        // tell the user if input was invalid
                        else{
                            Toast.makeText(Login.this, "Login failed: Invalid email " +
                                    "or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}