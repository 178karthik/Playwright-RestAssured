package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.common.ConfigHelper;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AccessService {

    private static final Logger logger = LogManager.getLogger(AccessService.class);

    @Step("Forgot Password API Response")
    public Response forgotPassword(UserContext userContext, String clientKey) {
        RestRequestDefinition forgotPasswordDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/access/forgotPassword.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        Secret secret = ConfigHelper.getSecret(clientKey);
        replaceKeys.put("email", secret.getEmail());
        PayloadBuilder.getResolvedDefinition(forgotPasswordDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, forgotPasswordDefinition);
        return response;
    }

    @Step("Logout API Response")
    public Response logout(UserContext userContext) {
        RestRequestDefinition logoutDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/access/logout.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        PayloadBuilder.getResolvedDefinition(logoutDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, logoutDefinition);
        return response;
    }
}
