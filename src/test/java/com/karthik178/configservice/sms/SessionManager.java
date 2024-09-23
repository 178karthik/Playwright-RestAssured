package com.karthik178.configservice.sms;

import com.karthik178.apimanager.utils.AllureLogger;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents SessionManager
 * @author Karthik T
 */
public class SessionManager {

    private static Map<String, SMSConfigService> sessions = new ConcurrentHashMap<>();

    private static final Logger logger = LogManager.getLogger(SessionManager.class);

    @Step("Get SMS Client Session")
    public static SMSConfigService getSession(String sessionKey) {
        if (sessions.containsKey(sessionKey)){
            AllureLogger.info(logger, String.format("Getting Context :: Reuse existing context :: Key :: %s", sessionKey));
            return sessions.get(sessionKey);
        }
        AllureLogger.info(logger, String.format("Getting Context :: Create new context :: Key :: %s", sessionKey));
        SMSConfigService smsConfigService = new SMSConfigService(sessionKey);
        sessions.put(sessionKey, smsConfigService);
        return smsConfigService;
    }

    public static void main(String[] args) {
        SMSConfigService smsConfigService1 =  getSession("auto.admin");
        SMSConfigService smsConfigService2 =  getSession("auto.admin");

    }
}
