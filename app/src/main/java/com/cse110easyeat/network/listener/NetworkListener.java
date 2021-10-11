package com.cse110easyeat.network.listener;

/* A custom listener class that can be used during callback functions */
public interface NetworkListener<T> {
    void getResult(T result);
}
