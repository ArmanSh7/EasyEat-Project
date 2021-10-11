package com.cse110easyeat.controller;

import android.content.Context;
import android.util.Log;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.api.service.APIHandlerService;
import com.cse110easyeat.api.service.GooglePlacesAPIServices;
import com.cse110easyeat.database.injector.DatabaseHandlerInjector;
import com.cse110easyeat.database.service.DatabaseHandlerService;
import com.cse110easyeat.database.service.FirebaseHandlerService;
import com.cse110easyeat.database.injector.FirebaseHandlerInjector;

import java.util.ArrayList;

/* Weave the API and Database together */
public class EasyEatController {
    private DatabaseHandlerService databaseService;
    // TODO: Take care of the API injectors
    private APIHandlerService apiService;
    private static final String TAG = "EasyEatController";

    /* Constructors - use the corresponding injectors */
    public EasyEatController(Context ctx) {
        apiService = new GooglePlacesAPIServices();
        apiService.initializeAPIClient(ctx);
        // Replace this injector if need to change
        DatabaseHandlerInjector dbInjector = new FirebaseHandlerInjector();
        databaseService = dbInjector.getDataBaseHandlerService();
        databaseService.connectToDatabase();
    }

    // Display
    public void registerUser(String email, String fullName) {
        User newUser = new User(email, fullName);
        databaseService.writeToDatabase(newUser);
        Log.d(TAG, "Registered a new user");
    }

    public DatabaseHandlerService getDatabaseService() {
        return databaseService;
    }



}
