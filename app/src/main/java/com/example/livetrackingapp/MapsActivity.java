package com.example.livetrackingapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.livetrackingapp.databinding.ActivityMapsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    int maxtime = 1000;
    int distance = 1;
    LocationManager manager;
    DatabaseReference reference;
    float start_rotation;
    Marker marker;
    LatLng latLng;
    GoogleMap mmap;
    Geocoder geocoder;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for taking permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        } else {
            Toast.makeText(this, "permission Granted!", Toast.LENGTH_SHORT).show();
            }

        //creating location request object
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //declaring default latlang
        latLng = new LatLng(10, 115);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.basemap);
        mapFragment.getMapAsync(this);

       //for realtime database
        reference = FirebaseDatabase.getInstance().getReference().child("UserLocation");

    }

//method to get location
    public void startLocationtracking() {

        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Taking permission", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
        else {
            Toast.makeText(MapsActivity.this, "start Getting Location", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback1,Looper.getMainLooper());
        }
    }

    //getting last location using callback object
    LocationCallback locationCallback1 = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            setLocationMarker(locationResult.getLastLocation());
            super.onLocationResult(locationResult);
        }
    };


//setting marker to given location
    public void setLocationMarker(Location location) {

        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setAnchor((float) 0.6, (float) 0.5);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,17f));
        moveVechile(marker,location);
        rotateMarker(marker,location.getBearing(),start_rotation);

    }

    //on map ready to use
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mmap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("They"));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.yellocar));
        startLocationtracking();
        Toast.makeText(MapsActivity.this, "Map is ready...", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100)
        {
            Toast.makeText(MapsActivity.this, "permission granted!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MapsActivity.this, "permission Denied!", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }

            }
        });
    }







}