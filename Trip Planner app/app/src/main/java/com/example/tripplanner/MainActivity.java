package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ProgressBar progress;
    private EditText email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.logInMailText);
        password = (EditText) findViewById(R.id.logInPasswordText);
        progress = (ProgressBar) findViewById(R.id.logInProgressBar);

    }


    public void toSignUp(View view) {
        startActivity(new Intent(MainActivity.this,SignUp.class));
    }

    public void logInUser(View view) {
        String mailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (mailText.isEmpty()){
            email.setError("Email is empty");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mailText).matches()){
            email.setError("Email is Not Proper");
            email.requestFocus();
            return;
        }
        if (passwordText.isEmpty()){
            password.setError("Password is wrong");
            password.requestFocus();
            return;
        }
        {
            progress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(mailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (true || user.isEmailVerified()) {
//                        Redirect to Home page
                            startActivity(new Intent(MainActivity.this, HomePage.class));
                        }
                        else{
                            user.sendEmailVerification();

                            Toast.makeText(MainActivity.this,"Verify Your Email",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Failed to Register Check Credentials ",Toast.LENGTH_LONG).show();
                    }
                    progress.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}