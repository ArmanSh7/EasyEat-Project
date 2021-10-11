package com.cse110easyeat.network.manager;

import com.cse110easyeat.network.listener.NetworkListener;

public interface NetworkManagerService {
    void postRequestAndReturnString(String url, final NetworkListener<String> customListener);
}
