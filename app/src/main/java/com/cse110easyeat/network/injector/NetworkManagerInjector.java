package com.cse110easyeat.network.injector;

import android.content.Context;

import com.cse110easyeat.network.manager.NetworkManagerService;

public interface NetworkManagerInjector {
    NetworkManagerService getNetworkManager(Context ctx);
}
