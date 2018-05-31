package com.maximzedits.hitchtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {

    //statics
    public static final String TAG = "TAG_INFORMATION";
    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    public static final int RC_SIGN_IN = 1;
    
    //firebase objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //xml elements
    private TextView mWarningTextView;
    private SignInButton mSignInButton;
    //For google sign in
    private GoogleSignInClient mGoogleSignInClient;
    //other vars
    private boolean mPermissionGranted = false;
    private boolean mUserSignedInToFirebase = false;


    /**Initializes everything, checks permissions**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignInButton = findViewById(R.id.signInButton);
        mWarningTextView = findViewById(R.id.warningTextView);

        //listens when the state of authentication is changed
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onAuthStateChanged: Entered");
                if(firebaseAuth.getCurrentUser() != null) {
                    mUserSignedInToFirebase = true;
                    if(isPermissionsGranted()) {
                        mPermissionGranted = true;
                        Log.i(TAG, "onAuthStateChanged: Going to map activity rightaway");
                        goToMapsActivity();
                    }
                }
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //create sign in client using the GSO
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    /**Starts everything**/
    @Override
    protected void onStart() {
        super.onStart();

        mWarningTextView.setVisibility(View.GONE);

        //check fpr permissions
        if(isPermissionsGranted()) {
            mPermissionGranted = true;
        }
        Log.i(TAG, "onStart: PermissionGranted: " + mPermissionGranted);

        //is user signed in?
        //this doesn't mean that the user is currently logged in with google, but that is not necessary i guess
        if(isUserSignedInToFirebase()) {
            mUserSignedInToFirebase = true;
        }
        Log.i(TAG, "onStart: UserSignedInToFirebase: " + mUserSignedInToFirebase);

        //start listening to signing ins and outs
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        //button click listener
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do all the jobs here
                if(!mPermissionGranted) {
                    Log.i(TAG, "on start > onClick: no permission");
                    mWarningTextView.setVisibility(View.VISIBLE);
                    grandPermissions(); //this will call the signIn automatically
                } else {
                    Log.i(TAG, "on start > onClick: user not logged in");
                    if(!mUserSignedInToFirebase) {
                        signIn(); //this wil call the intent automatically
                    } else {
                        Log.i(TAG, "onClick: everything is ookey");
                        goToMapsActivity();
                    }
                }
            }
        });
    }

    /**Is user signed in to firebase**/
    private boolean isUserSignedInToFirebase () {
        return (mFirebaseAuth.getCurrentUser() != null);
    }

    /**Google signin Intent, for that select google account screen**/
    private void signIn() {
        Log.i(TAG, "signIn: Entered");
        Toast.makeText(LoginActivity.this, "Please wait", Toast.LENGTH_LONG).show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**called when google sign in completes**/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: Entered");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Log.i(TAG, "Trying to get google sign in account from user");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(TAG, "onActivityResult: Got account from user");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed. Please retry", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**Connect the google account with firebase**/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i(TAG, "firebaseAuthWithGoogle: Entered");
        Log.d(TAG, "firebaseAuthWithGoogle with id:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUserSignedInToFirebase = true;
                            Log.i(TAG, "firebaseAuthWithGoogle > onComplete: successful");

                            //automatically calling the intent from here
                            Log.i(TAG, "firebaseAuthWithGoogle > onComplete: calling the intent");
                            goToMapsActivity();

                        }else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "firebaseAuthWithGoogle > onComplete: signInWithCredential is failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            mUserSignedInToFirebase = false;
                            //updateUI(null);
                        }
                    }
                });
        //TODO: Do i need the this listener? probably yes
    }


    /**Intent to Test activity**/
    private void goToMapsActivity() {
        Log.i(TAG, "theIntentMethod: Entered");
        //TODO: intent to the next activity
        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
    }

    /**Is permissions granted**/
    private boolean isPermissionsGranted () {
        return (ContextCompat.checkSelfPermission(this, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED);
    }

    /**request for permissions**/
    private void grandPermissions() {
        Log.i(TAG, "grandPermissions: Entered");
        Log.i(TAG, "grandPermissions: requesting to user");
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1234);
    }

    /**Check if the user allowed the permissions**/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: Entered");
        mPermissionGranted = false;
        if(requestCode == 1234 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mWarningTextView.setVisibility(View.GONE);
                Log.i(TAG, "onRequestPermissionsResult: permissions granted");
                Log.i(TAG, "onRequestPermissionsResult: calling the SignIn");
                signIn();
            } else {
                //here we need to do something, like inform the user grand permission
            }
        }
    }

}


/* TODO: error message on the xml file */