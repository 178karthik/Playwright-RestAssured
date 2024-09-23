package com.karthik178.apimanager.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.payload.PayloadBuilder;
import io.qameta.allure.Allure;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents FlattenUtils
 * @author Karthik T
 */
public class FlattenUtils {
    public static Map<String, Object> flatten(Map<String, Object> map) {
        return flatten(map, null);
    }

    public static Map<String, Object> stringToMap(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return mapper.readValue(path, new TypeReference<Map<String, Object>>() {});
    }


    public static Map<String, Object> jsonFileToMap(String path) throws IOException {
        ClassLoader cl = PayloadBuilder.class.getClassLoader();
        File localFile = new File(cl.getResource(path).getFile());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(localFile, new TypeReference<Map<String, Object>>() {});
    }

    private static Map<String, Object> flatten(Map<String, Object> map, String prefix) {
        Map<String, Object> flatMap = new HashMap<>();
        map.forEach((key, value) -> {
            String newKey = prefix != null ? prefix + "." + key : key;
            if (value instanceof Map) {
                flatMap.putAll(flatten((Map<String, Object>) value, newKey));
            } else if (value instanceof List) {
                // check for list of primitives
                if (((List<?>) value).size() > 0) {
                    Object element = ((List<?>) value).get(0);
                    if (element instanceof String || element instanceof Number || element instanceof Boolean) {
                        flatMap.put(newKey, value);
                    } else {
                        // check for list of objects
                        List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                        for (int i = 0; i < list.size(); i++) {
                            flatMap.putAll(flatten(list.get(i), newKey + "[" + i + "]"));
                        }
                    }
                }

            } else {
                flatMap.put(newKey, value);
            }
        });
        return flatMap;
    }

    public void validateOutboundWebookFields(String baselineWebhookPayloadPath, JsonNode actucalIntegrationLogsResponse) throws IOException {
        Allure.link("Refer this baseline webhook payload : baseline-payload.json" , "https://bitbucket.org/prismforce/sms_qa_automation/src/master/src/test/resources/" + baselineWebhookPayloadPath);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedWebhookFields = FlattenUtils.flatten(FlattenUtils.jsonFileToMap(baselineWebhookPayloadPath));
        AllureLogger.info("Baseline Webhook Payload : \n" + PayloadBuilder.readClassPathResource(baselineWebhookPayloadPath));
        AllureLogger.info("Actual Webhook Payload: \n" + actucalIntegrationLogsResponse.toString());
        JsonNode responseLog =  actucalIntegrationLogsResponse;
        String jsonFromResponse = objectMapper.writeValueAsString(responseLog);
        System.out.println("String to map : " + FlattenUtils.stringToMap(jsonFromResponse));
        Map<String, Object> feildsFromReponse = FlattenUtils.flatten(FlattenUtils.stringToMap(jsonFromResponse));
        AllureLogger.info("Baseline Payload fields" + expectedWebhookFields.keySet());
        AllureLogger.info("Actual Webhook fields" + feildsFromReponse.keySet());
        boolean testFailed = compareTwoSets(expectedWebhookFields.keySet(), feildsFromReponse.keySet());
        Assert.assertFalse(testFailed, "Decrepency in payload fields");
    }

    public boolean compareTwoSets(Set<String> baselineSet , Set<String> actualResponseSet){
        boolean testfailed = false;
        for(String baselineField : baselineSet){
            if (!actualResponseSet.contains(baselineField)){
                if (!baselineField.startsWith("headers")){
                    testfailed = true;
                }

                AllureLogger.error(String.format("!!Missing Field : %s in outbound webhook", baselineField));
            }
        }
        return testfailed;
    }

    public static String getUTCFormattedTime() {
        // Get current UTC time
        ZonedDateTime zonedDateTime = ZonedDateTime.now(java.time.ZoneOffset.UTC);

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");

        // Format the ZonedDateTime using the defined formatter
        String formattedTime = formatter.format(zonedDateTime);

        return formattedTime;
    }
}
