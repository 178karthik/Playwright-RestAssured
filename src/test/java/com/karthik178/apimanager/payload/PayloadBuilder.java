package com.karthik178.apimanager.payload;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.JsonUtils;
import io.qameta.allure.Allure;

import java.io.*;
import java.util.*;

/**
 * Represents PayloadBuilder
 * readClassPathResource -> file to string converter
 * mapJsonToRestDefinition -> resolved definition with map
 * @author Karthik T
 */
public class PayloadBuilder {

    public static String getFileName(String fileTemplate) {
        ClassLoader cl = PayloadBuilder.class.getClassLoader();
        File localFile = null;
        try {
            localFile = new File(cl.getResource(fileTemplate).getFile());
        }catch(Exception ex){
            System.out.printf("%s file doesn't exist", fileTemplate);
        } finally {
            if (localFile == null || !localFile.exists()) {
                localFile = new File(cl.getResource("./" + fileTemplate).getFile());
            }
        }
        return localFile.getName();
    }

    public static String getRequestURI(String fileTemplate) {
        RestRequestDefinition saveClientDef = mapJsonToRestDefinition("sms/authentication/client-autologin.post.json");
        return saveClientDef.getUriPath();

    }


    public static String readClassPathResource(String fileTemplate) {
        ClassLoader cl = PayloadBuilder.class.getClassLoader();
        File localFile = null;
        try {
            localFile = new File(cl.getResource(fileTemplate).getFile());
        }catch(Exception ex){
            System.out.printf("%s file doesn't exist", fileTemplate);
        } finally {
            if (localFile == null || !localFile.exists()) {
                localFile = new File(cl.getResource("./" + fileTemplate).getFile());
            }
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile))) ){
            String strLine;
            while ((strLine = br.readLine()) != null) {
                builder.append(strLine);
                builder.append("\n");
            }
            return builder.toString();
        } catch (IOException ioex) {
            throw new RuntimeException("Failed to read file " + fileTemplate + " from classpath", ioex);
        }
    }

    public static RestRequestDefinition mapJsonToRestDefinition(String fileName) {
        String payload = PayloadBuilder.readClassPathResource(fileName);
        RestRequestDefinition restRequestDefinition = PayloadBuilder.mapStringPayloadToDefinition(payload);
        Allure.link("Payload :: " + restRequestDefinition.getUriPath() , "https://bitbucket.org/prismforce/sms_qa_automation/src/master/src/test/resources/" + fileName);
        return restRequestDefinition;
    }

    public static Map<String, Object> mapJsonToHasMap(String fileName) {
        String payload = PayloadBuilder.readClassPathResource(fileName);
        Map<String, Object> map = PayloadBuilder.mapStringPayHashMapObject(payload);
        return map;
    }

    public static RestRequestDefinition mapStringPayloadToDefinition(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestRequestDefinition restRequestDefinition = null;
        try {
            restRequestDefinition = objectMapper.readValue(payload, new TypeReference<RestRequestDefinition>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return restRequestDefinition;
    }

    public static Map<String, String> mapStringPayHashMap(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> map = null;
        try {
            map = objectMapper.readValue(payload, new TypeReference<Map<String,String>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, Object> mapStringPayHashMapObject(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> map = null;
        try {
            map = objectMapper.readValue(payload, new TypeReference<Map<String,Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static void getResolvedDefinition(RestRequestDefinition restRequestDefinition, Map<String, Object> replaceValues) {

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        String uriResolvedString="", bodyResolvedString="";
        try {
            uriResolvedString = getResolvedString(restRequestDefinition.getUriPath(), replaceValues, restRequestDefinition);
            restRequestDefinition.setUriPath(uriResolvedString);
            bodyResolvedString = getResolvedString(restRequestDefinition.getBody().toString(), replaceValues, restRequestDefinition);
            restRequestDefinition.setBody(objectMapper.readTree(bodyResolvedString));

            Map<String, String> headers =  restRequestDefinition.getHeaders();
            if (Objects.nonNull(headers)) {
                for (Map.Entry<String, String> header: headers.entrySet()) {
                    Set<String> setOfReplacableKeys =  JsonUtils.iterateOverGroupKeys(header.getValue());
                    if (setOfReplacableKeys.size() > 0) {
                        String resolvedHeaderValue = JsonUtils.performStringSubstitution(header.getValue(), replaceValues, setOfReplacableKeys);
                        headers.put(header.getKey(), resolvedHeaderValue);
                    }
                }
            }
            if (Objects.nonNull(restRequestDefinition.getCookie())){
                Map<String, String> cookieparams =  restRequestDefinition.getCookie();
                for(Map.Entry<String, String> reqParameterValue : cookieparams.entrySet()) {
                    Set<String> set =  JsonUtils.iterateOverGroupKeys(reqParameterValue.getValue());
                    if (set.size() > 0) {
                        String resolvedInput = JsonUtils.performStringSubstitution(reqParameterValue.getValue(), replaceValues, set);
                        cookieparams.put(reqParameterValue.getKey(), resolvedInput);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("\nError occured while resolving payload : Check replacers" +
                    "\nuri : %s " +
                    "\nbody : %s " +
                    "\nErr : %s", uriResolvedString, bodyResolvedString, e));
        }

    }

    public Map<String, String> resolveReplacersInMap(Map<String, String> input, Map<String, Object> replaceValues) {
        if (Objects.nonNull(input)) {
            for (Map.Entry<String, String> header: input.entrySet()) {
                Set<String> setOfReplacableKeys =  JsonUtils.iterateOverGroupKeys(header.getValue());
                if (setOfReplacableKeys.size() > 0) {
                    String resolvedHeaderValue = JsonUtils.performStringSubstitution(header.getValue(), replaceValues, setOfReplacableKeys);
                    input.put(header.getKey(), resolvedHeaderValue);
                }
            }
        }
        return input;
    }

    public static String getResolvedString(String str, Map<String, Object> replaceValues)  {
        Set<String> set =  JsonUtils.iterateOverGroupKeys(str);
        String resolvedInput = JsonUtils.performStringSubstitution(str, replaceValues, set);
        return resolvedInput;

    }

    public static String getResolvedString(String str, Map<String, Object> replaceValues, RestRequestDefinition restRequestDefinition)  {
        Set<String> set =  JsonUtils.iterateOverGroupKeys(str);
        String resolvedInput = JsonUtils.performStringSubstitution(str, replaceValues, set, restRequestDefinition);
        return resolvedInput;

    }

    public static String mapToJsonString(Map<String, String> map)  {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String objectToJsonString(Object object)  {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
