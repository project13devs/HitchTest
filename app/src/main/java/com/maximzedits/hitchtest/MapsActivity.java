package com.maximzedits.hitchtest;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //vars
    private Location mLastKnownLocation = null;
    
    //static publics
    public static final String TAG = "TAG_INFORMATION";
    public static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //firebase initializations
        mFirebaseAuth = FirebaseAuth.getInstance();
        //If user is somehow null (may be when signed out), go to login
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onAuthStateChanged: Entered");
                if(firebaseAuth.getCurrentUser() == null) {
                    Log.i(TAG, "onAuthStateChanged: User logged out, intent to login activity");
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        //calls the listeners
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initializing map objects
        mGoogleMap = googleMap;
        mFusedLocationProviderClient = new FusedLocationProviderClient(this);


        // Turn on the My Location layer and the related control on the map.
//        updateLocationUI();
//
//        findMyLocation();

    }

    /**Find the device's location**/
//    private void findMyLocation() {
//        Toast.makeText(this, "Trying to get current location", Toast.LENGTH_LONG).show();
//        Log.i(TAG, "findMyLocation: Entered");
//        try {
//            if(isPermissionGranted) {
//                Task<Location> currentLocation = mFusedLocationProviderClient.getLastLocation();
//                currentLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if(task.isSuccessful()) {
//                            Log.i(TAG, "onComplete: Moving camera");
//                            mLastKnownLocation = task.getResult();
//                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        } else {
//                            Log.i(TAG, "onComplete: current location is null");
//                            Log.i(TAG, "onComplete: excepting is: " + task.getException());
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e) {
//            Log.i(TAG, "findMyLocation: Security exception: " + e);
//        }
//
//    }

    /**enable or disable ui elements, when permission is granted**/
//    private void updateLocationUI() {
//        Log.i(TAG, "updateLocationUI: Entered");
//        if (mGoogleMap == null) {
//            Log.i(TAG, "updateLocationUI: mMap is null");
//            return;
//        }
//        try {
//            if (isPermissionGranted) {
//                Log.i(TAG, "updateLocationUI: permission granted");
//                mGoogleMap.setMyLocationEnabled(true);
//                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
//                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//            } else {
//                Log.i(TAG, "updateLocationUI: permission not granted");
//                Toast.makeText(this, "Please allow required permissions to continue", Toast.LENGTH_SHORT).show();
//                mGoogleMap.setMyLocationEnabled(false);
//                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
//            }
//        } catch (SecurityException e)  {
//            Log.i(TAG, "updateLocationUI: Exception: " + e.getMessage());
//        }
//    }


}
