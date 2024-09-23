package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.*;

public class SkillGroupService {

    private  List<Map<String, String>> locations;


    public Response getallLocationsResponse(UserContext userContext) {
        RestRequestDefinition getAllLocations = PayloadBuilder.mapJsonToRestDefinition("sms/location-cluster/get-all-location.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(getAllLocations, replaceKeys);
        Response response = APIExecutor.execute(userContext, getAllLocations);
        return response;
    }

    public List<Map<String, String>> getAllLocations(UserContext userContext) {
        if (Objects.isNull(locations)){
            Response response = getallLocationsResponse(userContext);
            return response.getBody().jsonPath().getList("searchData");
        }
        return  locations;
    }

    @Step
    public List<String> getTwoRandomLocations(UserContext userContext) {
        if (Objects.isNull(locations)){
            locations = getAllLocations(userContext);
        }
        List<String> randomLocations = new ArrayList<>();
        randomLocations.add(locations.get(Math.max(1,5)).get("name"));
        randomLocations.add(locations.get(Math.max(6,8)).get("name"));
        return randomLocations;
    }
}
