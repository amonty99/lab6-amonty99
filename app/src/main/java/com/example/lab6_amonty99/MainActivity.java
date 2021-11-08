package com.example.lab6_amonty99;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private FusedLocationProviderClient flpc;
    private final LatLng mDestinationLatLng = new LatLng(43.0752308,-89.4050011);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        flpc = LocationServices.getFusedLocationProviderClient(this);
        mf.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));
            displayMyLocation();
        });
    }

    private void displayMyLocation() {
        Log.i("displaying location", "top");
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission==PackageManager.PERMISSION_DENIED) {
            Log.i("dml", "DENIED");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Log.i("permission granted", "top");
            flpc.getLastLocation().addOnCompleteListener(this, task -> {
                Location mLastKnownLocation = task.getResult();
                Log.i("DISPLAY MY LOCATION", "CHECK IF TASK IS SUCCESSFUL");
                if (!task.isSuccessful()) {
                    Log.i("PROBLEM", "TASK UNSUCCESSFUL");
                }
                if (mLastKnownLocation == null) {
                    Log.i("PROBLEM", "NULL LAST KNOWN LOCATION");
                }
                if (task.isSuccessful() && (mLastKnownLocation != null)) {
                    Log.i("currLat", String.valueOf(mLastKnownLocation.getLatitude()));
                    Log.i("currLong", String.valueOf(mLastKnownLocation.getLongitude()));
                    LatLng currLoc = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currLoc).title("Current Location"));
                    mMap.addPolyline(new PolylineOptions().add(currLoc, mDestinationLatLng));
//                    SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
//                    mf.getMapAsync(googleMap -> {
//                        mMap = googleMap;
//                        googleMap.addMarker(new MarkerOptions().position(currLoc).title("Current Location"));
//                    });
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("orpr", "let's check permission granted");
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("orpr", "returning to display my location");
                displayMyLocation();
            }
        }
    }
}