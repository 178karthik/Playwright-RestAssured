package com.karthik178.configservice.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.karthik178.apimanager.enums.IntegrationFields;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.playwritemanager.utils.JavaFakerUtils;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TagsService {

    @Step("Get Custom Tags API keys configured in integrations :: {dataKey}")
    public String customTagsFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.customTagMasterFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.customTagMasterFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.customTagMasterFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get HRMS Master Tags API keys configured in integrations :: {dataKey}")
    public String hrmsTagsFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.hrmsTagMasterFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.hrmsTagMasterFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.hrmsTagMasterFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    public String getRandomCustomTag(UserContext userContext){
        SMSConfigService smsConfigService=new SMSConfigService();
        Random random = new Random();
        List<JsonNode> customTags=smsConfigService.getDataKeyForCustomTags(userContext);
        String customTag=customTags.get(random.nextInt(customTags.size())).asText();
        return customTag;
    }

    public String getRandomHrmsMasterTag(UserContext userContext){
        SMSConfigService smsConfigService=new SMSConfigService();
        Random random = new Random();
        List<JsonNode> hrmsTags=smsConfigService.getDataKeyForHrmsMasterTags(userContext);
        String hrmsTag=hrmsTags.get(random.nextInt(hrmsTags.size())).asText();
        return hrmsTag;
    }

    public Response createCustomTag(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> customTagMasterFields) {
        RestRequestDefinition createCustomTagDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/customTags/create-custom-tags.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomID = JavaFakerUtils.getRandomStringId();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("attributeTypeKey", customTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "attributeType", IntegrationFields.key.name()));
        replaceKeys.put("nameValueKey", customTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "tagValue", IntegrationFields.key.name()));
        replaceKeys.put("externalIdKey", customTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "externalId", IntegrationFields.key.name()));
        replaceKeys.put("statusKey", customTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));

        replaceKeys.put("attributeType", customTagMasterFields.getOrDefault("attributeType", getRandomCustomTag(userContext)));
        replaceKeys.put("nameValue", customTagMasterFields.getOrDefault("nameValue", "Inbound-Test-" + randomID));
        replaceKeys.put("externalId", customTagMasterFields.getOrDefault("externalId", randomID));
        replaceKeys.put("status", customTagMasterFields.getOrDefault("status", "Active"));

        PayloadBuilder.getResolvedDefinition(createCustomTagDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createCustomTagDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response createHrmsMasterTag(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> hrmsTagMasterFields) {
        RestRequestDefinition createHrmsMasterTagDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/hrmsTags/create-hrms-tags-value.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomID = JavaFakerUtils.getRandomStringId();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("attributeTypeKey", hrmsTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "attributeType", IntegrationFields.key.name()));
        replaceKeys.put("nameValueKey", hrmsTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "tagValue", IntegrationFields.key.name()));
        replaceKeys.put("externalIdKey", hrmsTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "externalId", IntegrationFields.key.name()));
        replaceKeys.put("statusKey", hrmsTagsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));

        replaceKeys.put("attributeType", hrmsTagMasterFields.getOrDefault("attributeType", getRandomHrmsMasterTag(userContext)));
        replaceKeys.put("nameValue", hrmsTagMasterFields.getOrDefault("nameValue", "Inbound-Test-" + randomID));
        replaceKeys.put("externalId", hrmsTagMasterFields.getOrDefault("externalId", randomID));
        replaceKeys.put("status", hrmsTagMasterFields.getOrDefault("status", "Active"));

        PayloadBuilder.getResolvedDefinition(createHrmsMasterTagDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createHrmsMasterTagDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }
}