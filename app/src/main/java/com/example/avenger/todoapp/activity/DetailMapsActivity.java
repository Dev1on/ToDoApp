package com.example.avenger.todoapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.model.Todo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import static com.example.avenger.todoapp.R.id.map;
import static com.example.avenger.todoapp.model.AppSettingsConstants.PERMISSIONS_REQUEST_LOCATION;
import static com.example.avenger.todoapp.model.Todo.Location;

public class DetailMapsActivity extends AppCompatActivity implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private MarkerOptions marker = null;
    private Geocoder coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        Button saveButton = (Button) findViewById(R.id.maps_action_save);
        coder = new Geocoder(DetailMapsActivity.this);

        saveButton.setOnClickListener(v -> {
            if (null != marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailMapsActivity.this);
                builder.setMessage("Do you want to select " + getLocation().getName() + "?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("location", getLocation());
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //do nothing
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(DetailMapsActivity.this, "No Location selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();

            String locName = "";
            try {
                locName = coder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            marker = new MarkerOptions().position(latLng).title(locName);

            Marker locationMarker = mMap.addMarker(marker);
            locationMarker.setDraggable(true);
            locationMarker.showInfoWindow();
        });

        Todo.Location loc = (Todo.Location)getIntent().getSerializableExtra("location");
        if (loc != null) {
            LatLng oldPos = new LatLng(loc.getLatlng().getLat(), loc.getLatlng().getLng());
            MarkerOptions marker = new MarkerOptions().position(oldPos).title(loc.getName());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldPos, 8f));
            mMap.addMarker(marker);
        }

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_LOCATION) {
            return;
        }

        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                mPermissionDenied = true;
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        Toast.makeText(DetailMapsActivity.this, "You have to enable location permission to use this function.", Toast.LENGTH_SHORT).show();
    }

    private Location getLocation() {
        Todo.LatLng latlng = new Todo.LatLng();
        latlng.setLat(marker.getPosition().latitude);
        latlng.setLng(marker.getPosition().longitude);
        Location loc = new Location();
        loc.setLatlng(latlng);
        loc.setName(marker.getTitle());

        return loc;
    }
}