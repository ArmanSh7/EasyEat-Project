package com.cse110easyeat.api.service;

import android.content.Context;

public interface APIHandlerService {
    // Initializer
    void initializeAPIClient(Context ctx);

    // Spit out the whole API call result
    String getRestaurantInfo(String queryString, int minPrice, int maxPrice,
                                    float latitude, float longitude, float radius);

}

