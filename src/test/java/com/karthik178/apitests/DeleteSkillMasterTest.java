package com.karthik178.apitests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.enums.Literals;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.configservice.common.BaseRestTest;
import com.karthik178.configservice.sms.IntegrationService;
import com.karthik178.configservice.sms.LeafSkillsService;
import com.karthik178.configservice.sms.SMSConfigService;
import com.karthik178.exceptionhandler.NoIntegrationLogsFoundException;
import com.karthik178.playwritemanager.utils.JavaFakerUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Features;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Epic("Virtusa")
@Features({@Feature("Skill Master")})
@Story("[VA-16]:  Delete Skill Master and Verify Outbound Webhook")
public class DeleteSkillMasterTest extends BaseRestTest {

    private LeafSkillsService leafSkillsService;
    private Map<String, Object> entities;
    private Optional<JsonNode> virtusaSkillSource;

    private Map<String, Response> responseMap;
    public DeleteSkillMasterTest() {
        super("virtusa.admin");
        leafSkillsService = new LeafSkillsService();
        entities = new HashMap<>();
    }

    @Test(description = "1. Precondition : Check Target Client Exist in Target Environment",
            groups = {"VA-16", "skillMaster", "virtusa"})
    public void preconditionCheck(){
        Assert.assertNotNull(userContext, String.format("Target client %s :: does not exist in target Env %s ","virtusa.admin",  env));
        AllureLogger.info(String.format("Target client %s :: exist in target Env %s ","virtusa.admin",  env));
    }

    @Test(description = "2. Get Precondition Data : Search and retrieve Skill group and  Skill Source  ",
            dependsOnMethods = {"preconditionCheck"},
            groups = {"VA-16", "skillMaster", "virtusa"}
    )
    public void getPreconditionDataToCreateRandomData () {

        /**get random skill category**/
        Optional<JsonNode> skillCategory= smsConfigService.getRandomSkillCategory(userContext);
        Assert.assertTrue(skillCategory.isPresent(), "No Skill Category present");
        entities.put("randomSkillCategory", skillCategory.get());
        /***get random Virtusa Skil Source ***/
        virtusaSkillSource= leafSkillsService.getRandomVirtusaSkillSource(userContext);
        Assert.assertTrue(virtusaSkillSource.isPresent(), "No skill Source present");
        entities.put("randomVirtusaSkillSource", virtusaSkillSource.get());

    }

    @Test(description = "3. [Admin->Leafskill->Skill] Create Skill Master",
            dependsOnMethods = {"getPreconditionDataToCreateRandomData"},
            groups = {"VA-16", "skillMaster", "virtusa"}
    )
    public void createSkillmaster(ITestContext iTestContext) {
        RestRequestDefinition createSkillMasterDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/leafskills/skillmaster/skillMaster-virtusa-save.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("name", "SM-AutoTest-"+ JavaFakerUtils.getRandomStringId());
        replaceKeys.put("randomSentence", getRandomStringId());
        replaceKeys.put("user.clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("randomVirtusaSkillSource", entities.get("randomVirtusaSkillSource"));
        replaceKeys.put("randomSkillCategory", entities.get("randomSkillCategory"));
        replaceKeys.put("skillDefinition", getRandomStringId());
        replaceKeys.put("randomVirtusaSkillSource.hierarchyElementId", SMSConfigService.getSkillHierachyId(userContext,"Skill Source"));
        PayloadBuilder.getResolvedDefinition(createSkillMasterDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, createSkillMasterDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, "Create skill master is not successfull");
        iTestContext.setAttribute("skillName", replaceKeys.get("name"));
    }


    @Test(description = "5. Verify Outbound Webhook status",
            dependsOnMethods = {"deleteSkillMaster"},
            groups = {"VA-16", "skillMaster", "virtusa"})
    public void searchOutboundWebHook(ITestContext iTestContext) throws NoIntegrationLogsFoundException {
        String skillName = (String) iTestContext.getAttribute("skillName");
        Response response = IntegrationService.getIntegrationLogs(iTestContext, userContext, skillName);
        iTestContext.setAttribute("integrationlogsResponse", response);
    }

    @Test(description = "6. Verify Outbound Save Skills Payload fields",
            dependsOnMethods = {"searchOutboundWebHook"},
            groups = {"VA-16", "skillMaster", "virtusa"})
    public void validateSaveSkillsOutboundWebhookPayload(ITestContext context) throws IOException {
        Response actucalIntegrationLogsResponse = (Response) context.getAttribute("integrationlogsResponse");
        List<Map<String, JsonNode>> logs =  actucalIntegrationLogsResponse.getBody().jsonPath().getList("logs");
        JsonNode targetLog = null;
        for(Map<String, JsonNode> log : logs){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.valueToTree(log.get("request"));;
            if (jsonNode.get("json").get("event").toString().contains("skillMaster.deleted")) {
                targetLog = jsonNode;
                break;
            }
        }
        validateOutboundWebookFields("sms/integrations/webhooks/virtusa/skillmaster/outbound.skillmaster.deleted.json"
                ,targetLog);
    }





}
