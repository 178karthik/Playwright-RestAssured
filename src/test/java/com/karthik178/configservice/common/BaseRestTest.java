package com.karthik178.configservice.common;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.apimanager.utils.FlattenUtils;
import com.karthik178.configservice.sms.SMSConfigService;
import com.karthik178.configservice.sms.SessionManager;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents BaseRestTest
 * @author Karthik T
 */
public abstract class BaseRestTest {

    protected SMSConfigService smsConfigService;
    protected UserContext userContext;
    protected String env;
    protected ObjectMapper objectMapper;
    protected String userName;
    private Secret secret;
    Random random ;
    private String key;

    protected String webhookPath;

    public BaseRestTest() {
    }
    public BaseRestTest(String key) {
        this.key = key;
    }

    @BeforeClass(alwaysRun = true)
    public void setup() {
//        smsConfigService = SessionManager.getSession(key);
//        userContext = smsConfigService.getUserContext();
        env = System.getProperty("env");
        secret = ConfigHelper.getSecret(key);
        userName = secret.getEmail();
        env = System.getenv("env");
        webhookPath = "sms/integrations/webhooks/"+env;
        random = new Random();
        objectMapper = new ObjectMapper();
    }


    public String getRandomStringId() {
        String randomStringId= String.format("%d-%d",this.getRandomNumberUsingNextInt(10001, 99999), this.getRandomNumberUsingNextInt(10001, 99999));
        return randomStringId;
    }
    public String getRandomWord() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return generatedString;
    }

    public String getRandomSentence() {

        return  String.format("%s %s %s", getRandomWord(), getRandomWord(), getRandomWord());
    }



    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
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
//        for(String responseField : actualResponseSet){
//            if (!baselineSet.contains(responseField)){
//                if (!responseField.startsWith("headers")){
//                    testfailed = true;
//                }
//                AllureLogger.error(String.format("!!Extra field : %s in outbound webhook", responseField));
//            }
//        }
        return testfailed;
    }
    public void validateOutboundWebookFields(String baselineWebhookPayloadPath, Response actucalIntegrationLogsResponse) throws IOException {
        Allure.link("Refer this baseline webhook payload : baseline-payload.json" , "https://bitbucket.org/prismforce/sms_qa_automation/src/master/src/test/resources/" + baselineWebhookPayloadPath);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedWebhookFields = FlattenUtils.flatten(FlattenUtils.jsonFileToMap(baselineWebhookPayloadPath));
        AllureLogger.info("Baseline Webhook Payload : \n" + PayloadBuilder.readClassPathResource(baselineWebhookPayloadPath));
        AllureLogger.info("Actual Webhook Payload: \n" +actucalIntegrationLogsResponse.jsonPath().prettify());
        Map<String, Object> responseLog =  actucalIntegrationLogsResponse.jsonPath().get("logs[0].request");
        String jsonFromResponse = objectMapper.writeValueAsString(responseLog);
        System.out.println("String to map : " + FlattenUtils.stringToMap(jsonFromResponse));
        Map<String, Object> feildsFromReponse = FlattenUtils.flatten(FlattenUtils.stringToMap(jsonFromResponse));
        AllureLogger.info("Baseline Payload fields" + expectedWebhookFields.keySet());
        AllureLogger.info("Actual Webhook fields" + feildsFromReponse.keySet());
        boolean testFailed = compareTwoSets(expectedWebhookFields.keySet(), feildsFromReponse.keySet());
        Assert.assertFalse(testFailed, "Decrepency in payload fields");
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

    public void validateOutboundWebookFields(String baselineWebhookPayloadPath, JsonNode actucalIntegrationLogsResponse, String logType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedWebhookFields = FlattenUtils.flatten(FlattenUtils.jsonFileToMap(baselineWebhookPayloadPath));
        AllureLogger.info("Baseline Webhook Payload: \n" + PayloadBuilder.readClassPathResource(baselineWebhookPayloadPath));
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

    public String getCurrentDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
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
