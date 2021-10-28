package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompletedTaskPage extends AppCompatActivity {
    private ListView toDoList,doneList;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private List<String> tasks = new ArrayList<>();
    private List<String> doneTasks = new ArrayList<>();

    private String tripId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task_page);

        Intent in = getIntent();
        tripId = in.getStringExtra("tripId");


        toDoList = (ListView) findViewById(R.id.toDoTaskPage);
        doneList = (ListView) findViewById(R.id.doneTaskPage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks);
        toDoList.setAdapter(adapter);

        ArrayAdapter<String> doneTasksAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, doneTasks);
        doneList.setAdapter(doneTasksAdapter);


        reference.child("Trips").child("completed").child(tripId).child("toDo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<String> task = (List<String>) snapshot.getValue();
                        if (task!=null){
                            for (String i : task) {
                                tasks.add(i);
                            }
                            adapter.notifyDataSetChanged();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        reference.child("Trips").child("completed").child(tripId).child("done")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<String> task = (List<String>) snapshot.getValue();
                        if (task!=null){
                            for (String i : task) {
                                doneTasks.add(i);
                            }
                            doneTasksAdapter.notifyDataSetChanged();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
}