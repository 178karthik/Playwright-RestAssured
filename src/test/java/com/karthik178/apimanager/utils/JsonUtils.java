package com.karthik178.apimanager.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.sms.SMSConfigService;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents JsonUtils
 * @author Karthik T
 */
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils() {
        objectMapper = new ObjectMapper();
    }

    public static String readFileFromResources(String fileTemplate)  {

        String payloadString;
        try {
            payloadString = IOUtils.resourceToString(fileTemplate, StandardCharsets.UTF_8);
            return payloadString;
        } catch (Error | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set iterateOverGroupKeys(String input) {
        Pattern PROPERTY_FORMAT = Pattern.compile("\\{([a-zA-Z.:'(a-zA-Z)\\-_0-9]+[ a-zA-Z.:'(a-zA-Z)\\-_0-9&*+/@]+)}");
        String terminationString = input;
        Matcher matcher = PROPERTY_FORMAT.matcher(terminationString);
        Set<String> set = new HashSet<>();
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            Set<String> groupKeys = new HashSet<>();

            for (int i = 0; i < groupCount; i++) {
                groupKeys.add(matcher.group(i+1));
            }

            for (String groupKey : groupKeys) {
                set.add(groupKey);
                terminationString = terminationString.replace("{" + groupKey + "}", "");
            }

            matcher = PROPERTY_FORMAT.matcher(terminationString);
        }
        return set;
    }

    public static String performStringSubstitution(String input, Map<String, Object> inputParameters, Set<String> replacerKeys) {
        /*System.out.printf("\ninputParameters %s\n", inputParameters);*/
        StringBuilder resolvedInput = new StringBuilder(input);
        for(String key : replacerKeys) {
            if (inputParameters.containsKey(key)) {
                Object replacementValue = inputParameters.get(key);
                if (Objects.isNull(replacementValue)) {
                    throw new IllegalArgumentException("There is no replacement value specified for key '" + key + "'");
                }
                /*System.out.printf("\n key: %s -> replacement value : %s\n",key, replacementValue);
                System.out.printf("\n key: %s -> replacement value : %s\n",key, replacementValue.toString());*/
                String actualStringRepresentation = "{" + key + "}";
                int index = resolvedInput.indexOf(actualStringRepresentation);
                while (index >= 0) {
                    if (replacementValue.toString().startsWith("{") || replacementValue.toString().startsWith("[")){
                        resolvedInput.replace(index-1, index+actualStringRepresentation.length()+1, replacementValue.toString());
                    }else {
                        resolvedInput.replace(index, index+actualStringRepresentation.length(), replacementValue.toString());
                    }

                    index = resolvedInput.indexOf(actualStringRepresentation);
                }
            } else{
                throw new IllegalArgumentException("There is no parameter value specified for key '" + key + "'");
            }
        }
        return resolvedInput.toString();
    }

    public static String performStringSubstitution(String input, Map<String, Object> inputParameters, Set<String> replacerKeys, RestRequestDefinition restRequestDefinition) {
        /*System.out.printf("\ninputParameters %s\n", inputParameters);*/
        StringBuilder resolvedInput = new StringBuilder(input);
        for(String key : replacerKeys) {
            if (inputParameters.containsKey(key)) {
                Object replacementValue = inputParameters.get(key);
                if (Objects.isNull(replacementValue)) {
                    throw new IllegalArgumentException("There is no replacement value specified for key '" + key + "'" +
                            restRequestDefinition.toString());
                }
                /*System.out.printf("\n key: %s -> replacement value : %s\n",key, replacementValue);
                System.out.printf("\n key: %s -> replacement value : %s\n",key, replacementValue.toString());*/
                String actualStringRepresentation = "{" + key + "}";
                int index = resolvedInput.indexOf(actualStringRepresentation);
                while (index >= 0) {
                    if (replacementValue.toString().startsWith("{") || replacementValue.toString().startsWith("[")){
                        resolvedInput.replace(index-1, index+actualStringRepresentation.length()+1, replacementValue.toString());
                    }else {
                        resolvedInput.replace(index, index+actualStringRepresentation.length(), replacementValue.toString());
                    }

                    index = resolvedInput.indexOf(actualStringRepresentation);
                }
            } else{
                throw new IllegalArgumentException("There is no parameter value specified for key '" + key + "'");
            }
        }
        return resolvedInput.toString();
    }

    /**
     *
     * @param searchResponseAndDeletePayloadeKeysMappingFilePath : Map search response fields delete payload based on keys
     * @param searchResponse
     * @param userContext
     * //left side one is delete payload key
     * //right side one search response key
     * @return
     */
    public static Map<String, Object> searchResponseToDeletePayloadsKeysMapping(String searchResponseAndDeletePayloadeKeysMappingFilePath, Response searchResponse, UserContext userContext){
        Map<String, Object> payloadKeys = PayloadBuilder.mapJsonToHasMap(searchResponseAndDeletePayloadeKeysMappingFilePath);
        List<Map<String, Object>> searchResults = searchResponse.getBody().jsonPath().getList("searchData");
        if (searchResults.size() == 1) {
            Map<String, Object> searchData = (Map<String, Object>) searchResponse.getBody().jsonPath().getList("searchData").get(0);
            for(Map.Entry<String, Object> entry : payloadKeys.entrySet()){
                if (searchData.containsKey(entry.getKey())) {
                    System.out.println(String.format("Search for %s :: got %s", entry.getKey(), searchData.get(entry.getKey())));
                    if (searchData.get(entry.getKey()).toString().startsWith("[")) {
                        JSONArray jsonArr = new JSONArray(searchData.get(entry.getKey()).toString());
                        payloadKeys.put(entry.getKey(), jsonArr);
                    } else {
                        payloadKeys.put(entry.getKey(), searchData.get(entry.getKey()).toString());
                    }

                }else {
                    System.out.println("During mapping of Search and delete keys mapping " + entry.getKey() + " :: No key found");
                    if (entry.getKey().contentEquals("clientId")){
                        payloadKeys.put("clientId", SMSConfigService.getClientId(userContext));
                    }
                }
            }
            return payloadKeys;
        } else if (searchResults.size() > 1) {
            throw new RuntimeException("Multiple results found");
        }
        throw new RuntimeException("No unique results found");
    }

    public static Map<String, Object> searchResponseToDeletePayloadsKeysMappingMulti(String searchResponseAndDeletePayloadeKeysMappingFilePath, Response searchResponse, UserContext userContext, int index){
        Map<String, Object> payloadKeys = PayloadBuilder.mapJsonToHasMap(searchResponseAndDeletePayloadeKeysMappingFilePath);
            Map<String, Object> searchData = (Map<String, Object>) searchResponse.getBody().jsonPath().getList("searchData").get(index);
            for(Map.Entry<String, Object> entry : payloadKeys.entrySet()){
                if (searchData.containsKey(entry.getKey())) {
                    payloadKeys.put(entry.getKey(), searchData.get(entry.getKey()).toString());
                }else {
                    if (entry.getKey().contentEquals("clientId")){
                        payloadKeys.put("clientId", SMSConfigService.getClientId(userContext));
                    } else {
                        throw new RuntimeException("During mapping of Search and delete keys mapping " + entry.getKey() + " :: No key found");
                    }
                }
            }
            return payloadKeys;
    }
}
