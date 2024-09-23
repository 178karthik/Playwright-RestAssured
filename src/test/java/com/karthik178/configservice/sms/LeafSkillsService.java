package com.karthik178.configservice.sms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.karthik178.apimanager.enums.Literals;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.*;

/**
 * Represents LeafSkillsService
 * @author Karthik T
 */
public class LeafSkillsService extends SMSConfigService{


    private List<JsonNode> allSkillsGroups;
    private List<JsonNode> allDemographicSkillGroups;
    private List<JsonNode> allServices;
    private List<JsonNode> allSkillOrigins;
    private List<JsonNode> allSkillClusters;

    private List<JsonNode> allSpecializationPrograms;

    private List<JsonNode> allSpecialization;

    private List<JsonNode> allGroupServiceLine;

    private List<JsonNode> virtusaSkillOrigins;

    private List<JsonNode> virtusaSkillClusterMandatoryItems;
    private List<JsonNode> persistentSkillGroups;

    private List<JsonNode> allSkillSubGroups;

    private List<JsonNode> virtusaSkillSource;



    @Step("Get Random Skill Group")
    public Optional<JsonNode> getRandomSkillGroup(UserContext userContext){
        return  this.getSkillGroups(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random DemoGraphic Skill Group")
    public Optional<JsonNode> getRandomDemoGraphicSkillGroup(UserContext userContext){
        return  this.getDemoGraphicSkillGroups(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Skill")
    public Optional<JsonNode> getRandomSkillGroup(UserContext userContext, String skillType){
        return  this.getSkillGroups(userContext, skillType).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Service")
    public Optional<JsonNode> getRandomService(UserContext userContext){
        return  this.getAllServices(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Skill Origin")
    public Optional<JsonNode> getRandomSkillOrigin(UserContext userContext){
        return  this.getAllSkillOrigins(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Specialization Program")
    public Optional<JsonNode> getRandomSpecializationPrograms(UserContext userContext){
        return  this.getAllSpecializationPrograms(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Specialization")
    public Optional<JsonNode> getRandomSpecialization(UserContext userContext){
        return  this.getAllSpecialization(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Skill Item")
    public Optional<JsonNode> getRandomSkillItems(UserContext userContext){
        return  this.getAllSkillItems(userContext).stream().filter(sg -> sg.get("name").toString().length() > 1).findAny();
    }

    @Step("Get Random Skill Item")
    public Optional<JsonNode> getSkillItemsOtherThanTargetOne(UserContext userContext, String targetskillCluster){
        return  this.getAllSkillItems(userContext).stream().filter(sg -> !sg.get("name").toString().contains(targetskillCluster)).findAny();
    }
    @Step("Get Random Skill sub group")
    public Optional<JsonNode> getRandomPersistentskillsubGroup(UserContext userContext){
        return  this.getPersistentSkillSubGroups(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    @Step("Get Random Skill Sources")
    public Optional<JsonNode> getRandomVirtusaSkillSource(UserContext userContext){
        return  this.getVirtusaSkillSource(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }


    public static String getHierarchyElementId(UserContext userContext, Map<String, Object> keys){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value",keys.get("name").toString());
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext, Literals.SKILL_GROUP));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        List<Map<String, Object>> searchResults = response.jsonPath().getList("searchData");
        System.out.println(searchResults);
        return response.getBody().jsonPath().get("searchData[0].hierarchyElementId").toString();
    }

    public static String getHierarchyElementIdForSkillSource(UserContext userContext, Map<String, Object> keys){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value",  keys.get("name").toString());
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext, Literals.SKILL_SOURCE));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        List<Map<String, Object>> searchResults = response.jsonPath().getList("searchData");
        System.out.println(searchResults);
        return response.getBody().jsonPath().get("searchData[0].hierarchyElementId").toString();
    }

    @Step("Get Skill HierarchyElement Id")
    public static String getSkillHierarchyElementId(UserContext userContext, String searchValue, String type){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Assert.assertNotNull(getSkillHierachyId(userContext,type), "Unable to get hierachyid");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value", searchValue);
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,type));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        List<Map<String, Object>> searchResults = response.jsonPath().getList("searchData");
        System.out.println(searchResults);
        return response.getBody().jsonPath().get("searchData[0].hierarchyElementId").toString();
    }

    @Step("Search Skill")
    public static Response searchAndReturnSkillResponse(UserContext userContext, Map<String, Object> keys){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value", keys.get("name").toString());
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,Literals.SKILL_GROUP));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        System.out.println(response.jsonPath().prettify());
        return response;
    }
    public static Response searchAndReturnSkillResponseForSkillSource(UserContext userContext, Map<String, Object> keys){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value", keys.get("name").toString());
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,Literals.SKILL_SOURCE));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        System.out.println(response.jsonPath().prettify());
        return response;
    }

    @Step("Search Skill")
    public static Response searchAndReturnSkillResponse(UserContext userContext, String name, String type){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill-entity.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value", name);
        replaceKeys.put("user.clientId", getClientId(userContext));
        if(type.equalsIgnoreCase("skillOrigin"))
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", type);
        else
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,type));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        System.out.println(response.jsonPath().prettify());
        return response;
    }

    @Step("Search Skill")
    public static Response searchAndReturnSkillResponse(UserContext userContext, String searchAttribute){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/search/search-skill.get.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search.value", searchAttribute);
        replaceKeys.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        return response;
    }

    public  List<JsonNode> getSkillGroups(UserContext userContext){

        if (Objects.isNull(allSkillsGroups)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillgroup/get-skill-group.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,Literals.SKILL_GROUP));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill groups");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get skill groups");
            allSkillsGroups = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allSkillsGroups;
    }

    public  List<JsonNode> getSkillGroups(UserContext userContext, String skillType){
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillgroup/get-skill-group.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,skillType));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Status " + response.getStatusCode() + " : Unable to get " + skillType);
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Searchdata list is zero - Unable to get " + skillType);
            return objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
    }

    public  List<JsonNode> getDemoGraphicSkillGroups(UserContext userContext){
        if (Objects.isNull(allDemographicSkillGroups)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillgroup/get-skill-group.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,"Demographic Skill Group"));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Demographic skill groups");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get Demographic skill groups");
            allDemographicSkillGroups = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allDemographicSkillGroups;
    }

    public  List<JsonNode> getAllServices(UserContext userContext){
        if (Objects.isNull(allServices)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/service/get-services.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Services");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get services");
            allServices = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allServices;
    }

    public  List<JsonNode> getAllSkillOrigins(UserContext userContext){
        if (Objects.isNull(allSkillOrigins)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillOrigin/get-skill-origin.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill Origins");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to skill Origins");
            allSkillOrigins = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allSkillOrigins;
    }

    public  List<JsonNode> getAllSpecializationPrograms(UserContext userContext){
        if (Objects.isNull(allSpecializationPrograms)) {
            RestRequestDefinition getSpecializationDef = PayloadBuilder.mapJsonToRestDefinition("sms/specializations/get-speclization-program-post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("clientId", getClientId(userContext));
            replaceKeys.put("search", "");
            PayloadBuilder.getResolvedDefinition(getSpecializationDef, replaceKeys);
            Response response = APIExecutor.execute(userContext, getSpecializationDef);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get specializations");
            Assert.assertTrue(response.getBody().jsonPath().getList("allSpecializationPrograms").size() > 0,  "Unable to speclization program");
            allSpecializationPrograms = objectMapper.convertValue(response.getBody().jsonPath().getList("allSpecializationPrograms"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allSpecializationPrograms;
    }

    public  List<JsonNode> getAllSpecialization(UserContext userContext){
        if (Objects.isNull(allSpecialization)) {
            RestRequestDefinition getSpecializationDef = PayloadBuilder.mapJsonToRestDefinition("sms/specializations/get-speclization-program-post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("clientId", getClientId(userContext));
            replaceKeys.put("search", "");
            PayloadBuilder.getResolvedDefinition(getSpecializationDef, replaceKeys);
            Response response = APIExecutor.execute(userContext, getSpecializationDef);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get specializations");
            Assert.assertTrue(response.getBody().jsonPath().getList("allSpecializations").size() > 0,  "Unable to speclization");
            allSpecialization = objectMapper.convertValue(response.getBody().jsonPath().getList("allSpecializations"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allSpecialization;
    }

    public  List<JsonNode> getAllSkillItems(UserContext userContext){
        if (Objects.isNull(allSkillClusters)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillCluster/search-skill-item.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("search", "a");
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill Clusters");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to skill Cluster");
            allSkillOrigins = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.allSkillOrigins;
    }
    public  List<JsonNode> getGroupServiceLine(UserContext userContext){

        RestRequestDefinition groupServiceLineDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillCluster/search-group-service-line.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("search", "d");
        PayloadBuilder.getResolvedDefinition(groupServiceLineDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, groupServiceLineDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Group Service Lines");
        Assert.assertTrue(response.getBody().jsonPath().getList("data").size() > 0,  "Unable to skill Cluster");
        allGroupServiceLine = objectMapper.convertValue(response.getBody().jsonPath().getList("data"),  new TypeReference<List<JsonNode>>(){});

        return this.allGroupServiceLine;
    }

    public  List<JsonNode> getAllSkillItemsForVirtusa(UserContext userContext){

        if (Objects.isNull(virtusaSkillOrigins)) {

            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillCluster/search-virtusa-skill-item.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("search", "j");
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill Clusters");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0, "Unable to skill Cluster");
            virtusaSkillOrigins = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"), new TypeReference<List<JsonNode>>() {
            });
        }
        return this.virtusaSkillOrigins;
    }

    public  List<JsonNode> getSkillClusterMandatoryItemsForVirtusa(UserContext userContext){

        if (Objects.isNull(virtusaSkillClusterMandatoryItems)) {

            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/specializations/get-virtusa-cluster-mandatoty-items-post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill Clusters");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0, "Unable to skill Cluster");
            virtusaSkillClusterMandatoryItems = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"), new TypeReference<List<JsonNode>>() {
            });
        }
        return this.virtusaSkillClusterMandatoryItems;
    }

    @Step("Get Random extSkillClusterId - Virtusa")
    public Map<String, JsonPath> getRandomExtSkillClusterId(UserContext userContext) {
        RestRequestDefinition getRandomExtSkillClusterId = PayloadBuilder.mapJsonToRestDefinition("sms/specializations/get-virtusa-cluster-mandatoty-items-post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomExtSkillClusterId, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomExtSkillClusterId);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        Random random = new Random();
        Assert.assertNotNull(list, "No enough extSkillClusterIds to get random extSkillClusterId");
        return list.get(random.nextInt((list.size() - 1) - 0));
    }

    public  List<JsonNode> getPersistentSkillSubGroups(UserContext userContext){
        if (Objects.isNull(persistentSkillGroups)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillgroup/get-skill-group.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,"Skill Sub-Group"));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Demographic skill groups");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get Demographic skill groups");
            persistentSkillGroups = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.persistentSkillGroups;
    }
    public  List<JsonNode> getVirtusaSkillSource(UserContext userContext){
        if (Objects.isNull(virtusaSkillSource)) {
            RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillgroup/get-skill-group.post.json");
            Map<String, Object> replaceKeys = new HashMap<>();
            replaceKeys.put("user.clientId", getClientId(userContext));
            replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,"Skill Source"));
            PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
            Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
            Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Skill Sources");
            Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get Skill Sources");
            virtusaSkillSource = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        }
        return this.virtusaSkillSource;
    }

}
