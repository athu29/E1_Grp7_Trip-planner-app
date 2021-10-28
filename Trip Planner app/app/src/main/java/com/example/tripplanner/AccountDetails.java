package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AccountDetails extends AppCompatActivity {

    private EditText mail,name,age,phone;
    private EditText currentTrip,tripCount;
    private ImageView imageProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mail = (EditText) findViewById(R.id.mailAccountText);
        age = (EditText) findViewById(R.id.ageAccountText);
        name = (EditText) findViewById(R.id.nameAccountText);
        phone = (EditText) findViewById(R.id.phoneAccountText);
        imageProfile = (ImageView) findViewById(R.id.accountImage);

        currentTrip = (EditText)findViewById(R.id.currentTripNameAccounDetails);
        tripCount = (EditText) findViewById(R.id.tripCompletedCountAccountDetails);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null) {
                    mail.setText(userprofile.mail.toString());
                    name.setText(userprofile.name.toString());
                    phone.setText(userprofile.phone.toString());

                    Picasso.get().load(userprofile.imageURL.toString()).into(imageProfile);

                    Log.i("hello", String.valueOf(userprofile.imageURL));
                    age.setText("Age : " + String.valueOf(userprofile.age), TextView.BufferType.EDITABLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountDetails.this,"Somethings not correct",Toast.LENGTH_LONG).show();

            }


        });
        reference.child(userId).child("Trips").child("current")
                .child("tripName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) == null){
                    currentTrip.setText("No Current Trips");

                }
                else{
                    currentTrip.setText("Current Trip : " + snapshot.getValue(String.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child(userId).child("Trips").child("completed")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    tripCount.setText("Completed Trips : " + snapshot.getChildrenCount());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void toHomePage(View view) {
        startActivity(new Intent(AccountDetails.this,HomePage.class));
    }
}