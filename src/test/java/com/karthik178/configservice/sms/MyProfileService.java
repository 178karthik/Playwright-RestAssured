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

public class MyProfileService {

    private static final Logger logger = LogManager.getLogger(MyProfileService.class);

    @Step("Get all resumes built from a profile API Response")
    public Response getResumesBuiltFromProfile(UserContext userContext) {
        RestRequestDefinition getResumesBuiltFromProfileDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/myprofile/getResumesBuiltFromProfile.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        PayloadBuilder.getResolvedDefinition(getResumesBuiltFromProfileDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, getResumesBuiltFromProfileDefinition);
        return response;
    }
}
