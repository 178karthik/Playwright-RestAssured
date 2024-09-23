package com.karthik178.configservice.sms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.karthik178.apimanager.enums.LmsRequiredFields;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.playwritemanager.utils.DateUtils;
import com.karthik178.playwritemanager.utils.JavaFakerUtils;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.*;

import static com.karthik178.configservice.sms.SMSConfigService.getClientId;

public class CourseService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private List<JsonNode> allCourses;
    protected ObjectMapper objectMapper;
    public CourseService()
    {
        objectMapper = new ObjectMapper();
    }

    @Step("Get LMS API keys configured in integrations :: {dataKey}")
    public String lmsRequiredFieldValuesBasedOnDataKey(String clientKey, String dataKey, String key) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        return lmsRequiredFieldValuesBasedOnDataKey(smsConfigService, dataKey, key);
    }

    @Step("Get LMS API keys configured in integrations :: {dataKey}")
    public String lmsRequiredFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(smsConfigService.userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.courseRequiredFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.courseRequiredFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.courseRequiredFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get LMS Employee Rating API keys configured in integrations")
    public String lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(String clientKey, String dataKey, String key) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        return lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, dataKey, key);
    }

    @Step("Get LMS Employee Rating API keys configured in integrations")
    public String lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(smsConfigService.userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.lmsRequiredFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.lmsRequiredFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.lmsRequiredFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    public Response createCourse(String clientKey, ITestContext iTestContext, Map<String, Object> lmsFields) {

        RestRequestDefinition createCourseDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/lms/create-course-mandatory-fields-only.post.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("courseIdKey", lmsFields.getOrDefault("courseIdKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "extCourseId", LmsRequiredFields.key.name())));
        replaceKeys.put("courseNameKey", lmsFields.getOrDefault("courseNameKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "courseName", LmsRequiredFields.key.name())));
        replaceKeys.put("statusKey", lmsFields.getOrDefault("statusKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "status", LmsRequiredFields.key.name())));

        replaceKeys.put("courseId", lmsFields.getOrDefault("courseId", "TestCourse-" + randomID));
        replaceKeys.put("courseName", lmsFields.getOrDefault("courseName", "TestCourse-" + Faker.instance().idNumber().valid()));
        replaceKeys.put("status", lmsFields.getOrDefault("status", "Active"));
        PayloadBuilder.getResolvedDefinition(createCourseDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createCourseDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }
    public Response createCourseWithSkills(String clientKey, ITestContext iTestContext, Map<String, Object> lmsFields) {

        RestRequestDefinition createCourseDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/lms/create-course-with-skills.post.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        UserSkillService skillService = new UserSkillService();
        String skillId = skillService.getRandomSkillIDNotAssociateToUser(smsConfigService.getUserContext());
        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("courseIdKey", lmsFields.getOrDefault("courseIdKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "extCourseId", LmsRequiredFields.key.name())));
        replaceKeys.put("courseNameKey", lmsFields.getOrDefault("courseNameKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "courseName", LmsRequiredFields.key.name())));
        replaceKeys.put("statusKey", lmsFields.getOrDefault("statusKey", lmsRequiredFieldValuesBasedOnDataKey(clientKey, "status", LmsRequiredFields.key.name())));
        replaceKeys.put("skillsKey", "Skills");

        replaceKeys.put("courseId", lmsFields.getOrDefault("courseId", "TestCourse-" + randomID));
        replaceKeys.put("courseName", lmsFields.getOrDefault("courseName", "TestCourse-" + Faker.instance().idNumber().valid()));
        replaceKeys.put("status", lmsFields.getOrDefault("status", "Active"));
        replaceKeys.put("skillIds", lmsFields.getOrDefault("skillIds", skillId));
        PayloadBuilder.getResolvedDefinition(createCourseDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createCourseDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response createCourseWithSkills(SMSConfigService smsConfigService, ITestContext iTestContext, Map<String, Object> lmsFields) {

        RestRequestDefinition createCourseDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/lms/create-course-with-skills.post.json");

        UserSkillService skillService = new UserSkillService();
        String skillId = skillService.getRandomSkillIDNotAssociateToUser(smsConfigService.getUserContext());
        String apiKey = smsConfigService.getAPIKey();
        String randomID = Faker.instance().idNumber().valid();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("courseIdKey", lmsFields.getOrDefault("courseIdKey", lmsRequiredFieldValuesBasedOnDataKey(smsConfigService, "extCourseId", LmsRequiredFields.key.name())));
        replaceKeys.put("courseNameKey", lmsFields.getOrDefault("courseNameKey", lmsRequiredFieldValuesBasedOnDataKey(smsConfigService, "courseName", LmsRequiredFields.key.name())));
        replaceKeys.put("statusKey", lmsFields.getOrDefault("statusKey", lmsRequiredFieldValuesBasedOnDataKey(smsConfigService, "status", LmsRequiredFields.key.name())));
        replaceKeys.put("skillsKey", "Skills");

        replaceKeys.put("courseId", lmsFields.getOrDefault("courseId", "TestCourse-" + randomID));
        replaceKeys.put("courseName", lmsFields.getOrDefault("courseName", "TestCourse-" + Faker.instance().idNumber().valid()));
        replaceKeys.put("status", lmsFields.getOrDefault("status", "Active"));
        replaceKeys.put("skillIds", lmsFields.getOrDefault("skillIds", skillId));
        PayloadBuilder.getResolvedDefinition(createCourseDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createCourseDefinition, iTestContext);
        System.out.println("Response is : " + response.body().prettyPrint());
        return response;
    }

    public Response searchCourse(String clientKey, ITestContext iTestContext, String searchQuery) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        return searchCourse(smsConfigService, iTestContext, searchQuery);
    }

    public Response searchCourse(SMSConfigService smsConfigService, ITestContext iTestContext, String searchQuery) {
        RestRequestDefinition searchCourseDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/lms/search-course.post.json");
        String apiKey = smsConfigService.getAPIKey();
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("clientId", SMSConfigService.getClientId(smsConfigService.userContext));
        replaceKeys.put("search", searchQuery);
        PayloadBuilder.getResolvedDefinition(searchCourseDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, searchCourseDefinition, iTestContext);
        return response;
    }

    public List<Map<String, JsonPath>> getAllCoursesAvailableForGivenUser(String clientKey, ITestContext iTestContext,int limit) {
        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        RestRequestDefinition getAllCoursesAvailableDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/courses/get-all-courses-available-to-user.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(smsConfigService.userContext));
        replaceKeys.put("userId", SMSConfigService.getUserId(clientKey));
        replaceKeys.put("limit", limit);
        PayloadBuilder.getResolvedDefinition(getAllCoursesAvailableDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, getAllCoursesAvailableDefinition, iTestContext);
        List<Map<String, JsonPath>> list = response.getBody().jsonPath().getList("searchData");
        return list;
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
      @Step("Assign Course Through Inbound API with only mandatory fields")
     public void assignCourseThroughInboundAPI(String clientKey,UserContext userContext,ITestContext iTestContext) {
          SMSConfigService smsConfigService = new SMSConfigService(clientKey);
          assignCourseThroughInboundAPI(smsConfigService, userContext, iTestContext);
    }
    @Step("Assign Course Through Inbound API with only mandatory fields")
    public void assignCourseThroughInboundAPI(SMSConfigService smsConfigService,UserContext userContext,ITestContext iTestContext) {
        /*getCourseDetails*/
        List<JsonNode> courses = getCourses(userContext);
        Assert.assertTrue(courses.size() > 0, "No Courses present");
        int randomInt = JavaFakerUtils.getRandomNumberWithRange(0, courses.size() - 1);
        /* Fetch courseId */
        String courseId = courses.get(randomInt).get("extCourseId").textValue();
        /* Fetch courseName */
        String courseName = courses.get(randomInt).get("courseName").textValue();
        /* Fetch courseDescription */
        String courseDescription = courses.get(randomInt).get("courseDescription").textValue();

        int  actualprogressPercentage=30;
        String apiKey = smsConfigService.getAPIKey();
        String employeeId = smsConfigService.getEmployeeIdFromUserDetailsAPI(iTestContext,smsConfigService.userContext.getSecret());
        /* Assign Course through Inbound API */
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/inbound/assigncourse-only-mandatory-fields.json");
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("courseIdKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "extCourseId", LmsRequiredFields.key.name()));
        replaceKeys.put("usernameKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "empId", LmsRequiredFields.key.name()));
        replaceKeys.put("start_dateKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "startDt", LmsRequiredFields.key.name()));
        replaceKeys.put("statusKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "status", LmsRequiredFields.key.name()));
        replaceKeys.put("isSpecializationKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "isSpecialization", LmsRequiredFields.key.name()));
        replaceKeys.put("program_typeKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "programType", LmsRequiredFields.key.name()));

        replaceKeys.put("x-prismforce-key",apiKey);
        replaceKeys.put("username", employeeId);
        replaceKeys.put("courseId", courseId);
        replaceKeys.put("progress", actualprogressPercentage);
        replaceKeys.put("start_date", DateUtils.getCurrentDateAndTime("yyyy-MM-dd"));
        replaceKeys.put("status", "Active");

        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"),"Success","Assign Course Inbound API is not working");
        iTestContext.setAttribute("replaceKeys",replaceKeys);
        iTestContext.setAttribute("courseDescription",courseDescription);
        iTestContext.setAttribute("courseName",courseName);
    }
    @Step("Assign Course Through Inbound API with only mandatory fields")
    public void assignCourseThroughInboundAPI(String clientKey,UserContext userContext,ITestContext iTestContext,Map<String, Object> lmsFields) {

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        assignCourseThroughInboundAPI(smsConfigService, userContext, iTestContext, lmsFields);
    }

    @Step("Assign Course Through Inbound API with only mandatory fields")
    public void assignCourseThroughInboundAPI(SMSConfigService smsConfigService,UserContext userContext,ITestContext iTestContext,Map<String, Object> lmsFields) {
        /*getCourseDetails*/
        List<JsonNode> courses = getCourses(userContext);
        Assert.assertTrue(courses.size() > 0, "No Courses present");
        int randomInt = JavaFakerUtils.getRandomNumberWithRange(0, courses.size() - 1);
        /* Fetch courseId */
        String courseId = courses.get(randomInt).get("extCourseId").textValue();
        /* Fetch courseName */
        String courseName = courses.get(randomInt).get("courseName").textValue();
        /* Fetch courseDescription */
        String courseDescription = courses.get(randomInt).get("courseDescription").textValue();

        int  actualprogressPercentage=30;
        String apiKey = smsConfigService.getAPIKey();
        String employeeId = smsConfigService.getEmployeeIdFromUserDetailsAPI(iTestContext, smsConfigService.userContext.getSecret());
        /* Assign Course through Inbound API */
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/inbound/assigncourse-only-mandatory-fields.json");
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("courseIdKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "extCourseId", LmsRequiredFields.key.name()));
        replaceKeys.put("usernameKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "empId", LmsRequiredFields.key.name()));
        replaceKeys.put("start_dateKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "startDt", LmsRequiredFields.key.name()));
        replaceKeys.put("statusKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "status", LmsRequiredFields.key.name()));
        replaceKeys.put("isSpecializationKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "isSpecialization", LmsRequiredFields.key.name()));
        replaceKeys.put("program_typeKey", lmsEmployeeRatingRequiredFieldValuesBasedOnDataKey(smsConfigService, "programType", LmsRequiredFields.key.name()));

        replaceKeys.put("x-prismforce-key",apiKey);
        replaceKeys.put("username", employeeId);
        replaceKeys.put("courseId", lmsFields.getOrDefault("courseId",courseId));
        replaceKeys.put("progress", actualprogressPercentage);
        replaceKeys.put("start_date", DateUtils.getCurrentDateAndTime("yyyy-MM-dd"));
        replaceKeys.put("status", "Active");

        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"),"Success","Assign Course Inbound API is not working");
        iTestContext.setAttribute("replaceKeys",replaceKeys);
        iTestContext.setAttribute("courseDescription",courseDescription);
        iTestContext.setAttribute("courseName",courseName);
    }

    public void removeCourseAssignedThroughInboundAPI(UserContext userContext,ITestContext iTestContext) {
        Map<String, Object> replaceKeys = (Map<String, Object>) iTestContext.getAttribute("replaceKeys");
        /* Remove Course through Inbound API */
        RestRequestDefinition saveIntegrationsDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/inbound/assigncourse-only-mandatory-fields.json");
        replaceKeys.put("status", "InActive");
        PayloadBuilder.getResolvedDefinition(saveIntegrationsDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveIntegrationsDef, iTestContext);
        System.out.println("the remove assigned course api response is"+response.body().prettyPrint());
        Assert.assertEquals(response.getStatusCode(), 200);
    }


}
