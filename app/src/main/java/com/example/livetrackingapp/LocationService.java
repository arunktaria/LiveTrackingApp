package com.example.livetrackingapp;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service implements OnMapReadyCallback,LocationListener{
    Location location;
    LocationListener listener;
    DatabaseReference reference;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "in service", Toast.LENGTH_SHORT).show();
        reference= FirebaseDatabase.getInstance().getReference().child("UserLocation");

        return super.onStartCommand(intent, flags, startId);
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "in bond", Toast.LENGTH_SHORT).show();
        return null;
    }



    @Override
    public void onDestroy() {
        Toast.makeText(this, "service stopped!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "ready map", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "location in", Toast.LENGTH_SHORT).show();

    }

}
