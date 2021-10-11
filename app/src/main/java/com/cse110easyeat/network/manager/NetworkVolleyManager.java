package com.cse110easyeat.network.manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cse110easyeat.network.listener.NetworkListener;

/* Singleton class to handle Volley HTTP requests(resolve threading issues) */
public class NetworkVolleyManager implements NetworkManagerService {
    private static NetworkVolleyManager managerInstance;
    private RequestQueue requestQueue;
    private static Context applicationContext;
    private static final String TAG = "NetworkVolleyManager: ";

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(applicationContext.getApplicationContext());
        }
        return requestQueue;
    }

    private NetworkVolleyManager(Context context) {
        applicationContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkVolleyManager getInstance(Context ctx) {
        if (managerInstance == null) {
            managerInstance = new NetworkVolleyManager(ctx);
        }
        return managerInstance;
    }

    public static synchronized NetworkVolleyManager getInstance() {
        if (managerInstance == null)
        {
            Log.d(TAG, "managerInstance found to be null");
            throw new IllegalStateException(NetworkVolleyManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return managerInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void postRequestAndReturnString(String url, final NetworkListener<String> customListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        if (response != null) {
                            customListener.getResult(response);
                            Log.i(TAG, "Result is " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e(TAG, "Volley error: " + error);
                    }
                });

        addToRequestQueue(stringRequest);
    }

}
