package com.cse110easyeat.database.service;

import android.content.Context;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.network.listener.NetworkListener;

import java.util.ArrayList;

public interface DatabaseHandlerService {
    void connectToDatabase();

    boolean writeToDatabase(final User data);
    ArrayList<User> getDataFromDatabase(final String userId, final NetworkListener<User> dbListener);
}
