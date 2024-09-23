package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.common.BasePlaywriteTest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AdminService extends BasePlaywriteTest {

    private static final Logger logger = LogManager.getLogger(AdminService.class);

    @Step("Read Skill Addition Request API Response")
    public Response readSkillAdditionRequest(UserContext userContext) {
        RestRequestDefinition readSkillAdditionRequestDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/admin/readSkillAdditionRequest.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(readSkillAdditionRequestDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, readSkillAdditionRequestDefinition);
        return response;
    }

    @Step("Admin Save Users API Response")
    public Response saveUsers(UserContext userContext) {
        RestRequestDefinition saveUsersDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/admin/saveUsers.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", SMSConfigService.getUserId(userContext));
        replaceKeys.put("roleId", SMSConfigService.getUserRoleId(userContext));
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(saveUsersDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveUsersDefinition);
        return response;
    }

    @Step("Admin Reports Dump As API Response")
    public Response getReports(UserContext userContext) {
        RestRequestDefinition getReportsDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/admin/getReports.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("reportStartDt", getYesterdayDateAndTime("yyyy-MM-dd"));
        replaceKeys.put("reportEndDt", getCurrentDateAndTime("yyyy-MM-dd"));
        PayloadBuilder.getResolvedDefinition(getReportsDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, getReportsDefinition);
        return response;
    }
}
