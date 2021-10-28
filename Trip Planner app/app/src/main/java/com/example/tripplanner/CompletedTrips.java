package com.example.tripplanner;

import java.util.List;

public class CompletedTrips {
    public TripData tripData;
    public List<String> toDo;

    public CompletedTrips() {
    }

    public CompletedTrips(TripData tripData, List<String> toDo, List<String> done) {
        this.tripData = tripData;
        this.toDo = toDo;
        this.done = done;
    }

    public TripData getTripData() {
        return tripData;
    }

    public void setTripData(TripData tripData) {
        this.tripData = tripData;
    }

    public List<String> getToDo() {
        return toDo;
    }

    public void setToDo(List<String> toDo) {
        this.toDo = toDo;
    }

    public List<String> getDone() {
        return done;
    }

    public void setDone(List<String> done) {
        this.done = done;
    }

    public List<String> done;

}
