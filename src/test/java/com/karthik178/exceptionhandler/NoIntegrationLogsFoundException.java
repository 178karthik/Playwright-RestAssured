package com.karthik178.exceptionhandler;

import com.karthik178.apimanager.utils.AllureLogger;

public class NoIntegrationLogsFoundException extends Exception{

    public NoIntegrationLogsFoundException (String str)
    {
        super(str);
        AllureLogger.error(str);
    }
}
