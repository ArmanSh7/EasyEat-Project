package com.cse110easyeat.database.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.network.listener.NetworkListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseHandlerService implements DatabaseHandlerService {
    private static final String TAG = "FirebaseHandlerService";

    private FirebaseDatabase firebaseDb;
    private ArrayList<User> dataQueryList;

    /* Function to connect to database */
    public void connectToDatabase() {
        dataQueryList = new ArrayList<User>();
        firebaseDb = FirebaseDatabase.getInstance();
        DatabaseReference dbConnection = firebaseDb.getReference();

        dbConnection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Error connect " + error.toString());
            }
        });
    }

    // TODO: FIGURE THE WRITE DATABASE STRUCTURE
    /* Function to read data from Firebase */
    public ArrayList<User> getDataFromDatabase(final String userId,
                                               final NetworkListener<User> dbListener) {
        dataQueryList.clear();
        DatabaseReference dbReference = firebaseDb.getReference("Users");
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            //User userFound;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(userId).exists()) {
                        User userFound = dataSnapshot.child(userId).getValue(User.class);
                        dbListener.getResult(userFound);
                    } else {
                        dbListener.getResult(null);
                        Log.d(TAG, " No users found with that email");
                    }
                } else {
                    dbListener.getResult(null);
                    Log.d(TAG, "onDataChange of query " + "Snapshot does not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query cancelled" + databaseError.toString());
                dbListener.getResult(null);
            }
        });

        return dataQueryList;
    }

    public boolean writeToDatabase(final User data) {
        DatabaseReference dbConnection = firebaseDb.getReference("Users");
        dbConnection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference dbCon = firebaseDb.getReference("Users");
                boolean unique = true;
                dataQueryList.clear();
                String dataKey = data.getId();
                if (dataSnapshot.child(dataKey).exists()) {
                    User userFound = dataSnapshot.child(dataKey).getValue(User.class);
                    dataQueryList.add(userFound);
                    Log.d(TAG, "snapshot exists" + " Data Query Testing");
                    unique = false;
                }

                if (unique) {
                    dbCon.child(dataKey).setValue(data);
                    dataQueryList.add(data);
                    Log.d(TAG, "Writing to DB since it's not a duplicate");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Something wrong with writing or error");
            }
        });

        if(dataQueryList.size() == 1) {
            return true;
        }
        return false;
    }


}
