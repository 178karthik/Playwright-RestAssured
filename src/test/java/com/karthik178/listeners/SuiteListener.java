package com.karthik178.listeners;

import com.karthik178.apimanager.model.ResponseEntity;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureEnv;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.apimanager.utils.JsonUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.annotations.BeforeSuite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents SMSConfigService
 * @author Karthik T
 */
public class SuiteListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        // When <suite> tag starts
        System.out.println("onStart: before suite starts");
        setAllureEnvironment();
    }

    @BeforeSuite
    public void init() {
        // Initialize the system before the test suite.
        System.out.println("onStart: before suite starts");
    }

    static void setAllureEnvironment() {
        Map<String, String> propsConfig = new HashMap<>();
        propsConfig.put("env", "UAT");
        propsConfig.put("System Info", System.getProperty("os.name"));
        propsConfig.put("Runner", "user.name");
        AllureEnv.createAllureEnvironmentFile(propsConfig);
    }

    @Override
    public void onFinish(ISuite suite) {
        Set<String> allEntities = suite.getAttributeNames();
        System.out.println("All deletable entities found :: " + allEntities);
        for (String entity : allEntities) {
            if (entity.startsWith("entity-")) {
                System.out.println("===================Delete Entity found==================");
                ResponseEntity responseEntity = (ResponseEntity) suite.getAttribute(entity);
                RestRequestDefinition definition = responseEntity.getDefinition();
                Response response = responseEntity.getResponse();
                String deleteTemplatePath = definition.getCleanup().get("source");
                RestRequestDefinition deleteDefinition = PayloadBuilder.mapJsonToRestDefinition(deleteTemplatePath);
                String deletePayload = PayloadBuilder.readClassPathResource(deleteTemplatePath);
                Set<String> keysToReplaceInDeletePayload = JsonUtils.iterateOverGroupKeys(deletePayload);
                JsonPath jsonPath = new JsonPath(definition.getBody().toString());
                Map<String, Object> replaceKeys = new HashMap<>();
                for (String key : keysToReplaceInDeletePayload) {
                    if (Objects.nonNull(jsonPath.get(key))) {
                        System.out.printf("%s key is present in definition. Value is %s\n DeleteDefinition :: %s", key, jsonPath.get(key), deleteDefinition.toString());
                        replaceKeys.put(key, jsonPath.get(key));
                    } else {
                        throw new RuntimeException(String.format("%s key is not present in definition", key));
                    }
                }
                PayloadBuilder.getResolvedDefinition(deleteDefinition, replaceKeys);
                Response responses = APIExecutor.execute(responseEntity.getUserContext(), deleteDefinition);
                AllureLogger.info(responses.getStatusLine());
                System.out.println("=====================================");

            }
        }
    }
}
