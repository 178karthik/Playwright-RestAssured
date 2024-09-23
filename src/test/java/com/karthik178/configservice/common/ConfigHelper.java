package com.karthik178.configservice.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.model.Mode;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.payload.PayloadBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigHelper {

    private static final Logger logger = LogManager.getLogger(ConfigHelper.class);

    public static String getEnv(String key) {
        String env;
        if (!Objects.isNull(System.getProperty("env"))) {
            env = System.getProperty("env");
        } else if (!Objects.isNull(System.getenv("env"))){
            env = System.getenv("env");
        } else {
            throw new InvalidParameterException("\nERROR ::Environment info is not passed in command-line arguments. How to pass environment info ? ::  '-Denv=uat' in commandline or '-e env=uat' for docker run ");
        }
        return env;
    }

    public static Secret getSecret(String key) {
        String env = getEnv(key);
        System.setProperty("env", env);
        String app = System.getProperty("app");
        logger.info(env.toUpperCase() + " Environment is selected");

        ObjectMapper objectMapper = new ObjectMapper();
        String secretString;
        if (Objects.nonNull(app)) {
            logger.info(app.toUpperCase() + " APP is selected");
            secretString = PayloadBuilder.readClassPathResource(String.format("configurations/%s/%s/secret.json",app, env));
        } else {
            secretString = PayloadBuilder.readClassPathResource(String.format("configurations/sms/%s/secret.json", env));
        }
        List<Secret> allSecrets = null;
        try {
            allSecrets = objectMapper.readValue(secretString, new TypeReference<List<Secret>>(){});
            List<Secret> targetSecret = allSecrets.stream().filter(secret -> secret.getKey().contentEquals(key)).collect(Collectors.toList());
            if (targetSecret.size() == 0) {
                throw new RuntimeException(
                        String.format("'%s' key doesn't exist, please check secret file: %s", key, allSecrets.toString()));
            }
            if (targetSecret.size() > 1) {
                logger.warn(key + " Duplicate keys exist in secret file");
            }
            return targetSecret.get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Secret getSecret(String key, String env) {
        String app = System.getProperty("app");
        logger.info(env.toUpperCase() + " Environment is selected");
        ObjectMapper objectMapper = new ObjectMapper();
        String secretString;
        if (Objects.nonNull(app)) {
            logger.info(app.toUpperCase() + " APP is selected");
            secretString = PayloadBuilder.readClassPathResource(String.format("configurations/%s/%s/secret.json",app, env));
        } else {
            secretString = PayloadBuilder.readClassPathResource(String.format("configurations/sms/%s/secret.json", env));
        }
        List<Secret> allSecrets = null;
        try {
            allSecrets = objectMapper.readValue(secretString, new TypeReference<List<Secret>>(){});
            List<Secret> list = allSecrets.stream().filter(secret -> secret.getKey().contentEquals(key)).collect(Collectors.toList());
            if (list.size() != 1){
                return null;
            }
            return list.get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Secret getSecret(String key, String env, String type) {
        ObjectMapper objectMapper = new ObjectMapper();
        String secretString = PayloadBuilder.readClassPathResource(String.format("configurations/sms/%s/%s.json", env, type));
        List<Secret> allSecrets = null;
        if (Objects.isNull(allSecrets)) {
            throw new RuntimeException("Unable to find secret");
        }
        try {
            allSecrets = objectMapper.readValue(secretString, new TypeReference<List<Secret>>(){});
            List<Secret> list = allSecrets.stream().filter(secret -> secret.getKey().contentEquals(key)).collect(Collectors.toList());
            if (list.size() != 1){
                return null;
            }
            return list.get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Mode playwrightConfigurations() {
        String mode = "";
        if (Objects.nonNull(System.getProperty("mode"))) {
            mode = System.getProperty("mode");
        } else {
            mode = "local";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String executionModeFile = PayloadBuilder.readClassPathResource(String.format("configurations/uiexecution/%s-mode.json", mode));
        Mode Mode = null;
        try {
            Mode = objectMapper.readValue(executionModeFile, new TypeReference<Mode>(){});
            return Mode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
