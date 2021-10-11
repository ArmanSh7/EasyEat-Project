package com.cse110easyeat.swipeviewtools;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("rating")
    @Expose
    private String restaurantRating;

    @SerializedName("distance")
    @Expose
    private String distanceFromCurLoc;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("distanceURL")
    @Expose
    private String distanceURL;

    // Rename the functions
    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRestaurantRating() {
        return restaurantRating;
    }

    public String getDistanceFromCurLoc() {
        return distanceFromCurLoc;
    }

    public String getAddress() { return address; }

    public String getPrice() {return price; }

    public String getDistanceURL() {return distanceURL;}
}
