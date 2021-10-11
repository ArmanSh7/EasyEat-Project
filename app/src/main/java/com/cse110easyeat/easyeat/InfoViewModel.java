package com.cse110easyeat.easyeat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.cse110easyeat.swipeviewtools.Profile;

public class InfoViewModel extends ViewModel {
    private MutableLiveData<Profile> restaurantProfile;

    public void init() {
        if (restaurantProfile != null) {
            return;
        }
    }

    public MutableLiveData<Profile> getCurrentRestaurant() {
        if (restaurantProfile == null) {
            restaurantProfile = new MutableLiveData<Profile>();
        }
        return restaurantProfile;
    }




}
