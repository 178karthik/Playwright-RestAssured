package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MyTeamService {

    private static final Logger logger = LogManager.getLogger(MyTeamService.class);

    @Step("Get Team Details Filters API Response")
    public Response getTeamDetailsFilters(UserContext userContext) {
        RestRequestDefinition getTeamDetailsFiltersDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/myteam/getTeamDetailsFilters.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        PayloadBuilder.getResolvedDefinition(getTeamDetailsFiltersDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, getTeamDetailsFiltersDefinition);
        return response;
    }

    @Step("Get Team Details API Response")
    public Response getTeamDetails(UserContext userContext, String fileName) {
        RestRequestDefinition getTeamDetailsDefinition = PayloadBuilder.mapJsonToRestDefinition(fileName);
        Map<String, Object> replaceKeys = new HashMap<>();
        PayloadBuilder.getResolvedDefinition(getTeamDetailsDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, getTeamDetailsDefinition);
        return response;
    }
    
}
