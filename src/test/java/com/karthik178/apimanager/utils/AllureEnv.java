package com.karthik178.apimanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Represents AllureEnv
 * @author Karthik T
 */
public final class AllureEnv {
    private static final Properties props;
    private static final Logger logger = LogManager.getLogger(AllureEnv.class);

    private AllureEnv() { }

    static {
        props = new Properties();
    }

    public static void createAllureEnvironmentFile(Map<String, String> propsConfig, String pathToAllureResultsDir) {
        propsConfig.forEach(props::setProperty);

        try(FileOutputStream fos = new FileOutputStream(new File(pathToAllureResultsDir + "/environment.properties"))) {
            props.store(fos, "Environment properties data for Allure report");
            logger.info("Allure environment.properties file has been created and stored successfully in the " + pathToAllureResultsDir);
        } catch (IOException e) {
            logger.error("OOPS! Something went wrong with environment.properties file creation. \n");
            e.printStackTrace();
        }

    }

    public static void createAllureEnvironmentFile(Map<String, String> propsConfig) {
        createAllureEnvironmentFile(propsConfig, System.getProperty("user.dir") + "/allure-results/");
    }

}