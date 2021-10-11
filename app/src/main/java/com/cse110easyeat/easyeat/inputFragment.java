package com.cse110easyeat.easyeat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110easyeat.api.service.GooglePlacesAPIServices;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.android.volley.VolleyLog.TAG;

public class inputFragment extends Fragment {
    private final String TAG = "InputFragment";
    private EditText budgetField;
    private EditText distanceField;
    private EditText timeField;

    private int budget;
    private int distance;
    private int timeToWait;
    private float longitude;
    private float latitude;

    private ProgressDialog progressCircle;

    private NetworkVolleyManager networkManager;
    private GooglePlacesAPIServices apiHelper;

    private FusedLocationProviderClient locationClient;

    // TODO: WHY IS IT NOT ENDING
    // TODO: IMAGE IS STILL A BIT TOO LARGE
    // TODO: onPostExecute

    /**
     * This function is used to call the GooglePlacesAPI and parse the result into a JSONArray
     * to be used to render information with the SwipePlaceHolderView class
     *
     * @param apiResult - result from api call
     * @return a JSONArray that contains the parsed information corresponding to the Profile class
     */
    private JSONArray writeDataToJsonFile(String apiResult, final double latitude,
                                          final double longitude) {
        final JSONArray arrToWrite = new JSONArray();
        try {
            final JSONObject jsonResult = new JSONObject(apiResult);
            JSONArray resultsArr = jsonResult.getJSONArray("results");
            Log.d(TAG, "parsed result array: \n" + resultsArr.toString());

            // TODO: EXTRACT THE RATING, NAME, DISTANCE, IMAGE URL
            // TODO: USE DISTANCE MATRIX API TO GET DISTANCE
            // TODO: EXTRACT FORMATED ADDRESS FIELD
            // TODO: GET THE PLACE_ID
            for (int i = 0; i < resultsArr.length(); i++) {
                Log.d(TAG, "Results length: " + resultsArr.length());

                // TODO: figure out how to get distance
                // TODO: IMPORTANT SOME API FIELDS MIGHT BE MISSING
                try {
                    String height = "220";
                    String width = "150";

                    JSONObject restaurantRes = resultsArr.getJSONObject(i);
                    String photoRef = restaurantRes.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                    String address = restaurantRes.getString("vicinity");
                    String priceLevel = restaurantRes.getString("price_level");
                    String priceIcon = "";

                    int price = Integer.parseInt(priceLevel);
                    for (int j = 0; j < price; j++) {
                        priceIcon += "$";
                    }

                    jsonResult.put("address", address);
                    jsonResult.put("price", priceIcon);
                    jsonResult.put("distance", "3" + " miles");
                    jsonResult.put("rating", restaurantRes.getString("rating"));
                    jsonResult.put("name", restaurantRes.getString("name"));

                    String placeId = restaurantRes.getString("place_id");
                    String totalNumRatings = restaurantRes.getString("user_ratings_total");

                    String imageURL = generateImageURL(photoRef, height, width);

                    jsonResult.put("url", imageURL);
                    Log.d(TAG, "json res: \n" + jsonResult.toString());
                    String distanceURL = generateDistanceURL(latitude, longitude, placeId);
                    jsonResult.put("distanceURL", distanceURL);
                    arrToWrite.put(jsonResult.toString());

                } catch (final JSONException e) {
                    Log.d(TAG, "error found: " + e.getMessage());
                    continue;
                } catch (NumberFormatException e) {
                    Log.d(TAG, "Weird result from aPI");
                }
            }

            // USE THIS TO GET DISTANCE
//                networkManager.postRequestAndReturnString(distanceURL, new NetworkListener<String>() {
//                    @Override
//                    public void getResult(String result) {
//                        Log.d(TAG, "Distance API Call result: \n" + result);
//                        arrToWrite.put(jsonResult.toString());
//                    }
//                });
        } catch (final JSONException e) {
            Log.d(TAG, "error found: " + e.getMessage());
        }

        return arrToWrite;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requestPermission();
        return inflater.inflate(R.layout.input_layout, container, false);
    }

    /**
     * Function called when the fragment view is created.
     * <p>
     * MAIN CONCERNS: CALLBACK HELL | REFACTOR WITH LIVEVIEW TO PREVENT UI THREAD BLOCK
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        progressCircle = new ProgressDialog(getActivity());

        networkManager = NetworkVolleyManager.getInstance(getContext());
        apiHelper = new GooglePlacesAPIServices();
        Button btn = (Button) view.findViewById(R.id.submitButton);
        btn.setOnClickListener(new View.OnClickListener() {

            /**
             * The callback function first checks whether the user enables permission.
             * The function will then try to get the latest user's location and on success
             * it will make an api call that will find the restaurants that are on a certain
             * radius to the users
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                // DO THE API CALL
                requestPermission();
                progressCircle.setMessage("Validating input fields...");
                progressCircle.show();
                if (validateInputFields()) {
                    // TODO: TESTING API CALL
                    progressCircle.setMessage("Finding Easiest Eats...");
                    Log.d(TAG, "Longitude: " + longitude);
                    Log.d(TAG, "Latitude: " + latitude);

                    locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.d(TAG, "Location obtained: " + location.toString());
                                    final Location curLocation = location;
                                    String queryURL = apiHelper.generateAPIQueryURL(1,
                                            budget, curLocation.getLatitude(), curLocation.getLongitude(), distance);
                                    networkManager.postRequestAndReturnString(queryURL, new NetworkListener<String>() {
                                        @Override
                                        public void getResult(String result) {
                                            // write the results to a json file
                                            Log.d(TAG, "API RESULTS:\n" + result);
                                            JSONArray test = writeDataToJsonFile(result,
                                                    curLocation.getLatitude(), curLocation.getLongitude());

                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                            btnFragment fragClass = new btnFragment();
                                            /**
                                             * Pass in the parsed api results in to the infoFragment
                                             */
                                            Bundle bundle = new Bundle();
                                            bundle.putString("data", test.toString());
                                            fragClass.setArguments(bundle);

                                            ft.replace(R.id.mainFragment, fragClass);
                                            ft.addToBackStack(null);
                                            if (btnFragment.restaurantList != null) {
                                                btnFragment.restaurantList.clear();
                                            }
                                            progressCircle.hide();
                                            progressCircle.dismiss();
                                            ft.commit();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

        timeField = (EditText) view.findViewById(R.id.timeInput);
        distanceField = (EditText) view.findViewById(R.id.distanceInput);
        budgetField = (EditText) view.findViewById(R.id.budgetInput);

    }

    private boolean validateInputFields() {
        if (budgetField.getText().toString().matches("") ||
                distanceField.getText().toString().matches("") ||
                timeField.getText().toString().matches("")) {
            Log.d(TAG, "One of the input fields is empty");
            progressCircle.hide();
            Toast.makeText(getActivity(), "Please fill all the fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            budget = Integer.parseInt(budgetField.getText().toString());
            if (budget <= 0 || budget > 4) {
                progressCircle.hide();
                Toast.makeText(getActivity(), "Please enter budget between 1-4",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            distance = Integer.parseInt(distanceField.getText().toString());
            timeToWait = Integer.parseInt(timeField.getText().toString());
        } catch (NumberFormatException e) {
            progressCircle.hide();
            Toast.makeText(getActivity(), "Please enter integers", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // TODO: REFACTOR THESE METHODS
    public String generateImageURL(String photoReference, String height, String width) {
        String imageURL = "https://maps.googleapis.com/maps/api/place/photo?key=%s&photoreference=%s&maxheight=%s&maxwidth=%s";
        imageURL = String.format(imageURL, BuildConfig.PLACES_API_KEY, photoReference,
                height, width);
        return imageURL;
    }

    public String generateDistanceURL(double currentLat, double currentLong, String destinationId) {
        String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f" +
                "&destinations=place_id:%s&units=imperial&key=%s";
        distanceURL = String.format(distanceURL, currentLat, currentLong, destinationId,
                BuildConfig.PLACES_API_KEY);
        return distanceURL;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}