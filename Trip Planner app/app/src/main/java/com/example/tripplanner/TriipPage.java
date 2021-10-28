package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TriipPage extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tripName,fromLocation,toLocation,date;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private List<LocationData> route;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triip_page);

        tripName = (TextView) findViewById(R.id.tripNameTripPage);
        fromLocation = (TextView) findViewById(R.id.fromLocationTripPage);
        toLocation = (TextView) findViewById(R.id.toLocationTripPage);

        date = (TextView) findViewById(R.id.dateTripPage);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewTripPage);
        mapFragment.getMapAsync(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        reference.child("Trips").child("current").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    TripData tripData = snapshot.getValue(TripData.class);
//                    Log.i("hello", "hello");
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


                    // Position the map's camera near Alice Springs in the center of Australia,
                    // and set the zoom factor so most of Australia shows on the screen.



                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.fromLocation.latitude, tripData.fromLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("FROM"));
                    gMap.addMarker(new MarkerOptions().position(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("DESTINATION")).showInfoWindow();
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripData.toLocation.latitude, tripData.toLocation.longitude), 13));




                } catch (Exception e) {
                    Toast.makeText(TriipPage.this, "No Current Trip", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TriipPage.this, "Some Issue cant load", Toast.LENGTH_LONG).show();
            }
        });

    }

            @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }


    public void toHomePage(View view) {
        startActivity(new Intent(TriipPage.this,HomePage.class));
    }

    public void toTaskPage(View view) {
        startActivity(new Intent(TriipPage.this,TaskPage.class));

    }

    public void completeGivenTrip(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(TriipPage.this);
                builder.setMessage("Do you want to Complete this Trip ? ").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        completeGivenTripFunction(view);
                        startActivity(new Intent(TriipPage.this,HomePage.class));
                    }
                }).setNegativeButton("Cancel", null)
                        .show();


            }




    public void completeGivenTripFunction(View view) {

        reference.child("Trips").child("current").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TripData currentTrip = snapshot.getValue(TripData.class);
                reference.child("Trips").child("current task").child("to do")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<String> currentToDoTask = (List<String>) snapshot.getValue();
                                reference.child("Trips").child("current task").child("done")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                List<String> currentDoneTask = (List<String>) snapshot.getValue();
                                                reference.child("Trips").child("completed")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                reference.child("Trips").child("completed").child(String.valueOf(snapshot.getChildrenCount()))
                                                                        .setValue(new CompletedTrips(currentTrip,currentToDoTask,currentDoneTask)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(getBaseContext(), "Trip Completed", Toast.LENGTH_LONG).show();

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
