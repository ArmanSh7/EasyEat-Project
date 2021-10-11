package com.cse110easyeat.network.injector;

import android.content.Context;

import com.cse110easyeat.network.manager.NetworkManagerService;
import com.cse110easyeat.network.manager.NetworkVolleyManager;

public class VolleyManagerInjector  implements NetworkManagerInjector {
    public NetworkManagerService getNetworkManager(Context ctx) {
        return NetworkVolleyManager.getInstance(ctx);
    }
}
