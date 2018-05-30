package com.maximzedits.hitchtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //vars
    private boolean isPermissionGranted = false;
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
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initializing map objects
        mGoogleMap = googleMap;
        mFusedLocationProviderClient = new FusedLocationProviderClient(this);

        checkPermissionGranted();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        findMyLocation();

    }

    private void findMyLocation() {
        Toast.makeText(this, "Trying to get current location", Toast.LENGTH_LONG).show();
        Log.i(TAG, "findMyLocation: Entered");
        try {
            if(isPermissionGranted) {
                Task<Location> currentLocation = mFusedLocationProviderClient.getLastLocation();
                currentLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Moving camera");
                            mLastKnownLocation = task.getResult();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.i(TAG, "onComplete: current location is null");
                            Log.i(TAG, "onComplete: excepting is: " + task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.i(TAG, "findMyLocation: Security exception: " + e);
        }

    }

    /**enable or disable ui elements**/
    private void updateLocationUI() {
        Log.i(TAG, "updateLocationUI: Entered");
        if (mGoogleMap == null) {
            Log.i(TAG, "updateLocationUI: mMap is null");
            return;
        }
        try {
            if (isPermissionGranted) {
                Log.i(TAG, "updateLocationUI: permission granted");
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                Log.i(TAG, "updateLocationUI: permission not granted");
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
//                checkPermissionGranted();
            }
        } catch (SecurityException e)  {
            Log.i(TAG, "updateLocationUI: Exception: " + e.getMessage());
        }
    }

    private void checkPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: Entered");
        isPermissionGranted = false;
        if(requestCode == 1234 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;
                updateLocationUI();
                findMyLocation();
            }
        }

    }
}
