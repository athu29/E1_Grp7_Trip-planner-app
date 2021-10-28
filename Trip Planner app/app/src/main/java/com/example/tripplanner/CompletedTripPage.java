package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CompletedTripPage extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tripName,fromLocation,toLocation,date;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;

    String tripId;


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private List<LocationData> route;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_trip_page);

        Intent in = getIntent();
        tripId = in.getStringExtra("tripId");



        tripName = (TextView) findViewById(R.id.tripNameTripPage);
        fromLocation = (TextView) findViewById(R.id.fromLocationTripPage);
        toLocation = (TextView) findViewById(R.id.toLocationTripPage);

        date = (TextView) findViewById(R.id.dateTripPage);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewTripPage);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        reference.child("Trips").child("completed").child(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    CompletedTrips completedTrips = snapshot.getValue(CompletedTrips.class);
//                    Log.i("hello", "hello");
                    TripData tripData = completedTrips.getTripData();
                    tripName.setText(tripData.tripName);
                    fromLocation.setText(tripData.fromLocation.address);
                    toLocation.setText(tripData.toLocation.address);
                    date.setText("END : " + tripData.getDay() + "/" +tripData.getMonth() + "/"+tripData.getYear());


                    route = tripData.getRoute();
                    PolylineOptions options = new PolylineOptions();
                    for(LocationData locationData : route){
                        options.add(new LatLng(locationData.getLatitude(),locationData.getLongitude()));
                    }

                    Polyline polyline1 = gMap.addPolyline(options
                            .color(Color.GRAY)
                            .clickable(true));
                    polyline1.setEndCap(new RoundCap());

                    polyline1.setJointType(JointType.ROUND);


                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.fromLocation.latitude, tripData.fromLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("FROM"));
                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("DESTINATION")).showInfoWindow();
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude), 13));




                } catch (Exception e) {
                    Toast.makeText(CompletedTripPage.this, "No Current Trip", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompletedTripPage.this, "Some Issue cant load", Toast.LENGTH_LONG).show();
            }
        });

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }

    public void toCompletedTaskPage(View view) {
        Intent in = new Intent(CompletedTripPage.this,CompletedTaskPage.class);
        in.putExtra("tripId",tripId);
        startActivity(in);

    }

    public void toHomePage(View view) {
        startActivity(new Intent(CompletedTripPage.this,HomePage.class));
    }

}