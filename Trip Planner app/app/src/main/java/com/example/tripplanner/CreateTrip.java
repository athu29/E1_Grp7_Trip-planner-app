package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnMapReadyCallback {
    //Initialize variable
    Button find_me;
    private TextView fromLocationTextView, dateEnd;
    private EditText tripName;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double fromLat,fromLang,toLat,toLong;
    private String fromAddress,fromLocality,toAddress,toLocality;
    private int day,month,year;
    private SearchView searchBar;

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        fromLocationTextView = findViewById(R.id.fromLocationCreateTrip);
        dateEnd = (TextView) findViewById(R.id.dateCreateTrip);
        tripName = (EditText) findViewById(R.id.tripNameCreateTrip);
        searchBar = (SearchView) findViewById(R.id.toLocationCreateTrip);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewCreateTrip);

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(CreateTrip.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            getLocation();
        } else {
            //When permission denied
            ActivityCompat.requestPermissions(CreateTrip.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchBar.getQuery().toString().trim();
                Log.i("hello",location);

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(CreateTrip.this);

                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                        int count = 10;
                        while (addressList.size()==0 && count>0) {
                            count=-1;
                            addressList = geocoder.getFromLocationName(location, 1);
                            Log.i("hello","hello");
                        }
                        if (addressList.size()>0) {
                            Log.i("hello","hello 1234");
                            toAddress = addressList.get(0).getAddressLine(0);
                            toLocality = addressList.get(0).getLocality();
                            toLat = addressList.get(0).getLatitude();
                            toLong = addressList.get(0).getLongitude();
                            LatLng latlng = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());

                        gMap.addMarker(new MarkerOptions().position(latlng).title(location));
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,18));}

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("hello","hello unsucessfully");

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                Log.i("hello","kya malum");
//                Log.i("hello", String.valueOf(location.getLongitude()));

                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(CreateTrip.this, Locale.getDefault());
                        Log.i("hello", String.valueOf(geocoder.isPresent()));

                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        fromLocationTextView.setText( "FROM : " + addresses.get(0).getAddressLine(0));
                        fromLat = addresses.get(0).getLatitude();
                        fromLang = addresses.get(0).getLongitude();
                        fromLocality = addresses.get(0).getLocality();
                        fromAddress = addresses.get(0).getAddressLine(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getDateClicker(View view) {
        new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .show();

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        day = i2;
        month = i1+1;
        year = i;
        dateEnd.setText(i2 + "/" + (i1+1) + "/" + i);
    }

    public void addTrip(View view) {
        String tripNameText = tripName.getText().toString().trim();
        LocationData fromLocation = new LocationData(fromAddress,fromLocality,fromLat,fromLang);
        LocationData toLocation = new LocationData(toAddress,toLocality,toLat,toLong);
        List<LocationData> route = new ArrayList<>();
        route.add(new LocationData(fromLat,fromLang));

        reference.child("Trips").child("current")
                .setValue(new TripData(fromLocation,toLocation,route,day,month,year,tripNameText))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CreateTrip.this, "Added " + tripName.getText().toString() + " Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateTrip.this,HomePage.class));
                } else {
                    Toast.makeText(CreateTrip.this, "Failed to Add Trip", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }
}