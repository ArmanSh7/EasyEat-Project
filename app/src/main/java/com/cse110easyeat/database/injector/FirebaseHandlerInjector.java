package com.cse110easyeat.database.injector;

import com.cse110easyeat.database.service.FirebaseHandlerService;

public class FirebaseHandlerInjector implements DatabaseHandlerInjector {
    public FirebaseHandlerService getDataBaseHandlerService() {
        return new FirebaseHandlerService();
    }
}
