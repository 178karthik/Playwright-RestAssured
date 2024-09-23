package com.karthik178.configservice.sms;

import com.karthik178.apimanager.enums.IntegrationFields;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.configservice.common.BaseRestTest;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSpecializationService extends BaseRestTest{

    @Step("Add User Specialization Item")
    public String UserSpecializationFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(smsConfigService.userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.userSpecializationsFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.userSpecializationsFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.userSpecializationsFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    public Response assignUserSpecialization(SMSConfigService smsConfigService, ITestContext iTestContext, Map<String, Object> userSpecializationFields) {
        RestRequestDefinition assignUserSpecDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/userSpecialization/assign-specialization.post.json");
        String apiKey = smsConfigService.getAPIKey();
        Map<String, Object> replaceKeys = new HashMap<>();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        String empId = String.valueOf(userDetails.get("empId"));
        Map<String, JsonPath>  randomSpecialization = smsConfigService.getRandomSpecilization();
        String randomSpecializationId = String.valueOf(randomSpecialization.get("specializationId"));

        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("empIdKey", userSpecializationFields.getOrDefault("empIdKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "empId", IntegrationFields.key.name())));
        replaceKeys.put("specializationIdKey", userSpecializationFields.getOrDefault("specializationIdKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "specializationId", IntegrationFields.key.name())));
        replaceKeys.put("skillClusterIDKey", userSpecializationFields.getOrDefault("skillClusterIDKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "skillClusterId", IntegrationFields.key.name())));
        replaceKeys.put("skillClusterExternalIDKey", userSpecializationFields.getOrDefault("skillClusterExternalIDKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "skillClusterExtId", IntegrationFields.key.name())));
        replaceKeys.put("specializationCategoryKey", userSpecializationFields.getOrDefault("specializationCategoryKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "specializationCategory", IntegrationFields.key.name())));
        replaceKeys.put("dropKey", userSpecializationFields.getOrDefault("dropKey", UserSpecializationFieldValuesBasedOnDataKey(smsConfigService, "drop", IntegrationFields.key.name())));

        replaceKeys.put("empId", userSpecializationFields.getOrDefault("empId", empId));
        replaceKeys.put("specializationId", userSpecializationFields.getOrDefault("specializationId", randomSpecializationId));
        replaceKeys.put("skillClusterID", userSpecializationFields.getOrDefault("skillClusterID", ""));
        replaceKeys.put("skillClusterExternalID", userSpecializationFields.getOrDefault("skillClusterExternalID", ""));
        replaceKeys.put("specializationCategory", userSpecializationFields.getOrDefault("specializationCategory", "Primary"));
        replaceKeys.put("drop", userSpecializationFields.getOrDefault("drop", ""));

        PayloadBuilder.getResolvedDefinition(assignUserSpecDefinition, replaceKeys);
        Response response =APIExecutor.execute(smsConfigService.userContext, assignUserSpecDefinition, iTestContext);
        return response;
    }

}