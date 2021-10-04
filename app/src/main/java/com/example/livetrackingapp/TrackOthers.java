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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.livetrackingapp.databinding.ActivityTrackOthersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackOthers extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    DatabaseReference reference;
    Marker marker;
    LatLng latLng;
    GoogleMap mmap;
    Geocoder geocoder;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location;
    ActivityTrackOthersBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityTrackOthersBinding.inflate(getLayoutInflater());
        SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.othersmap);
        mapFragment1.getMapAsync(TrackOthers.this);
        setContentView(bind.getRoot());

        //for realtime database
        reference=FirebaseDatabase.getInstance().getReference().child("UserLocation");

        //taking permission from user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        } else {
            Toast.makeText(this, "permission Granted!", Toast.LENGTH_SHORT).show();
        }
        //assing/creating location request object
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        latLng = new LatLng(10, 115);
        geocoder = new Geocoder(this);
        reference = FirebaseDatabase.getInstance().getReference().child("UserLocation");

    }

    //getting location data from realtime database
    public void startLocationtracking() {
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
        DataModel location=snapshot.getValue(DataModel.class);
       // Toast.makeText(TrackOthers.this, "start tracking...", Toast.LENGTH_SHORT).show();
         settingLocationMarker(location);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(TrackOthers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

    }


    //setting location marker
    public void settingLocationMarker(DataModel location) {

        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(latLng1);
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.yellocar));
        marker.setRotation((float) location.getBearing());
        marker.setAnchor((float) 0.5, (float) 0.5);
        mmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,18f));
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    //on map ready.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mmap = googleMap;

        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("They"));
        startLocationtracking();
        Toast.makeText(TrackOthers.this, "Map is ready...", Toast.LENGTH_SHORT).show();
    }

    //operation perform when location is granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100)
        {
            Toast.makeText(TrackOthers.this, "permission granted!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(TrackOthers.this, "permission Denied!", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}