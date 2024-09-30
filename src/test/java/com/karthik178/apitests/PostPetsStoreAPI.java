package com.karthik178.apitests;

import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.common.BaseRestTest;
import com.karthik178.configservice.common.ConfigHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Features;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Epic("Pet Store")
@Features({@Feature("Login")})
@Story("Post Pets From Pet Store")
public class PostPetsStoreAPI extends BaseRestTest {

    private static String userKey = "pet.admin";
    public PostPetsStoreAPI() {
        super(userKey);

    }

   @Test(description = "Post Pets From Pet Store")
    public void postPetsFromPetsStoreAPI(ITestContext iTestContext)
   {
       Secret secret = ConfigHelper.getSecret(userKey);
       RestRequestDefinition getPetsDefinition = PayloadBuilder.mapJsonToRestDefinition("jsonPayloads/pet-store-post.json");
       Map<String, Object> replaceKeys = new HashMap<>();
       replaceKeys.put("categoryName","Dog");
       replaceKeys.put("url","https://karthiksdet.hashnode.dev/");
       replaceKeys.put("tagName","German Shepard");
       replaceKeys.put("name","Ruby");
       PayloadBuilder.getResolvedDefinition(getPetsDefinition,replaceKeys);
       Response response = APIExecutor.execute(secret.getUrl(), getPetsDefinition);
       Assert.assertTrue(response.statusCode()==200);
       System.out.println(response.getBody().prettyPrint());
       String dogName = response.getBody().jsonPath().getString("name");
       String tagName = response.getBody().jsonPath().getString("tags[0].name");
       /*Assertion */
       Assert.assertEquals(dogName,"Ruby");
       Assert.assertEquals(tagName,"German Shepard");



   }






}
