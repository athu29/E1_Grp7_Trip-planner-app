package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class TaskPage extends AppCompatActivity {

    private ListView toDoList,doneList;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private List<String> tasks = new ArrayList<>();
    private List<String> doneTasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        toDoList = (ListView) findViewById(R.id.toDoTaskPage);
        doneList = (ListView) findViewById(R.id.doneTaskPage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks);
        toDoList.setAdapter(adapter);

        ArrayAdapter<String> doneTasksAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, doneTasks);
        doneList.setAdapter(doneTasksAdapter);


        reference.child("Trips").child("current task").child("to do")
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
        reference.child("Trips").child("current task").child("done")
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


        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskPage.this);
                builder.setMessage("Do you want to mark this Task as DONE ? ").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        String changedTask = tasks.get(i);
                        tasks.remove(i);
                        reference.child("Trips").child("current task").child("to do").removeValue();
                        reference.child("Trips").child("current task").child("to do").setValue(tasks);
                        doneTasks.add(changedTask);
//                        reference.child("Trips").child("current task").child("done").setValue(doneTasks);
                        reference.child("Trips").child("current task").child("done").child(String.valueOf(doneTasks.size()-1)).setValue(changedTask);

                        adapter.notifyDataSetChanged();
                        doneTasksAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "Task Added to DONE", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Cancel", null)
                        .show();
                return false;
            }

        });

        doneList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskPage.this);
                builder.setMessage("Do you want to DELETE this Task ? ").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        String changedTask = doneTasks.get(i);
                        doneTasks.remove(i);
                        reference.child("Trips").child("current task").child("done").setValue(doneTasks);

//                        adapter.notifyDataSetChanged();
                        doneTasksAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "Task Added to DONE", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Cancel", null)
                        .show();
                return false;
            }
        });

    }

    public void addTask(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(TaskPage.this);
        View mView = getLayoutInflater().inflate(R.layout.task_dialog,null);
        final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.add(txt_inputText.getText().toString().trim());
                reference.child("Trips").child("current task").child("to do").child(String.valueOf(tasks.size()-1)).setValue(txt_inputText.getText().toString().trim());
//                adapter.notifyDataSetChanged();


                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    }


    //        tasks.add("Majboori");
//        tasks.add("MArks");
//
//
//        reference.child("Trips").child("current task").child("to do").setValue(tasks);
//        tasks.add("VIVA");
//        reference.child("Trips").child("current task").child("done").setValue(tasks);

