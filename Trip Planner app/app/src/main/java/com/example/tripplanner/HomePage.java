package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tripName, fromLocation, toLocation;
    private SupportMapFragment mapFragment;
    private Button addButton;

    private GoogleMap gMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private ConstraintLayout currentTrip;

    private double currentLat, currentLong;


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private List<LocationData> route;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    RecyclerView completedTrips;
    CompletedTripsAdapter completedTripsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        tripName = (TextView) findViewById(R.id.tripNameHomePage);
        fromLocation = (TextView) findViewById(R.id.fromLocationHomePage);
        toLocation = (TextView) findViewById(R.id.toLocationHomePage);
//        date = (TextView) findViewById(R.id.dateTripPage);

        addButton = (Button) findViewById(R.id.addTripButtonHomePage);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewHomePage);


        mapFragment.getMapAsync(this);

        reference.child("Trips").child("current").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    TripData tripData = snapshot.getValue(TripData.class);
//                    Log.i("hello", "hello");
                    tripName.setText(tripData.tripName);

                    fromLocation.setText(tripData.fromLocation.address);
                    toLocation.setText(tripData.toLocation.address);



                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.fromLocation.latitude, tripData.fromLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("FROM"));
                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("DESTINATION")).showInfoWindow();
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude), 12));
                    addButton.setVisibility(View.INVISIBLE);

                    if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //When permission granted
                        locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(60*60*1000);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
//                                Log.i("Hello","hell");
                                if (locationResult == null) {
                                    return;
                                }
                                for (Location location : locationResult.getLocations()) {

                                    if (location != null && false) {
                                        currentLat = location.getLatitude();
                                        currentLong = location.getLongitude();

                                        reference.child("Trips")
                                                .child("current")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        route = snapshot.getValue(TripData.class).getRoute();

                                                        route.add(new LocationData(currentLat,currentLong));

                                                        reference.child("Trips").child("current").child("route")
                                                                .setValue(route);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(HomePage.this, "Failed to get Route Data from DB", Toast.LENGTH_LONG).show();

                                                    }
                                                });
                                    }
                                }
                            }
                        };
                        if (fusedLocationProviderClient != null) {
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                        }
                    }
                    else {
                        ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }


                } catch (Exception e) {
                    addButton.setVisibility(View.VISIBLE);
                    Toast.makeText(HomePage.this, "No Current Trip", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "Some Issue cant load", Toast.LENGTH_LONG).show();
            }
        });





//        Log.i("lat", String.valueOf(route.get(0).getLatitude()));
//        Log.i("lat lang",String.valueOf(route.get(0).getLongitude()));
//        route.add(new LocationData(currentLat, currentLong));
//

        currentTrip = (ConstraintLayout) findViewById(R.id.currentTripHomePage);

        currentTrip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent in = new Intent(HomePage.this,TriipPage.class);
                startActivity(in);
                return false;
            }
        });

        completedTrips = (RecyclerView) findViewById(R.id.completedTripsHomePage);
        completedTrips.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<CompletedTrips> options =
                new FirebaseRecyclerOptions.Builder<CompletedTrips>()
                        .setQuery(reference
                                .child("Trips")
                                .child("completed"),CompletedTrips.class)
                                .build();
        completedTripsAdapter = new CompletedTripsAdapter(options);
        completedTrips.setAdapter(completedTripsAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        completedTripsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        completedTripsAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(HomePage.this,"Signed Out",Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomePage.this,MainActivity.class));


    }

    public void toAccountDetails(View view) {
        startActivity(new Intent(HomePage.this,AccountDetails.class));
    }

    public void toCreateTrip(View view) {
        startActivity(new Intent(HomePage.this,CreateTrip.class));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }
}


//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }

//            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    //Initialize location
//                    Location location = task.getResult();
//                    if (location != null) {
//                        currentLat = location.getLatitude();
//                        currentLong = location.getLongitude();
//
//                        reference.child("Trips")
//                                .child("current")
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        route = snapshot.getValue(TripData.class).getRoute();
//
//                                        route.add(new LocationData(currentLat,currentLong));
//
//                                        reference.child("Trips").child("current").child("route")
//                                                .setValue(route);
////                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                    @Override
////                                                    public void onComplete(@NonNull Task<Void> task) {
////                                                        if (task.isSuccessful()) {
////                                                            Toast.makeText(HomePage.this, "Added  route Successfully", Toast.LENGTH_LONG).show();
////                                                            startActivity(new Intent(HomePage.this, HomePage.class));
////                                                        } else {
////                                                            Toast.makeText(HomePage.this, "Failed to Add Trip", Toast.LENGTH_LONG).show();
////                                                        }
////                                                    }
////                                                });
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        Toast.makeText(HomePage.this, "Failed to get Route Data from DB", Toast.LENGTH_LONG).show();
//
//                                    }
//                                });
//                    }
//                }
//            });