package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DashboardService {

    private static final Logger logger = LogManager.getLogger(AccessService.class);

    @Step("getDashboards API Response")
    public Response getDashboards(UserContext userContext, String fileName) {
        RestRequestDefinition getDashboardsDefinition = PayloadBuilder.mapJsonToRestDefinition(fileName);
        Map<String, Object> replaceKeys = new HashMap<>();
        PayloadBuilder.getResolvedDefinition(getDashboardsDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, getDashboardsDefinition);
        return response;
    }
}
