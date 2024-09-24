package com.karthik178.configservice.sms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.model.User;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.common.ConfigHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents SMSConfigService
 * @author Karthik T
 */
public class SMSConfigService {

    private String key;
    protected ObjectMapper objectMapper;

    protected UserContext userContext;

    private Map<String, String> authCookies;

    private Response smsSettingsResponse;

    private List<JsonNode> skillCategories;

    private List<JsonNode> customTags;
    private List<JsonNode> hrmsTags;


    private List<JsonNode> practices;
    private List<JsonNode> allServices;
    private List<JsonNode> virtusaSkillOrigins;
    private List<JsonNode> allGroupServiceLine;
    private List<JsonNode> allComptencyGroup;
    private List<JsonNode> allServiceLine;
    private List<JsonNode> allCourses;
    private List<JsonNode> allSkillsFromMySkillsSearchBar;


    private static final Logger logger = LogManager.getLogger(SMSConfigService.class);



    public SMSConfigService(String key) {
        String env;
        authCookies = new HashMap<>();
        if (!Objects.isNull(System.getProperty("env"))) {
            env = System.getProperty("env");
        } else if (!Objects.isNull(System.getenv("env"))){
            env = System.getenv("env");
        } else {
                throw new InvalidParameterException("\nERROR ::Environment info is not passed in command-line arguments. How to pass environment info ? ::  '-Denv=uat' in commandline or '-e env=uat' for docker run ");
        }
        System.setProperty("env", env);
        logger.info(env.toUpperCase() + " Environment is selected");
        this.key = key;
        objectMapper = new ObjectMapper();
        this.userContext = getContext(key, env);
    }

    public SMSConfigService(String url, String username, String password) {
        String env;
        if (!Objects.isNull(System.getProperty("env"))) {
            env = System.getProperty("env");
        } else if (!Objects.isNull(System.getenv("env"))){
            env = System.getenv("env");
        } else {
            throw new InvalidParameterException("\nERROR ::Environment info is not passed in command-line arguments. How to pass environment info ? ::  '-Denv=uat' in commandline or '-e env=uat' for docker run ");
        }
        System.setProperty("env", env);
        logger.info(env.toUpperCase() + " Environment is selected");
        this.key = key;
        objectMapper = new ObjectMapper();
        this.userContext = getContext(url, username, password);
    }


    public SMSConfigService() {
        objectMapper = new ObjectMapper();
    }




    @Step("Get Authenticaion of target client")
    public UserContext getContext(String key){
        Secret secret = ConfigHelper.getSecret(key);
        RestRequestDefinition authDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/authentication/client-auth.post.json");
        Map<String, Object> map = this.secretToMapConverter(secret);
        PayloadBuilder.getResolvedDefinition(authDefinition, map);
        Response response = APIExecutor.execute(secret.getUrl(), authDefinition);
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = null;;
        try {
            user =  objectMapper.readValue(response.getBody().jsonPath().prettify(), new TypeReference<User>() {});
        } catch (IOException e) {
            System.out.println("SSO is enabled, Please enable basic authentication or Invalid Credentials");
            AllureLogger.info(String.format("SSO is enabled, Please enable basic authentication or Invalid Credentials"));
            throw new RuntimeException(e);
        }
        return new UserContext(user, secret);
    }

    @Step("Get Authenticaion of target client")
    public UserContext getContext(String key, String env){
        Secret secret = ConfigHelper.getSecret(key, env);
        if (Objects.isNull(secret)) {
            throw new RuntimeException(String.format("Unable to find secret '%s' for '%s' Environment", key, env));
        };
        RestRequestDefinition authDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/authentication/client-auth.post.json");
        Map<String, Object> map = this.secretToMapConverter(secret);
        PayloadBuilder.getResolvedDefinition(authDefinition, map);
        Response response = APIExecutor.execute(secret.getUrl(), authDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, "Unable to get access token. Error " + response.getStatusCode());
        Assert.assertNotNull(response.getBody().jsonPath().getString("token"), "Login Failure : " + response.getBody().jsonPath().prettify());
        logger.info("Login Success :: " + response.getBody().jsonPath().getString("token"));
        authCookies.putAll(response.cookies());
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = null;;
        try {
            user =  objectMapper.readValue(response.getBody().jsonPath().prettify(), new TypeReference<User>() {});
        } catch (IOException e) {
            System.out.println("SSO is enabled, Please enable basic authentication or Invalid Credentials");
            AllureLogger.info(String.format("SSO is enabled, Please enable basic authentication or Invalid Credentials"));
            throw new RuntimeException(e);
        }
        return new UserContext(user, secret, authCookies);
    }

    @Step("Get Authenticaion of target client")
    public UserContext getContext(String url, String username, String password){
        RestRequestDefinition authDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/authentication/client-auth.post.json");
        Map<String, Object> map = this.secretToMapConverter(username, password);
        PayloadBuilder.getResolvedDefinition(authDefinition, map);
        Response response = APIExecutor.execute(url, authDefinition);
        if (Objects.isNull(response.getBody().jsonPath().getString("token"))) {
            return null;
        }
        Assert.assertNotNull(response.getBody().jsonPath().getString("token"), "Login Failure : " + response.getBody().jsonPath().prettify());
        logger.info("Login Success :: " + response.getBody().jsonPath().getString("token"));
        authCookies.putAll(response.cookies());
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = null;;
        try {
            user =  objectMapper.readValue(response.getBody().jsonPath().prettify(), new TypeReference<User>() {});
        } catch (IOException e) {
            System.out.println("SSO is enabled, Please enable basic authentication or Invalid Credentials");
            AllureLogger.info(String.format("SSO is enabled, Please enable basic authentication or Invalid Credentials"));
            throw new RuntimeException(e);
        }
        Secret secret = new Secret(url, username, password);
        return new UserContext(user, secret, authCookies);
    }

    public Response getAutoLoginResponse() {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/authentication/client-autologin.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("token", userContext.getUser().getToken());
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef);
        System.out.println(response.getBody().prettyPrint());
        Assert.assertEquals(response.getStatusCode(), 200);
        return response;
    }

    public static String getUserRoleId(UserContext userContext) {
        try {
            String userRoleId = userContext.getUser().getUser().get("role").get("roleId").textValue();
            logger.info("User Role Id : " + userRoleId);
            return userRoleId;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get user role id " +e);
        }

    }

    public static JsonPath getSMSSettings(UserContext userContext){
        RestRequestDefinition settingsDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/settings/get.sms.setting.post.json");
        Map<String, Object> replaceValues = new HashMap<>();
        replaceValues.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(settingsDefinition, replaceValues);
        Response response = APIExecutor.execute(userContext, settingsDefinition);
        return response.getBody().jsonPath();
    }

    public Response getSMSSettingsResponse(UserContext userContext){
        if (Objects.isNull(smsSettingsResponse)) {
            RestRequestDefinition settingsDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/settings/get.sms.setting.post.json");
            Map<String, Object> replaceValues = new HashMap<>();
            replaceValues.put("clientId", getClientId(userContext));
            PayloadBuilder.getResolvedDefinition(settingsDefinition, replaceValues);
            Response response = APIExecutor.execute(userContext, settingsDefinition);
            smsSettingsResponse = response;
        }
        return smsSettingsResponse;
    }

    public Optional<JsonNode> getRandomSkillCategory(UserContext userContext){
        return  this.getSkillCategories(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    public List<JsonNode> getSkillCategories(UserContext userContext) {
        if (Objects.isNull(skillCategories)){
            skillCategories = objectMapper.convertValue(
                    getSMSSettingsResponse(userContext).jsonPath().getList("client.smsSettings.skillCategories"),
                    new TypeReference<List<JsonNode>>(){});
        }
        return skillCategories;
    }

    public Optional<JsonNode> getRandomPractice(UserContext userContext){
        return  this.getPractices(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }

    public Optional<JsonNode> getRandomPracticeExceptTargetPractice(UserContext userContext, String targetPractice){
        return  this.getPractices(userContext).stream().filter(sg -> !sg.get("name").toString().contains(targetPractice)).findAny();
    }

    public List<JsonNode> getPractices(UserContext userContext) {
        if (Objects.isNull(practices)){
            practices = objectMapper.convertValue(
                    getSMSSettingsResponse(userContext).jsonPath().getList("client.smsSettings.practices"),
                    new TypeReference<List<JsonNode>>(){});
        }
        return practices;
    }

    public List<JsonNode> getDataKeyForCustomTags(UserContext userContext) {
        if (Objects.isNull(customTags)){
            customTags = objectMapper.convertValue(
                    getSMSSettingsResponse(userContext).jsonPath().getList("client.smsSettings.fieldsForCustomTags.dataKey"),
                    new TypeReference<List<JsonNode>>(){});
        }
        LogHandler.logInfo( "Enabled Custom Tags in Settings are: " + customTags);
        return customTags;
    }

    public List<JsonNode> getDataKeyForHrmsMasterTags(UserContext userContext) {
        if (Objects.isNull(hrmsTags)){
            hrmsTags = objectMapper.convertValue(
                    getSMSSettingsResponse(userContext).jsonPath().getList("client.smsSettings.hrmsFieldsChosenForMaster.dataKey"),
                    new TypeReference<List<JsonNode>>(){});
        }
        LogHandler.logInfo("Enabled HRMS fields chosen for HRMS Masters in Settings are: " + hrmsTags);
        return hrmsTags;
    }

    private Map<String, Object> secretToMapConverter(Secret secret){
        Map<String, Object> map = new HashMap<>();
        map.put("email", secret.getEmail());
        map.put("password", secret.getPassword());
        return map;
    }

    private Map<String, Object> secretToMapConverter(String email, String password){
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        return map;
    }

    public static String getClientId(UserContext userContext){
        User user = userContext.getUser();
        Assert.assertNotNull(user.getClient().get("clientId"), "Unable to get clientId in /prismapi/login response");
        return user.getClient().get("clientId").textValue();
    }

    public static String getUserId(UserContext userContext){
        User user = userContext.getUser();
        Assert.assertNotNull( user.getUser().get("userId"), "Unable to get userId in /prismapi/login response");
        return user.getUser().get("userId").textValue();
    }

    public static String getUserId(String clientKey) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        User user = smsConfigService.userContext.getUser();
        Assert.assertNotNull(user.getUser().get("userId"), "Unable to get userId in /prismapi/login response");
        return user.getUser().get("userId").textValue();
    }

    public static String getSkillHierachyId(UserContext userContext, String hierachyName){
        User user = userContext.getUser();
        JsonNode skillHeirachys = user.getClient().get("smsSettings").get("skillHierarchy");
        if (skillHeirachys.isArray())
        {
            for (final JsonNode objNode : skillHeirachys)
            {
                if (hierachyName.equalsIgnoreCase(objNode.path("name").textValue()))
                {
                    logger.info(objNode.path("hierarchyId").toString());

                    return objNode.path("hierarchyId").textValue();
                }
            }
        }
        throw new RuntimeException("Unable to get Skill Hierachy. Please Admin permission for user");
    };

    public  Map<String, String> getAllSMSFeaturePermission(UserContext userContext) {
        JsonPath jsonPath =  getSMSSettings(userContext);
        Map<String, String> permissions = jsonPath.getMap("features.sms");
        return permissions;
    }

    public  static Map<String, String> getAllSMSAdminPermission(UserContext userContext) {
        JsonPath jsonPath =  getSMSSettings(userContext);
        Map<String, String> permissions = jsonPath.getMap("client.features.admin");
        return permissions;
    }

    public static Map<String, String> getExistingPractices(UserContext userContext) {
        JsonPath jsonPath =  getSMSSettings(userContext);
        List<Map<String, String>> practices = jsonPath.getList("client.smsSettings.practices");
        logger.info("Practices " + practices);
        List<Map<String, String>> targetPractice = practices.stream().filter(practice -> practice.get("name").length() > 3).collect(Collectors.toList());
        return targetPractice.get(0);
    }

    public String getSMSFeaturePermission(UserContext userContext, String featureKey) {
        Map<String, String> permissions =  getAllSMSFeaturePermission(userContext);

        if (permissions.containsKey(featureKey)){
            return permissions.get(featureKey);
        }
        return null;

    }

    public  static String getSMSAdminPermission(UserContext userContext, String featureKey) {
        Map<String, String> permissions =  getAllSMSAdminPermission(userContext);
        if (permissions.containsKey(featureKey)){
            return permissions.get(featureKey);
        }
        return null;

    }

    @Step("Get Some Random Specilization")
    public Map<String, JsonPath> getRandomSpecilization() {
        List<Map<String, JsonPath>> list = getAllSpecializations();
        Random random = new Random();
        Assert.assertNotNull(list, "No enough specialization to get random specialization");
        return list.get(random.nextInt((list.size() - 1) - 0));

    }

    @Step("Get Some Random Specialization")
    public static Map<String, JsonPath> getRandomSpecilization(UserContext userContext) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestRequestDefinition getRandomSpecilizationDef = PayloadBuilder.mapJsonToRestDefinition("sms/myteam/get.allspecilizations.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        replaceKeys.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomSpecilizationDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomSpecilizationDef);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        Random random = new Random();
        return list.get(random.nextInt((list.size() - 1) - 0));
    }

    @Step("Get All Specializations")
    public List<Map<String, JsonPath>> getAllSpecializations() {
        Response response = getAllSpecializationsResponse();
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        return list;

    }
    @Step("Get All Projects")
    public static Response getAllProjectsResponse(UserContext userContext){
        RestRequestDefinition projectSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/project/get-all-projects.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId",  getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(projectSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, projectSearchDefinition);
        return response;
    }

    @Step("Get All Specializations")
    public Response getAllSpecializationsResponse() {
        RestRequestDefinition getRandomSpecilizationDef = PayloadBuilder.mapJsonToRestDefinition("sms/myteam/get.allspecilizations.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        replaceKeys.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomSpecilizationDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomSpecilizationDef);
        return response;
    }

    @Step("Get User Specilization")
    public List<String> getUserSpecilization() {
        RestRequestDefinition getRandomSpecilizationDef = PayloadBuilder.mapJsonToRestDefinition("sms/specializations/get-my-specialization.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomSpecilizationDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomSpecilizationDef);
        List<String> userSpecializationList = response.getBody().jsonPath().getList("userSpecializations.specializationId");
        return userSpecializationList;
    }

    public String getRandomSpecIDNotAssociateToUser() {
        Response allSpecsResponse = getAllSpecializationsResponse();
        List<String> allSpecIds = allSpecsResponse.body().jsonPath().getList("searchData.specializationId");
        List<String> allSpecsAssignedToUserList =  getUserSpecilization();
        Set<String> allSpecsAssignedToUserSet = new HashSet<>(allSpecsAssignedToUserList);
        for(String eachSpecId : allSpecIds) {
            if (allSpecsAssignedToUserSet.contains(eachSpecId) == false) {
                return  eachSpecId;
            }
        }
        throw new RuntimeException("No Unique specalization found");
    }

    @Step("Get User Assigned Project")
    public List<String> getUserAssignedProjects() {
        RestRequestDefinition getRandomProjectDef = PayloadBuilder.mapJsonToRestDefinition("sms/project/get-my-projects.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", getUserId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomProjectDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomProjectDef);
        List<String> userProjectList = response.getBody().jsonPath().getList("data.extProjectId");
        return userProjectList;
    }

    @Step("Get Random Project ID which is not assigned to User")
    public String getRandomProjectIDNotAssociateToUser(UserContext userContext) {
        Response allProjectsResponse = getAllProjectsResponse(userContext);
        List<String> allProjectIds = allProjectsResponse.body().jsonPath().getList("searchData.extProjectId");
        List<String> allProjectsAssignedToUserList =  getUserAssignedProjects();
        Set<String> allProjectsAssignedToUserSet = new HashSet<>(allProjectsAssignedToUserList);
        for(String eachProjectId : allProjectIds) {
            if (allProjectsAssignedToUserSet.contains(eachProjectId) == false) {
                return  eachProjectId;
            }
        }
        throw new RuntimeException("No Unique Project found");
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

    public  List<JsonNode> getServiceLine(UserContext userContext){

        RestRequestDefinition groupServiceLineDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillCluster/search-service-line.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("search", "d");
        PayloadBuilder.getResolvedDefinition(groupServiceLineDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, groupServiceLineDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to getService Lines");
        Assert.assertTrue(response.getBody().jsonPath().getList("data").size() > 0,  "Unable to skill Cluster");
        allServiceLine = objectMapper.convertValue(response.getBody().jsonPath().getList("data"),  new TypeReference<List<JsonNode>>(){});

        return this.allServiceLine;
    }
    public  List<JsonNode> getCompetencyGroup(UserContext userContext){

        RestRequestDefinition groupServiceLineDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/skillCluster/search-competencyGroup.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("search", "a");
        PayloadBuilder.getResolvedDefinition(groupServiceLineDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, groupServiceLineDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get competencyGroup");
        Assert.assertTrue(response.getBody().jsonPath().getList("data").size() > 0,  "Unable to get competencyGroup");
        allComptencyGroup = objectMapper.convertValue(response.getBody().jsonPath().getList("data"),  new TypeReference<List<JsonNode>>(){});

        return this.allComptencyGroup;
    }
    @Step("Get Random Service")
    public Optional<JsonNode> getRandomService(UserContext userContext){
        return  this.getAllServices(userContext).stream().filter(sg -> sg.get("name").toString().length() > 3).findAny();
    }
    public  List<JsonNode> getCourses(UserContext userContext){

        RestRequestDefinition groupServiceLineDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/courses/get.courses.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(groupServiceLineDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, groupServiceLineDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get Courses");
        Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to get Courses");
        allCourses = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});

        return this.allCourses;
    }
    public String getAPIKey() {
        RestRequestDefinition getRandomSpecilizationDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/get.integrations.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(getRandomSpecilizationDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomSpecilizationDef);
        String apiKey = response.getBody().jsonPath().getString("client.integrations.apiKey");
        return apiKey;
    }
    public  Map<String, JsonPath> getUserDetails(ITestContext iTestContext,String userEmailId) {
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/myteam/get.user.details.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("userEmailId", userEmailId);
        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        return list.get(0);
    }

    public  String getUserNameFromUserDetailsAPI(ITestContext iTestContext, String clientKey) {
        Secret secret = ConfigHelper.getSecret(clientKey);
        String userEmailId = secret.getEmail();
        Map<String, JsonPath> userDetails = getUserDetails(iTestContext, userEmailId);
        String reporteeUserName = String.valueOf(userDetails.get("name"));
        return reporteeUserName;
    }
    public  String getEmployeeIdFromUserDetailsAPI(ITestContext iTestContext, String clientKey) {
        Secret secret = ConfigHelper.getSecret(clientKey);
        return getEmployeeIdFromUserDetailsAPI(iTestContext, secret);
    }

    public  String getEmployeeIdFromUserDetailsAPI(ITestContext iTestContext, Secret secret) {
        String userEmailId = secret.getEmail();
        Map<String, JsonPath> userDetails = getUserDetails(iTestContext, userEmailId);
        String reporteeUserName = String.valueOf(userDetails.get("empId"));
        return reporteeUserName;
    }


    public  String getUserEmailIdFromSecretFile(ITestContext iTestContext, String clientKey) {
        Secret secret = ConfigHelper.getSecret(clientKey);
        String userEmailId = secret.getEmail();
        return userEmailId;
    }
    public void createCourseThroughInboundAPI(String courseId, String courseName, String status, String skillItemId, String apiKey) {
        RestRequestDefinition getRandomSpecilizationDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/inbound/createcourse.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("courseId",courseId);
        replaceKeys.put("courseName",courseName);
        replaceKeys.put("status",status);
        replaceKeys.put("skills",skillItemId);
        replaceKeys.put("x-prismforce-key",apiKey);
        PayloadBuilder.getResolvedDefinition(getRandomSpecilizationDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, getRandomSpecilizationDef);
        System.out.println(response.body().prettyPrint());
    }
    public  List<JsonNode> getAllMySkillsFromMySkillsSearchBar(UserContext userContext){

        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/myskill-and-compentencies/get-all-my-skills-search-results.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("search", "a");
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Unable to get skill Clusters");
        Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Unable to skill Cluster");
        allSkillsFromMySkillsSearchBar = objectMapper.convertValue(response.getBody().jsonPath().getList("searchData"),  new TypeReference<List<JsonNode>>(){});
        return this.allSkillsFromMySkillsSearchBar;
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
    public  List<Map<String, Object>> getSkillGroupsWithSearch(UserContext userContext, String skillType,String skillGroupName){
        RestRequestDefinition skillGroupSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/uitests/citiustech/search-skill-group.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("user.clientId", getClientId(userContext));
        replaceKeys.put("type.smsSettings.skillHierarchy.hierarchyId", getSkillHierachyId(userContext,skillType));
        replaceKeys.put("search",skillGroupName);
        PayloadBuilder.getResolvedDefinition(skillGroupSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, skillGroupSearchDefinition);
        Assert.assertEquals(response.getStatusCode(), 200, ":: Status " + response.getStatusCode() + " : Unable to get " + skillType);
        Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0,  "Searchdata list is zero - Unable to get " + skillType);
        List<Map<String, Object>> list = response.getBody().jsonPath().getList("searchData");
        return list;
    }

    public List<Map<String, Object>> getSkillGroupsWithFilter(UserContext userContext, String skillType, String skillGroupName) {
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/uitests/citiustech/get-skill-group-with-filters.json");
        /* Get Category ID */
        List<JsonNode> skillCategories = getSkillCategories(userContext);
        String categoryId = skillCategories.get(0).get("categoryId").textValue();

        /* Get Skill Group hiearchyElementId */
        List<Map<String, Object>> allSkillGroupSearch = getSkillGroupsWithSearch(userContext, skillType, skillGroupName);
        int i = 0;
        for (Map<String, Object> skillGroup : allSkillGroupSearch) {
            String foundSkillGroupName = (String) skillGroup.get("name");
            if (skillGroupName.equalsIgnoreCase(foundSkillGroupName)) {
                break;
            }
            i++;
        }
        String skillGroupHiearchyElementId = (String) allSkillGroupSearch.get(i).get("hierarchyElementId");
        /* Replace The Json Keys and Values and trigger API Call */
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("skillCategoryId", categoryId);
        replaceKeys.put("skillGroupHiearchyElementId", skillGroupHiearchyElementId);
        replaceKeys.put("skillHieararchyElement", getSkillHierachyId(userContext, skillType));
        replaceKeys.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        List<Map<String, Object>> list = response.getBody().jsonPath().getList("searchData");
        return list;
    }
    public  List<Map<String, JsonPath>> getDesignationLevels(ITestContext iTestContext) {
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/designtaionLevels/get-designationLevels.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        return list;
    }
    public  List<Map<String, JsonPath>> getTechnoFunctionalSkills(ITestContext iTestContext) {
        /* Get TechnoFunctionalSkills Category ID */
        List<JsonNode> skillCategories = getSkillCategories(userContext);
        String categoryId = skillCategories.get(1).get("categoryId").textValue();
        System.out.println("Skill category Id is"+categoryId);
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/leafskills/skillmaster/get-technofunctional-skills.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("skillCategoryId", categoryId);
        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        return list;
    }


    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public Response getSmsSettings() {
        return smsSettingsResponse;
    }

    public void setSmsSettings(Response smsSettings) {
        this.smsSettingsResponse = smsSettings;
    }

    public void setSkillCategories(List<JsonNode> skillCategories) {
        this.skillCategories = skillCategories;
    }
}
