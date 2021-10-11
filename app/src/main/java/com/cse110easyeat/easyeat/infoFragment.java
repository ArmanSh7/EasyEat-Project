package com.cse110easyeat.easyeat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;
import com.cse110easyeat.swipeviewtools.Profile;
import com.cse110easyeat.swipeviewtools.RestaurantCard;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// TODO: TRY LIVEVIEW AND VIEWMODEL ASAP

public class infoFragment extends Fragment {
    private static final String TAG = "infoFragment";

    private NetworkVolleyManager networkManager;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    private PlaceHolderView cardDisplay;
    private ImageView image;
    private TextView nameField;
    private TextView distanceField;

    private Profile mProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // GET ALL THE FIELDS
        cardDisplay = (PlaceHolderView)view.findViewById(R.id.swipeView);
        image = (ImageView) view.findViewById(R.id.profileImageView);
        nameField = (TextView) view.findViewById(R.id.nameAgeTxt);
        distanceField = (TextView) view.findViewById(R.id.locationNameTxt);
        //TextView ratingField = (TextView) view.findViewById(R.id.ratingTxt);

        RestaurantCard acceptCard = btnFragment.getLastCardInfo();
        mProfile = acceptCard.getmProfile();
        mContext = getActivity().getApplicationContext();

        String distanceURL = mProfile.getDistanceURL();
        /** Call distanceMatrix API and extract the distance  */
        networkManager = NetworkVolleyManager.getInstance(getContext());
        networkManager.postRequestAndReturnString(distanceURL, new NetworkListener<String>() {
            @Override
            public void getResult(String result) {
                Pair<String, String> parsedRes = extractDistanceAndTime(result);
                Glide.with(mContext).load(mProfile.getImageUrl()).into(image);
                nameField.setText("Name: " + mProfile.getName() + "\nRating: " + mProfile.getRestaurantRating());
                //ratingField.setText("Ratings: " + mProfile.getRestaurantRating() + "\n");
                distanceField.setText("\nDistance: " + parsedRes.first+ "\nETA: " + parsedRes.second +
                        "\nPrice: " + mProfile.getPrice() + "\nAddress: " + mProfile.getAddress());
                Log.d(TAG, "backstack count: " + getActivity().getSupportFragmentManager().getBackStackEntryCount());
            }
        });
    }

    public Pair<String, String> extractDistanceAndTime(String apiResult) {
        String distResult = "Unknown";
        String timeResult = "Unknown";

        try {
            final JSONObject jsonResult = new JSONObject(apiResult);
            JSONArray apiJSONResult = jsonResult.getJSONArray("rows");

            JSONArray distTimeRes = apiJSONResult.getJSONObject(0).getJSONArray("elements");
            JSONObject distance = distTimeRes.getJSONObject(0).getJSONObject("distance");
            JSONObject duration = distTimeRes.getJSONObject(0).getJSONObject("duration");

            distResult = distance.getString("text");
            timeResult = duration.getString("text");


        } catch(JSONException e) {
            Log.d(TAG, "JSON Exception: " + e.getMessage());
        }
        return new Pair(distResult, timeResult);
    }

}