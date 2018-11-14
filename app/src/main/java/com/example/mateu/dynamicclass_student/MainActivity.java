package com.example.mateu.dynamicclass_student;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public static String myUID;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    View LoginView;
    View HomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        LoginView = findViewById(R.id.LoginLayout);
        HomeView = findViewById(R.id.HomeLayout);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mSingUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSignUpActivity();
            }
        });

        Button mLogoutButton = (Button) findViewById(R.id.logoutButton);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginView.setVisibility(View.VISIBLE);
                HomeView.setVisibility((View.INVISIBLE));
            }
        });

        Button mMySubjectsButton = (Button) findViewById(R.id.mySubjectsButton);
        mMySubjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMySubjectsActivity();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser == null){
            LoginView.setVisibility(View.VISIBLE);
            HomeView.setVisibility((View.INVISIBLE));
        }else{
            HomeView.setVisibility(View.VISIBLE);
            LoginView.setVisibility((View.INVISIBLE));
            myUID = currentUser.getUid();
            Log.d("OLHA O ID BRASIL1", myUID);
        }
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel == false){
            requestLogin(email, password);
        }

    }

    private void requestLogin(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            HomeView.setVisibility(View.VISIBLE);
                            LoginView.setVisibility((View.INVISIBLE));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static void tryToJoinSubject(String subjectCode, String subjectPassword){

    }

    public void toSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void toMySubjectsActivity() {
        Intent intent = new Intent(this, MySubjects.class);
        intent.putExtra("id", currentUser.getUid());
        startActivity(intent);
    }



}