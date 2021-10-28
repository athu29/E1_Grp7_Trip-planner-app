package com.example.tripplanner;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CompletedTripsAdapter extends FirebaseRecyclerAdapter<CompletedTrips,CompletedTripsAdapter.completedTripsViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CompletedTripsAdapter(@NonNull FirebaseRecyclerOptions<CompletedTrips> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull completedTripsViewHolder holder, int position, @NonNull CompletedTrips model) {
        holder.title.setText(model.tripData.tripName);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("hello", String.valueOf(position));

//                Intent
                Intent in = new Intent(view.getContext(),CompletedTripPage.class);
                in.putExtra("tripId",String.valueOf(position));
                view.getContext().startActivity(in);

            }
        });
    }

    @NonNull
    @Override
    public completedTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_trip,parent,false);

        return new completedTripsViewHolder(view);


    }

    class completedTripsViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        CardView cardView;

        public completedTripsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tripText);
            cardView = (CardView) itemView.findViewById(R.id.cardId);
        }
    }
}
