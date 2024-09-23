package com.karthik178.configservice.sms;

import com.karthik178.apimanager.enums.IntegrationFields;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.ITestContext;

import java.util.*;

import static com.karthik178.configservice.sms.SMSConfigService.getUserId;

public class UserSkillService{


    public Response getUserSkillsResponse(UserContext userContext) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/get-user-skills.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        return response;
    }

    @Step("Get Existing User Skills")
    public List<JsonNode> getUserSkillItems(UserContext userContext){
        Response response = getUserSkillsResponse(userContext);
        List<JsonNode> userSkillItemsList =  response.getBody().jsonPath().getList("userSkillItems");
        return userSkillItemsList;
    }


    @Step("Delete user skill item")
    public Response deleteUserSkillItem(UserContext userContext, List<JsonNode> remainingUserSkillItemsList, List<JsonNode> skillItemToDelete){
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/delete-user-skill.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        replaceKeys.put("userId", getUserId(userContext));
        replaceKeys.put("skillItems", objectMapper.valueToTree(remainingUserSkillItemsList));
        replaceKeys.put("deletedSkillItems", objectMapper.valueToTree(skillItemToDelete));
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        return response;
    }

    @Step("Add User Skill Item")
    public Response addUserSkillItem(UserContext userContext, List<JsonNode> userSkillItemsListToAdd) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/add-user-skill.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        replaceKeys.put("userId", getUserId(userContext));
        replaceKeys.put("skillItems", objectMapper.valueToTree(userSkillItemsListToAdd));
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        return response;
    }

    public Response getMySkillResponse(UserContext userContext) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/get-all-skills.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        return response;
    }

    public Response getMySkillsResponseV2(UserContext userContext) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/get-my-skill-v2.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        return response;
    }

    public List<String> getUserSkillItemIds(UserContext userContext) {
        Response response = getUserSkillsResponse(userContext);
        return response.getBody().jsonPath().getList("skillItemIdsTaggedToUser");
    }

    public Map<String, Object> getAllSkillItemIds(UserContext userContext) {
        Response response = getMySkillResponse(userContext);
        return response.getBody().jsonPath().getMap("skillItems");
    }

    @Step("Get UserSkill API keys configured :: {dataKey}")
    public String UserSkillRequiredFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.userSkillsFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.userSkillsFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.userSkillsFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get Random Skill ID which is not assigned to User")
    public String getRandomSkillIDNotAssociateToUser(UserContext userContext) {
        SMSConfigService smsConfigService = new SMSConfigService();
        Response allSkillsResponse = LeafSkillsService.searchAndReturnSkillResponse(userContext, "");
        List<String> allSkillIds = allSkillsResponse.body().jsonPath().getList("searchData.hierarchyElementId");
        List<JsonNode> allSkillsAssignedToUserList =  smsConfigService.getAllMySkillsFromMySkillsSearchBar(userContext);
        Set<JsonNode> allSkillsAssignedToUserSet = new HashSet<>(allSkillsAssignedToUserList);
        for(String eachSkillId : allSkillIds) {
            if (allSkillsAssignedToUserSet.contains(eachSkillId) == false) {
                return  eachSkillId;
            }
        }
        throw new RuntimeException("No Unique Skill found");
    }

    public Response assignUserSkill(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> userSkillFields) {
        RestRequestDefinition assignUserSkillDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/userskills/assign-user-skills-mandatory-fields-only.post.json");
        String apiKey = smsConfigService.getAPIKey();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        String userId = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("userIdKey", userSkillFields.getOrDefault("userIdKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "userId", IntegrationFields.key.name())));
        replaceKeys.put("skillIdKey", userSkillFields.getOrDefault("skillIdKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillId", IntegrationFields.key.name())));
        replaceKeys.put("skillRatingTypeKey", userSkillFields.getOrDefault("skillRatingTypeKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillRatingType", IntegrationFields.key.name())));
        replaceKeys.put("skillRatingKey", userSkillFields.getOrDefault("skillRatingKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillRating", IntegrationFields.key.name())));
        replaceKeys.put("skillSourceKey", userSkillFields.getOrDefault("skillSourceKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillSource", IntegrationFields.key.name())));
        replaceKeys.put("lastUsedKey", userSkillFields.getOrDefault("lastUsedKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "LastUsed", IntegrationFields.key.name())));
        replaceKeys.put("experienceKey", userSkillFields.getOrDefault("experienceKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "Experience", IntegrationFields.key.name())));

        replaceKeys.put("userId", userSkillFields.getOrDefault("userId", userId));
        replaceKeys.put("skillId", userSkillFields.getOrDefault("skillId", getRandomSkillIDNotAssociateToUser(userContext)));
        replaceKeys.put("skillRatingType", userSkillFields.getOrDefault("skillRatingType", "Incoming"));
        replaceKeys.put("skillRating", userSkillFields.getOrDefault("skillRating", "2"));
        replaceKeys.put("skillSource", userSkillFields.getOrDefault("skillSource", "User Skill"));
        replaceKeys.put("lastUsed", userSkillFields.getOrDefault("lastUsed", "2020"));
        replaceKeys.put("experience", userSkillFields.getOrDefault("experience", "4"));
        PayloadBuilder.getResolvedDefinition(assignUserSkillDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, assignUserSkillDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response assignUserSkillOptionalFields(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> userSkillFields) {
        RestRequestDefinition assignUserSkillDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/userskills/assign-user-skills-optional-fields.post.json");
        String apiKey = smsConfigService.getAPIKey();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        String userId = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("userIdKey", userSkillFields.getOrDefault("userIdKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "userId", IntegrationFields.key.name())));
        replaceKeys.put("skillIdKey", userSkillFields.getOrDefault("skillIdKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillId", IntegrationFields.key.name())));
        replaceKeys.put("skillRatingTypeKey", userSkillFields.getOrDefault("skillRatingTypeKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillRatingType", IntegrationFields.key.name())));
        replaceKeys.put("skillRatingKey", userSkillFields.getOrDefault("skillRatingKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillRating", IntegrationFields.key.name())));
        replaceKeys.put("skillSourceKey", userSkillFields.getOrDefault("skillSourceKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillSource", IntegrationFields.key.name())));
        replaceKeys.put("lastUsedKey", userSkillFields.getOrDefault("lastUsedKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "LastUsed", IntegrationFields.key.name())));
        replaceKeys.put("experienceKey", userSkillFields.getOrDefault("experienceKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "Experience", IntegrationFields.key.name())));

        replaceKeys.put("projectIdKey", userSkillFields.getOrDefault("projectIdKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectId", IntegrationFields.key.name())));
        replaceKeys.put("skillTypeKey", userSkillFields.getOrDefault("skillTypeKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillType", IntegrationFields.key.name())));
        replaceKeys.put("endorserKey", userSkillFields.getOrDefault("endorserKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "endorser", IntegrationFields.key.name())));
        replaceKeys.put("endorsementTypeKey", userSkillFields.getOrDefault("endorsementTypeKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "endorsementType", IntegrationFields.key.name())));
        replaceKeys.put("remarksRejectReasonKey", userSkillFields.getOrDefault("remarksRejectReasonKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "reason", IntegrationFields.key.name())));
        replaceKeys.put("enabledKey", userSkillFields.getOrDefault("enabledKey", UserSkillRequiredFieldValuesBasedOnDataKey(smsConfigService, userContext, "enabled", IntegrationFields.key.name())));

        replaceKeys.put("userId", userSkillFields.getOrDefault("userId", userId));
        replaceKeys.put("skillId", userSkillFields.getOrDefault("skillId", getRandomSkillIDNotAssociateToUser(userContext)));
        replaceKeys.put("skillRatingType", userSkillFields.getOrDefault("skillRatingType", "Incoming"));
        replaceKeys.put("skillRating", userSkillFields.getOrDefault("skillRating", "2"));
        replaceKeys.put("skillSource", userSkillFields.getOrDefault("skillSource", "User Skill"));
        replaceKeys.put("lastUsed", userSkillFields.getOrDefault("lastUsed", "2020"));
        replaceKeys.put("experience", userSkillFields.getOrDefault("experience", "4"));

        replaceKeys.put("projectId", userSkillFields.getOrDefault("projectId", ""));
        replaceKeys.put("skillType", userSkillFields.getOrDefault("skillType", ""));
        replaceKeys.put("endorser", userSkillFields.getOrDefault("endorser", userId));
        replaceKeys.put("endorsementType", userSkillFields.getOrDefault("endorsementType", "reject"));
        replaceKeys.put("remarksRejectReason", userSkillFields.getOrDefault("remarksRejectReason", "Skill acquired as Learning Outcome"));
        replaceKeys.put("enabled", userSkillFields.getOrDefault("enabled", "1"));
        PayloadBuilder.getResolvedDefinition(assignUserSkillDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, assignUserSkillDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response deleteUserSkill(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> userSkillFields) {
        userSkillFields.put("enabled", "0");
        return assignUserSkill(smsConfigService, userContext, iTestContext, userSkillFields);
    }

}
