package com.cse110easyeat.database.injector;

import com.cse110easyeat.database.service.DatabaseHandlerService;

public interface DatabaseHandlerInjector {
    DatabaseHandlerService getDataBaseHandlerService();
}
