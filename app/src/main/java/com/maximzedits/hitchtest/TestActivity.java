package com.maximzedits.hitchtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TestActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public static final String TAG = "TAG_INFORMATION";

    Button mSignoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mSignoutButton = findViewById(R.id.signoutBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser == null) {
                    Log.i(TAG, "theIntentMethod: User isnt signed in");
                }else {
                    mFirebaseAuth.signOut();
                    Log.i(TAG, "onDestroy: signedOUT from button");
                }
            }
        });

    }

}
