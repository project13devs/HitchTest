package com.maximzedits.hitchtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "TAG_INFORMATION";
    
    //firebase objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    //xml elements
    private EditText mEmailET, mPasswordET;
    private Button mLoginBtn, mSigninBtn;
    //vars
    private boolean permissionsGranted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoginBtn =  findViewById(R.id.logInBtn);
        mSigninBtn = findViewById(R.id.signInBtn);
        mPasswordET = findViewById(R.id.passwordText);
        mEmailET = findViewById(R.id.emailText);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isUserLoggedIn()) {
            theIntentMethod();
        } else {
            mSigninBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: button clicked");
                    mSigninBtn.setClickable(false);
                    if(!mEmailET.getText().toString().equals("") && !mPasswordET.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please wait", Toast.LENGTH_LONG).show();
                        mFirebaseAuth.createUserWithEmailAndPassword(mEmailET.getText().toString(), mPasswordET.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            Log.i(TAG, "onComplete: create user successfull with userlogin: " + isUserLoggedIn());
                                            theIntentMethod();
                                        } else {
                                            mSigninBtn.setClickable(true);
                                            Log.i(TAG, "onComplete: creation failed: " + task.getException());
                                        }
                                    }
                                });

                    }
                    else {
                        Toast.makeText(LoginActivity.this, "one or more fields are empty", Toast.LENGTH_SHORT).show();
                        mSigninBtn.setClickable(true);
                        Log.i(TAG, "onClick: one or more fields are empty");
                    }

                }
            });

            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Log.i(TAG, "onClick: button clicked");
                    mLoginBtn.setClickable(false);
                    if(!mEmailET.getText().toString().equals("") && !mPasswordET.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please wait", Toast.LENGTH_LONG).show();
                        mFirebaseAuth.signInWithEmailAndPassword(mEmailET.getText().toString(), mPasswordET.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            Log.i(TAG, "onComplete: signin successfull with userlogin: " + isUserLoggedIn());
                                            theIntentMethod();
                                        } else {
                                            mLoginBtn.setClickable(true);
                                            Log.i(TAG, "onComplete: signin failed: " + task.getException());
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "one or more fields are empty", Toast.LENGTH_SHORT).show();
                        mLoginBtn.setClickable(true);
                        Log.i(TAG, "onClick: one or more fields are empty");
                    }
                }
            });
        }

    }

    private boolean isUserLoggedIn () {
        Log.i(TAG, "isUserLoggedIn: Entered");
        // check if the user is signed in
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null) {
            Log.i(TAG, "theIntentMethod: User isnt signed in, returning Method");
            return false;
        }
        return true;
    }

    private void theIntentMethod() {
        Log.i(TAG, "theIntentMethod: Entered");

        //TODO: intent to the next activity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
