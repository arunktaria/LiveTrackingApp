package com.example.livetrackingapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.livetrackingapp.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    int maxtime = 1000;
    int distance = 1;
    LocationManager manager;
    DatabaseReference reference;
    LocationData locationData;
    Marker marker;
    LatLng latLng;
    GoogleMap mmap;
    Geocoder geocoder;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        } else {
            Toast.makeText(this, "permission Granted!", Toast.LENGTH_SHORT).show();
            }


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        latLng = new LatLng(10, 115);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.basemap);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);



        reference = FirebaseDatabase.getInstance().getReference().child("UserLocation");

       // locationData = new LocationData();

        //getLatlang();


    }


    public void startLocationtracking() {
        Toast.makeText(MapsActivity.this, "in locationtracing", Toast.LENGTH_SHORT).show();
        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "start tra permis", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
        else {
            Toast.makeText(MapsActivity.this, "start tra permis grant", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback1,Looper.getMainLooper());
        }
    }

    LocationCallback locationCallback1 = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            getLocation(locationResult.getLastLocation());
            super.onLocationResult(locationResult);
        }
    };



    public void getLocation(Location location) {
        Toast.makeText(MapsActivity.this, "getting Location", Toast.LENGTH_SHORT).show();
        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(latLng1);
        
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.yellocar));
        marker.setRotation(location.getBearing());
        marker.setAnchor((float) 0.5, (float) 0.5);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 18));
        mmap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mmap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("They"));
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
}