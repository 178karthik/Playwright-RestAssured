package com.karthik178.configservice.sms;

import com.github.javafaker.Faker;
import com.karthik178.apimanager.enums.IntegrationFields;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.configservice.common.ConfigHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.*;

import static com.karthik178.configservice.sms.SMSConfigService.getClientId;

public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Step("Get HRMS API keys configured in integrations :: {dataKey}")
    public String hrmsFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.hrmsFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.hrmsFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.hrmsFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get EmployeeVisa API keys configured in integrations :: {dataKey}")
    public String designationMasterFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.designationMasterFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.designationMasterFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.designationMasterFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get EmployeeVisa API keys configured in integrations")
    public String employeeVisaFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.visaFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations. visaFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.visaFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    public Object generateVisaObject() {
        Map<String, String> visas = new HashMap<>();
        visas.put("visaType", "Business Visa");
        visas.put("visaStatus", "Active");
        visas.put("visaCountry", "USA");
        visas.put("visaExpireDate", "03-12-2025");
        List<Map<String, String>> listOfVisas = new ArrayList<>();
        listOfVisas.add(visas);
        return PayloadBuilder.objectToJsonString(listOfVisas);
    }

    public Object generateLanguageObject() {
        Map<String, String> languages = new HashMap<>();
        languages.put("name", "English");
        languages.put("fluency", "Native/Bilingual");
        Map<String, String> languages1 = new HashMap<>();
        languages1.put("name", "Hindi");
        languages1.put("fluency", "Full Professional");
        List<Map<String, String>> listOfLanguages = new ArrayList<>();
        listOfLanguages.add(languages);
        listOfLanguages.add(languages1);
        return PayloadBuilder.objectToJsonString(listOfLanguages);
    }

    public Object generateEducationObject() {
        Map<String, String> educations = new HashMap<>();
        educations.put("EndDate", "2010-10-10T00:00:00.000Z");
        educations.put("Location", "Bilagi Dist");
        educations.put("StartDate", "2003-10-10T00:00:00.000Z");
        educations.put("Achievements", "Gold Medals from SKKPS");
        educations.put("Institution", "Sarkari Kannada kiriya Prathamika Shaale,karnataka-587101");
        educations.put("FieldOfStudy", "Kannada Medium");
        educations.put("PassingGrade", "A+");
        educations.put("Qualification", "Primary School");
        educations.put("Created_Date", "2024-02-15T00:00:00.000Z");
        List<Map<String, String>> listOfEducations = new ArrayList<>();
        listOfEducations.add(educations);
        return PayloadBuilder.objectToJsonString(listOfEducations);
    }

    public Object generatePreviousEmployersObject() {
        Map<String, String> previousEmployers = new HashMap<>();
        previousEmployers.put("PreviousEmployerName", "Ecolab Digital Center");
        previousEmployers.put("PreviousEmployerTitle", "Junior Qa Engineer-Update");
        previousEmployers.put("PreviousEmployerEndDate", "2020-11-10T00:00:00.000Z");
        previousEmployers.put("PreviousEmployerLocation", "Banglore");
        previousEmployers.put("PreviousEmployerStartDate", "2019-11-10T00:00:00.000Z");
        previousEmployers.put("PreviousEmployerJobDescription", "Create and promote the use of behaviour/test-driven development at multiple levels within the software by pairing with developers and product owners");
        previousEmployers.put("Create_Date", "2024-02-15T00:00:00.000Z");
        previousEmployers.put("PreviousEmployerEmpType", "Employee");
        List<Map<String, String>> listOfPreviousEmployers = new ArrayList<>();
        listOfPreviousEmployers.add(previousEmployers);
        return PayloadBuilder.objectToJsonString(listOfPreviousEmployers);
    }

    public Response createUser(String clientKey, ITestContext iTestContext, Map<String, Object> hrmsFields) {
        Secret secret = ConfigHelper.getSecret(clientKey);
        String userName = secret.getEmail();
        String[] domain = userName.split("@");
        String domainValue = domain[domain.length - 1];
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/hrms/common/create-user-mandatory-fields-only.post.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        UserContext userContext = smsConfigService.getUserContext();
        String primaryReportingId = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("employeeIdKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name()));
        replaceKeys.put("firstNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "firstName", IntegrationFields.key.name()));
        replaceKeys.put("lastNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "lastName", IntegrationFields.key.name()));
        replaceKeys.put("userEmailKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "email", IntegrationFields.key.name()));
        replaceKeys.put("joiningDateKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "doj", IntegrationFields.key.name()));
        replaceKeys.put("primaryReportingIdKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "reportingTo", IntegrationFields.key.name()));
        replaceKeys.put("employeeStatusKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("designationNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designation", IntegrationFields.key.name()));

        replaceKeys.put("employeeId", hrmsFields.getOrDefault("employeeId", "PF-" + randomID));
        replaceKeys.put("firstName", hrmsFields.getOrDefault("firstName", "Automation-Test"));
        replaceKeys.put("lastName", hrmsFields.getOrDefault("lastName", Faker.instance().name().lastName()));
        replaceKeys.put("userEmail", hrmsFields.getOrDefault("userEmail", "autopf" + randomID + "@" + domainValue));
        replaceKeys.put("joiningDate", hrmsFields.getOrDefault("joiningDate", "2024-01-01T00:00:00.000Z"));
        replaceKeys.put("employeeStatus", hrmsFields.getOrDefault("employeeStatus", "Active"));
        replaceKeys.put("primaryReportingId", hrmsFields.getOrDefault("primaryReportingId", primaryReportingId));
        replaceKeys.put("designationName", hrmsFields.getOrDefault("designationName", "QA Architect"));
        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createUserDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response createUser(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> hrmsFields) {
        String userName = userContext.getSecret().getEmail();
        String[] domain = userName.split("@");
        String domainValue = domain[domain.length - 1];
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/hrms/common/create-user-mandatory-fields-only.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid().toString();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, userName);
        String primaryReportingId = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("employeeIdKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name()));
        replaceKeys.put("firstNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "firstName", IntegrationFields.key.name()));
        replaceKeys.put("lastNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "lastName", IntegrationFields.key.name()));
        replaceKeys.put("userEmailKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "email", IntegrationFields.key.name()));
        replaceKeys.put("joiningDateKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "doj", IntegrationFields.key.name()));
        replaceKeys.put("primaryReportingIdKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "reportingTo", IntegrationFields.key.name()));
        replaceKeys.put("employeeStatusKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("designationNameKey", hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designation", IntegrationFields.key.name()));

        replaceKeys.put("employeeId", hrmsFields.getOrDefault("employeeId", "PF-" + randomID));
        replaceKeys.put("firstName", hrmsFields.getOrDefault("firstName", "Automation-Test"));
        replaceKeys.put("lastName", hrmsFields.getOrDefault("lastName", Faker.instance().name().lastName()));
        replaceKeys.put("userEmail", hrmsFields.getOrDefault("userEmail", "autopf" + randomID + "@" + domainValue));
        replaceKeys.put("joiningDate", hrmsFields.getOrDefault("joiningDate", "2024-01-01T00:00:00.000Z"));
        replaceKeys.put("employeeStatus", hrmsFields.getOrDefault("employeeStatus", "Active"));
        replaceKeys.put("primaryReportingId", hrmsFields.getOrDefault("primaryReportingId", primaryReportingId));
        replaceKeys.put("designationName", hrmsFields.getOrDefault("designationName", "QA Architect"));
        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        iTestContext.setAttribute("isNewUser", true);
        iTestContext.setAttribute("newUserReplaceKeys", replaceKeys);
        Response response = APIExecutor.execute(userContext, createUserDefinition, iTestContext);
        return response;
    }

    public Response searchUser(String clientKey, ITestContext iTestContext, String searchQuery) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        RestRequestDefinition searchUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/hrms/common/search-user.post.json");
        String apiKey = smsConfigService.getAPIKey();
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("clientId", SMSConfigService.getClientId(smsConfigService.userContext));
        replaceKeys.put("search", searchQuery);
        PayloadBuilder.getResolvedDefinition(searchUserDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, searchUserDefinition, iTestContext);
        return response;
    }

    @Step("Search Designation in Designation Master")
    public Response searchDesignation(UserContext userContext, ITestContext iTestContext, String searchQuery) {
        RestRequestDefinition searchDesignationDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/hrms/common/search-designation.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("search", searchQuery);
        PayloadBuilder.getResolvedDefinition(searchDesignationDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, searchDesignationDefinition, iTestContext);
        return response;
    }

    @Step("Get Random Designation Id")
    public Map<String, JsonPath> getRandomDesignationId(UserContext userContext, String searchQuery) {
        RestRequestDefinition designationId = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/inbound/hrms/common/search-designation.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("search", searchQuery);
        PayloadBuilder.getResolvedDefinition(designationId, replaceKeys);
        Response response = APIExecutor.execute(userContext, designationId);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        Random random = new Random();
        Assert.assertNotNull(list, "No enough designationIds to get random designationId");
        return list.get(random.nextInt((list.size() - 1) - 0));
    }

    public Response createDesignation(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> designationMasterFields) {
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/jsonPayloads/create-designation-in-designation-master.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomNumberString = Faker.instance().idNumber().valid().toString();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("designationKey", designationMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "name", IntegrationFields.key.name()));
        replaceKeys.put("designationIdKey", designationMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "extDesignationId", IntegrationFields.key.name()));
        replaceKeys.put("designationLevelKey", designationMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "designationLevel", IntegrationFields.key.name()));
        replaceKeys.put("statusKey", designationMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("skillPrismIdKey", designationMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "designationId", IntegrationFields.key.name()));

        replaceKeys.put("designation", designationMasterFields.getOrDefault("designation", "Role- " + randomNumberString));
        replaceKeys.put("designationId", designationMasterFields.getOrDefault("designationId", randomNumberString));
        replaceKeys.put("designationLevel", designationMasterFields.getOrDefault("designationLevel", "3"));
        replaceKeys.put("status", designationMasterFields.getOrDefault("status", "Active"));
        replaceKeys.put("skillPrismId", designationMasterFields.getOrDefault("skillPrismId", ""));

        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, createUserDefinition, iTestContext);
        return response;
    }

    public Response deleteDesignation(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> designationMasterFields) {
        designationMasterFields.put("status", "Inactive");
        return createDesignation(smsConfigService, userContext, iTestContext, designationMasterFields);
    }

    @Step("Search Project")
    public static Response searchAndReturnProjectId(UserContext userContext, String searchAttribute) {
        RestRequestDefinition projectSearchDefinition = PayloadBuilder.mapJsonToRestDefinition("sms/project/search-project.post.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("search", searchAttribute);
        replaceKeys.put("user.clientId", getClientId(userContext));
        PayloadBuilder.getResolvedDefinition(projectSearchDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, projectSearchDefinition);
        return response;
    }

    public Response createUserWithOptionalFields(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> hrmsFields, boolean IsOptionalFieldValueFilled) {
        String userName = userContext.getSecret().getEmail();
        String[] domain = userName.split("@");
        String domainValue = domain[domain.length - 1];
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/hrms/citiustech/create-user-with-optional-fields.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, userName);
        String primaryReportingId = String.valueOf(userDetails.get("empId"));
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("employeeIdKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name()));
        replaceKeys.put("firstNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "firstName", IntegrationFields.key.name()));
        replaceKeys.put("lastNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "lastName", IntegrationFields.key.name()));
        replaceKeys.put("userEmailKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "email", IntegrationFields.key.name()));
        replaceKeys.put("joiningDateKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "doj", IntegrationFields.key.name()));
        replaceKeys.put("primaryReportingIdKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "reportingTo", IntegrationFields.key.name()));
        replaceKeys.put("employeeStatusKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("designationNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designation", IntegrationFields.key.name()));

        replaceKeys.put("genderKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "gender", IntegrationFields.key.name()));
        replaceKeys.put("contactNumberKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "mobile", IntegrationFields.key.name()));
        replaceKeys.put("profileImageURLKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "profileImg", IntegrationFields.key.name()));
        replaceKeys.put("exitDateKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "doe", IntegrationFields.key.name()));
        replaceKeys.put("designationLevelKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designationLevel", IntegrationFields.key.name()));
        replaceKeys.put("secondaryReportingNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "secondaryReportingTo", IntegrationFields.key.name()));
        replaceKeys.put("baseLocationKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "baseLocation", IntegrationFields.key.name()));
        replaceKeys.put("baseLocationCodeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "baseLocationId", IntegrationFields.key.name()));
        replaceKeys.put("currentLocationKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "currentLocation", IntegrationFields.key.name()));
        replaceKeys.put("currentLocationCodeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "currentLocationId", IntegrationFields.key.name()));
        replaceKeys.put("marketNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "bu", IntegrationFields.key.name()));
        replaceKeys.put("subMarketNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "subBU", IntegrationFields.key.name()));
        replaceKeys.put("experienceAtJoiningKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "experienceAtJoining", IntegrationFields.key.name()));
        replaceKeys.put("employmentCategoryKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "employmentCategory", IntegrationFields.key.name()));
        replaceKeys.put("currentLocationTypeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "onsiteVsOffshore", IntegrationFields.key.name()));
        replaceKeys.put("employeePracticeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "employeePractice", IntegrationFields.key.name()));
        replaceKeys.put("marketIDKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "buId", IntegrationFields.key.name()));
        replaceKeys.put("subMarketIDKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "subBUId", IntegrationFields.key.name()));
        replaceKeys.put("allocCategoryKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "allocationCategory", IntegrationFields.key.name()));
        replaceKeys.put("availDateKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "availableOn", IntegrationFields.key.name()));
        replaceKeys.put("staffingKeySkillKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "staffingKeySkill", IntegrationFields.key.name()));
        replaceKeys.put("userCTIDKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "User_CTID", IntegrationFields.key.name()));
        replaceKeys.put("educationsKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "education", IntegrationFields.key.name()));
        replaceKeys.put("languagesKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "languages", IntegrationFields.key.name()));
        replaceKeys.put("visasKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "visas", IntegrationFields.key.name()));
        replaceKeys.put("previousEmployersKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "pastExperience", IntegrationFields.key.name()));
        replaceKeys.put("tentativeExitDateKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "tentativeExitDate", IntegrationFields.key.name()));
        replaceKeys.put("baseOfficeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeBaseoffice", IntegrationFields.key.name()));
        replaceKeys.put("baseCityKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeBasecity", IntegrationFields.key.name()));
        replaceKeys.put("baseCountryKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeBasecountry", IntegrationFields.key.name()));
        replaceKeys.put("baseOfficePincodeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeBaseofficepincode", IntegrationFields.key.name()));
        replaceKeys.put("currentOfficeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeCurrentoffice", IntegrationFields.key.name()));
        replaceKeys.put("currentCityKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeCurrentcity", IntegrationFields.key.name()));
        replaceKeys.put("currentCountryKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeCurrentcountry", IntegrationFields.key.name()));
        replaceKeys.put("currentOfficePincodeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "EmployeeCurrentofficepincode", IntegrationFields.key.name()));
        replaceKeys.put("proficiencyKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "proficiency", IntegrationFields.key.name()));
        replaceKeys.put("projectReportingManagerKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectReportingManager", IntegrationFields.key.name()));
        replaceKeys.put("practiceManagerKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "practiceManager", IntegrationFields.key.name()));
        replaceKeys.put("practiceLeadKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "practiceLead", IntegrationFields.key.name()));
        replaceKeys.put("regionKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "dataRegion", IntegrationFields.key.name()));
        replaceKeys.put("FTECostingKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "fteCosting", IntegrationFields.key.name()));
        replaceKeys.put("FTEBillingKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "fteBilling", IntegrationFields.key.name()));
        replaceKeys.put("marketCostingKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "mktCosting", IntegrationFields.key.name()));
        replaceKeys.put("employmentCategorySubtypeKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "Employment_Category_Subtype", IntegrationFields.key.name()));
        replaceKeys.put("leaveInfoKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "LeaveInfo", IntegrationFields.key.name()));

        replaceKeys.put("employeeId", hrmsFields.getOrDefault("employeeId", "PF-" + randomID));
        replaceKeys.put("firstName", hrmsFields.getOrDefault("firstName", "Automation-Test"));
        replaceKeys.put("lastName", hrmsFields.getOrDefault("lastName", Faker.instance().name().lastName()));
        replaceKeys.put("userEmail", hrmsFields.getOrDefault("userEmail", "autopf" + randomID + "@" + domainValue));
        replaceKeys.put("joiningDate", hrmsFields.getOrDefault("joiningDate", "2024-01-01T00:00:00.000Z"));
        replaceKeys.put("employeeStatus", hrmsFields.getOrDefault("employeeStatus", "Active"));
        replaceKeys.put("primaryReportingId", hrmsFields.getOrDefault("primaryReportingId", primaryReportingId));
        replaceKeys.put("designationName", hrmsFields.getOrDefault("designationName", "QA Architect"));

        replaceKeys.put("gender", hrmsFields.getOrDefault("gender", IsOptionalFieldValueFilled ? "Male" : ""));
        replaceKeys.put("contactNumber", hrmsFields.getOrDefault("contactNumber", IsOptionalFieldValueFilled ? "+9234567889" : ""));
        replaceKeys.put("profileImageURL", hrmsFields.getOrDefault("profileImageURL", IsOptionalFieldValueFilled ? "" : ""));
        replaceKeys.put("exitDate", hrmsFields.getOrDefault("exitDate", IsOptionalFieldValueFilled ? "2028-02-10T00:00:00.000Z" : ""));
        replaceKeys.put("designationLevel", hrmsFields.getOrDefault("designationLevel", IsOptionalFieldValueFilled ? "5" : ""));
        replaceKeys.put("secondaryReportingName", hrmsFields.getOrDefault("secondaryReportingName", IsOptionalFieldValueFilled ? "VaishaliD" : ""));
        replaceKeys.put("baseLocation", hrmsFields.getOrDefault("baseLocation", IsOptionalFieldValueFilled ? "CT Pune Qubix SEZ1" : ""));
        replaceKeys.put("baseLocationCode", hrmsFields.getOrDefault("baseLocationCode", IsOptionalFieldValueFilled ? "39" : ""));
        replaceKeys.put("currentLocation", hrmsFields.getOrDefault("currentLocation", IsOptionalFieldValueFilled ? "Pune" : ""));
        replaceKeys.put("currentLocationCode", hrmsFields.getOrDefault("currentLocationCode", IsOptionalFieldValueFilled ? "PNQ" : ""));
        replaceKeys.put("marketName", hrmsFields.getOrDefault("marketName", IsOptionalFieldValueFilled ? "HMT" : ""));
        replaceKeys.put("subMarketName", hrmsFields.getOrDefault("subMarketName", IsOptionalFieldValueFilled ? "HMT-3" : ""));
        replaceKeys.put("experienceAtJoining", hrmsFields.getOrDefault("experienceAtJoining", IsOptionalFieldValueFilled ? "8.2" : ""));
        replaceKeys.put("employmentCategory", hrmsFields.getOrDefault("employmentCategory", IsOptionalFieldValueFilled ? "Employee" : ""));
        replaceKeys.put("currentLocationType", hrmsFields.getOrDefault("currentLocationType", IsOptionalFieldValueFilled ? "Offshore" : ""));
        replaceKeys.put("employeePractice", hrmsFields.getOrDefault("employeePractice", IsOptionalFieldValueFilled ? "Engineering Services" : ""));
        replaceKeys.put("marketID", hrmsFields.getOrDefault("marketID", IsOptionalFieldValueFilled ? "789" : ""));
        replaceKeys.put("subMarketID", hrmsFields.getOrDefault("subMarketID", IsOptionalFieldValueFilled ? "1234" : ""));
        replaceKeys.put("allocCategory", hrmsFields.getOrDefault("allocCategory", IsOptionalFieldValueFilled ? "Employee" : ""));
        replaceKeys.put("availDate", hrmsFields.getOrDefault("availDate", IsOptionalFieldValueFilled ? "2025-10-10" : ""));
        replaceKeys.put("staffingKeySkill", hrmsFields.getOrDefault("staffingKeySkill", IsOptionalFieldValueFilled ? "26599317-6113-40c2-b4eb-974cb63e8d8a" : ""));
        replaceKeys.put("userCTID", hrmsFields.getOrDefault("userCTID", IsOptionalFieldValueFilled ? "CTIAdmin016" : ""));
        replaceKeys.put("educations", hrmsFields.getOrDefault("educations", IsOptionalFieldValueFilled ? generateEducationObject() : ""));
        replaceKeys.put("languages", hrmsFields.getOrDefault("languages", IsOptionalFieldValueFilled ? generateLanguageObject() : ""));
        replaceKeys.put("visas", hrmsFields.getOrDefault("visas", IsOptionalFieldValueFilled ? generateVisaObject() : ""));
        replaceKeys.put("previousEmployers", hrmsFields.getOrDefault("previousEmployers", IsOptionalFieldValueFilled ? generatePreviousEmployersObject() : ""));
        replaceKeys.put("tentativeExitDate", hrmsFields.getOrDefault("tentativeExitDate", IsOptionalFieldValueFilled ? "2028-10-08" : ""));
        replaceKeys.put("baseOffice", hrmsFields.getOrDefault("baseOffice", IsOptionalFieldValueFilled ? "CT Pune Qubix SEZ1" : ""));
        replaceKeys.put("baseCity", hrmsFields.getOrDefault("baseCity", IsOptionalFieldValueFilled ? "Pune" : ""));
        replaceKeys.put("baseCountry", hrmsFields.getOrDefault("baseCountry", IsOptionalFieldValueFilled ? "CitiusTech Healthcare Technology Private Limted" : ""));
        replaceKeys.put("baseOfficePincode", hrmsFields.getOrDefault("baseOfficePincode", IsOptionalFieldValueFilled ? "411057" : ""));
        replaceKeys.put("currentOffice", hrmsFields.getOrDefault("currentOffice", IsOptionalFieldValueFilled ? "CitiusTech Pune Qubix SEZ1" : ""));
        replaceKeys.put("currentCity", hrmsFields.getOrDefault("currentCity", IsOptionalFieldValueFilled ? "Pune" : ""));
        replaceKeys.put("currentCountry", hrmsFields.getOrDefault("currentCountry", IsOptionalFieldValueFilled ? "India" : ""));
        replaceKeys.put("currentOfficePincode", hrmsFields.getOrDefault("currentOfficePincode", IsOptionalFieldValueFilled ? "411057" : ""));
        replaceKeys.put("proficiency", hrmsFields.getOrDefault("proficiency", IsOptionalFieldValueFilled ? "PEA" : ""));
        replaceKeys.put("projectReportingManager", hrmsFields.getOrDefault("projectReportingManager", IsOptionalFieldValueFilled ? "ArameswarP" : ""));
        replaceKeys.put("practiceManager", hrmsFields.getOrDefault("practiceManager", IsOptionalFieldValueFilled ? "AdinathS" : ""));
        replaceKeys.put("practiceLead", hrmsFields.getOrDefault("practiceLead", IsOptionalFieldValueFilled ? "AadityaR" : ""));
        replaceKeys.put("region", hrmsFields.getOrDefault("region", IsOptionalFieldValueFilled ? "eu" : ""));
        replaceKeys.put("FTECosting", hrmsFields.getOrDefault("FTECosting", IsOptionalFieldValueFilled ? "100" : ""));
        replaceKeys.put("FTEBilling", hrmsFields.getOrDefault("FTEBilling", IsOptionalFieldValueFilled ? "200" : ""));
        replaceKeys.put("marketCosting", hrmsFields.getOrDefault("marketCosting", IsOptionalFieldValueFilled ? "300" : ""));
        replaceKeys.put("employmentCategorySubtype", hrmsFields.getOrDefault("employmentCategorySubtype", IsOptionalFieldValueFilled ? "Full-Time" : ""));
        replaceKeys.put("leaveInfo", hrmsFields.getOrDefault("leaveInfo", IsOptionalFieldValueFilled ? "" : ""));
        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        iTestContext.setAttribute("isNewUser", true);
        iTestContext.setAttribute("newUserReplaceKeys", replaceKeys);
        Response response = APIExecutor.execute(userContext, createUserDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    @Step("Disable the User Using Admin Login Using Manger Key Having Admin Access")
    public void disableUserWhileLoginAsAdmin(ITestContext iTestContext, String adminClientKey) {
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = (Map<String, Object>) iTestContext.getAttribute("newMenteeUserReplaceKeys");
        replaceKeys.put("employeeStatus", "InActive");
        Response response = userService.createUser(adminClientKey, iTestContext, replaceKeys);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "HRMS API - Updating new user is not working");

    }

    public Response createUserWithSelectedHRMSFieldsForFilters(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> hrmsFields) {
        String userName = userContext.getSecret().getEmail();
        String[] domain = userName.split("@");
        String domainValue = domain[domain.length - 1];
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/myteam/createReporteeUserWithSelectedHRMSTags.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, userName);
        String primaryReportingId = String.valueOf(userDetails.get("empId"));
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("employeeIdKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name()));
        replaceKeys.put("firstNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "firstName", IntegrationFields.key.name()));
        replaceKeys.put("lastNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "lastName", IntegrationFields.key.name()));
        replaceKeys.put("userEmailKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "email", IntegrationFields.key.name()));
        replaceKeys.put("joiningDateKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "doj", IntegrationFields.key.name()));
        replaceKeys.put("primaryReportingIdKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "reportingTo", IntegrationFields.key.name()));
        replaceKeys.put("employeeStatusKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("designationNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designation", IntegrationFields.key.name()));
        replaceKeys.put("currentLocationKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "currentLocation", IntegrationFields.key.name()));
        replaceKeys.put("marketNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "bu", IntegrationFields.key.name()));
        replaceKeys.put("subMarketNameKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "subBU", IntegrationFields.key.name()));
        replaceKeys.put("designationLevelKey", userService.hrmsFieldValuesBasedOnDataKey(smsConfigService, userContext, "designationLevel", IntegrationFields.key.name()));

        replaceKeys.put("employeeId", hrmsFields.getOrDefault("employeeId", "PF-" + randomID));
        replaceKeys.put("firstName", hrmsFields.getOrDefault("firstName", "Automation-Test"));
        replaceKeys.put("lastName", hrmsFields.getOrDefault("lastName", Faker.instance().name().lastName()));
        replaceKeys.put("userEmail", hrmsFields.getOrDefault("userEmail", "autopf" + randomID + "@" + domainValue));
        replaceKeys.put("joiningDate", hrmsFields.getOrDefault("joiningDate", "2024-01-01T00:00:00.000Z"));
        replaceKeys.put("employeeStatus", hrmsFields.getOrDefault("employeeStatus", "Active"));
        replaceKeys.put("primaryReportingId", hrmsFields.getOrDefault("primaryReportingId", primaryReportingId));
        replaceKeys.put("designationName", hrmsFields.getOrDefault("designationName", "AUTOMATION LEAD"));
        replaceKeys.put("currentLocation", hrmsFields.getOrDefault("currentLocation", "Pune"));
        replaceKeys.put("marketName", hrmsFields.getOrDefault("marketName", "HMT"));
        replaceKeys.put("subMarketName", hrmsFields.getOrDefault("subMarketName", "HMT-3"));
        replaceKeys.put("designationLevel", hrmsFields.getOrDefault("designationLevel", "5"));
        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, createUserDefinition, iTestContext);
        return response;
    }
    @Step("Update Existing Mentee User Designation/Role")
    public void updateExistingMenteeUserDesignationOrRoleThroughAPI(ITestContext iTestContext,String designationId,String menteeKey,String managerKey)
    {
        SMSConfigService smsConfigService = new SMSConfigService(menteeKey);
        String menteeuserEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,menteeKey);
        Map<String, JsonPath> menteeuserDetails = smsConfigService.getUserDetails(iTestContext, menteeuserEmailId);
        String menteeUserfirstName = String.valueOf(menteeuserDetails.get("firstName"));
        String menteeUserlastName = String.valueOf(menteeuserDetails.get("lastName"));
        String menteeEmpId = String.valueOf(menteeuserDetails.get("empId"));
        /* Get ReportingId */
        String managerUserEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,managerKey);
        Map<String, JsonPath> manageruserDetails = smsConfigService.getUserDetails(iTestContext, managerUserEmailId);
        String primaryReportingId = String.valueOf(manageruserDetails.get("empId"));

        /* Update User With Designation ID */
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("employeeId",menteeEmpId);
        replaceKeys.put("firstName",menteeUserfirstName);
        replaceKeys.put("lastName",menteeUserlastName);
        replaceKeys.put("userEmail",menteeuserEmailId);
        replaceKeys.put("employeeStatus", "Active");
        replaceKeys.put("primaryReportingId", primaryReportingId);
        replaceKeys.put("designationName",designationId);
        replaceKeys.put("joiningDate","2024-01-01T00:00:00.000Z");
        Response response = userService.createUser(menteeKey, iTestContext, replaceKeys);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"),"Success","Creating new user API not working");



    }

    /*Employee Visa creation through Inbound API*/
    public Response createEmployeeVisa(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> visaFields) {
        RestRequestDefinition createUserDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/employeeVisa/create-employee-visa.post.json");

        String apiKey = smsConfigService.getAPIKey();
        String userName = userContext.getSecret().getEmail();
        String randomID = Faker.instance().idNumber().valid().toString();
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, userName);
        String employeeId = String.valueOf(userDetails.get("empId"));
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("employeeIdKey", userService.employeeVisaFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name()));
        replaceKeys.put("statusKey", userService.employeeVisaFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name()));
        replaceKeys.put("visasKey", userService.employeeVisaFieldValuesBasedOnDataKey(smsConfigService, userContext, "visas", IntegrationFields.key.name()));

        replaceKeys.put("employeeId", visaFields.getOrDefault("employeeId", employeeId));
        replaceKeys.put("status", visaFields.getOrDefault("status", "Active"));
        replaceKeys.put("visas", visaFields.getOrDefault("visas", generateVisaObject()));

        PayloadBuilder.getResolvedDefinition(createUserDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, createUserDefinition, iTestContext);
        return response;
    }

    /*To do : Implement after test hook and add this method there*/
    public void disableUserHook(ITestContext iTestContext, String clientKey) {
        UserService userService = new UserService();
        if (Objects.nonNull(iTestContext.getAttribute("isNewUser")) && Objects.nonNull(iTestContext.getAttribute("newUserReplaceKeys"))) {
            Map<String, Object> replaceKeys = (Map<String, Object>) iTestContext.getAttribute("newUserReplaceKeys");
            replaceKeys.put("employeeStatus", "InActive");
            Response response = userService.createUser(clientKey, iTestContext, replaceKeys);
            Assert.assertEquals(response.getStatusCode(), 200);
            Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "HRMS API - Updating new user is not working");
        }
    }
}