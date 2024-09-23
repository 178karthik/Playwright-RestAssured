package com.karthik178.apimanager.utils;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Represents AllureLogger
 * @author Karthik T
 */
public class AllureLogger {
    private static final Logger logger = LogManager.getLogger(AllureLogger.class);

    public static void info(String message) {
        logger.info(message);
        attachLogToAllure("INFO: " + message);
    }

    public static void info(Logger logger, String message) {
        logger.info(message);
        attachLogToAllure("INFO: " + message);
    }

    public static void info(UserContext userContext, RestRequestDefinition definition, String message) {
        String msg = String.format("%d :: %d\n%d", definition.getMethod().toString().toUpperCase(),
                definition.getUriPath(), message);
        logger.info(msg);
        attachLogToAllure("INFO: " + msg);
    }

    public static void error(String message) {
        logger.error(message);
        attachLogToAllure("ERROR: " + message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    @Attachment(value = "{logMessage}", type = "text/plain")
    private static String attachLogToAllure(String logMessage) {
        return logMessage;
    }

}
