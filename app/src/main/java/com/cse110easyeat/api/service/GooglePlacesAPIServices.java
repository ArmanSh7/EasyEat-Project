package com.cse110easyeat.api.service;

import com.cse110easyeat.easyeat.BuildConfig;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;

import android.content.Context;
import android.util.Log;


public class GooglePlacesAPIServices implements APIHandlerService {
    private NetworkVolleyManager requestManager;
    private static final String TAG = "GooglePlacesAPIServices";
    private static final double milesConverter = 1609.34;

    private static final String apiRequestURL = "https://maps.googleapis.com/maps/api/place" +
            "/nearbysearch/json?location=%f,%f&radius=%f&keyword=food&type=restaurant&key=%s";

    public void initializeAPIClient(Context ctx) {
        requestManager = NetworkVolleyManager.getInstance(ctx);
    }

    public String generateAPIQueryURL(int minPrice, int maxPrice,
                                      double latitude, double longitude, float radius) {
        String result = "";
        /* Replace whitespace with plus buttons */
        /** Convert miles to meters */
        double meterRadius = radius * milesConverter;

        String url = String.format(apiRequestURL, latitude, longitude,
                meterRadius, BuildConfig.PLACES_API_KEY);

        if (minPrice > 0 && minPrice <= 4) {
            url += "&minprice=";
            url += minPrice;
        }

        if (maxPrice > 0 && maxPrice <= 4) {
            url += "&maxprice=";
            url += maxPrice;
        }

        Log.d(TAG, "/****** API URL USED: " + url);
        return url;
    }


    // add minprice, maxprice opennow
    // Check if price is an option
    public String getRestaurantInfo(String queryString, int minPrice, int maxPrice,
                                    float latitude, float longitude, float radius) {

        String result = "";
        String url = generateAPIQueryURL(minPrice, maxPrice, latitude, longitude, radius);
        // TODO: Think of the flow
        requestManager.postRequestAndReturnString(url, new NetworkListener<String>() {
            @Override
            public void getResult(String result) {
                if (result != null) {
                    Log.d(TAG, "The API call is: \n" + result);
                }
            }
        });

        return result;
    }
}
