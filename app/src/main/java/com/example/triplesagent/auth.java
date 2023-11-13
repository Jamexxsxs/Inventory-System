package com.example.triplesagent;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.triplesagent.menu.tabs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class auth extends AppCompatActivity {
    private ConstraintLayout container;
    private LayoutInflater inflater;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MyApplication);
        setContentView(R.layout.login);


        container = findViewById(R.id.container);
        inflater = LayoutInflater.from(this);

        TextView forgotPass = findViewById(R.id.hyperlinkForgotPass);
        String text = "Click here";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        forgotPass.setText(spannableString);

        TextView signUp = findViewById(R.id.hyperlinkSignUp);
        signUp.setText(spannableString);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSignupPanel();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("235343260790-900avbkjbqp1eoi8lmo1j6r33ubenoen.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button GoogleSignIn = findViewById(R.id.SignInGoogle);
        GoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


    }

    private void switchToSignupPanel() {
        View signupPanel = inflater.inflate(R.layout.signup, container, false);
        container.removeAllViews();
        container.addView(signupPanel);

        TextView haveAcc = findViewById(R.id.hyperlinkSignIn);
        String text = "Click here";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveAcc.setText(spannableString);

        haveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLoginPanel();
            }
        });

        TextView phoneNum = findViewById(R.id.SignUpPhone);
        TextView Password = findViewById(R.id.SignUpPassword);
        TextView verifyPass = findViewById(R.id.VerifySignUpPassword);


    }

    private void switchToLoginPanel() {
        View loginPanel = inflater.inflate(R.layout.login, container, false);
        container.removeAllViews();
        container.addView(loginPanel);

        TextView forgotPass = findViewById(R.id.hyperlinkForgotPass);

        TextView signUp = findViewById(R.id.hyperlinkSignUp);
        String text = "Click here";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setText(spannableString);

        forgotPass.setText(spannableString);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSignupPanel();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("235343260790-900avbkjbqp1eoi8lmo1j6r33ubenoen.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button GoogleSignIn = findViewById(R.id.SignInGoogle);
        GoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        // Add user to Realtime Database
                        String userId = user.getUid();
                        String email = user.getEmail();
                        String displayName = user.getDisplayName();
                        String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

                        addUserToDatabase(userId, email, displayName, photoUrl);
                    }
                    Intent intent = new Intent(auth.this, tabs.class);
                    startActivity(intent);
                } else {
                    // Handle authentication failure
                    // ...
                }
            }
        });
    }

    private void addUserToDatabase(String userId, String email, String displayName, String photoUrl) {
        User newUser = new User(email, displayName, photoUrl);
        mDatabase.child("users").child(userId).setValue(newUser);
    }

}