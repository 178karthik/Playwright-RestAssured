package com.karthik178.configservice.sms;

import com.github.javafaker.Faker;
import com.karthik178.apimanager.enums.IntegrationFields;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.configservice.common.BaseRestTest;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.*;

public class ProjectService extends BaseRestTest {

    @Step("Get ProjectMaster API keys configured :: {dataKey}")
    public String projectMasterFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.pmsMasterFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.pmsMasterFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.pmsMasterFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    @Step("Get ProjectAllocation API keys configured :: {dataKey}")
    public String projectAllocationFieldValuesBasedOnDataKey(SMSConfigService smsConfigService, UserContext userContext, String dataKey, String key) {
        Response response = smsConfigService.getSMSSettingsResponse(userContext);
        List<String> allDataKeys = response.jsonPath().getList("client.integrations.pmsFields.dataKey");
        int index = allDataKeys.indexOf(dataKey);
        String keyValue = response.jsonPath().getString(String.format("client.integrations.pmsFields[%d].%s", index, key));
        AllureLogger.info(String.format("client.integrations.pmsFields[%d].%s", index, key));
        System.out.println("Key value received for dataKey " + dataKey + " is : " + keyValue);
        return keyValue;
    }

    public Response createProjectMasterMandatoryFields(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> pmsMasterFields) {
        RestRequestDefinition createProjectMasterDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/projectMaster/create-project-master-mandatodry-fields-only.post.json");
        String apiKey = smsConfigService.getAPIKey();
        String projectId = "IA-" + Faker.instance().idNumber().valid();
        String projectName = "Inbound-Automation-" + Faker.instance().name().lastName();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("subProjectNameKey", pmsMasterFields.getOrDefault("subProjectNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "extProjectId", IntegrationFields.key.name())));
        replaceKeys.put("subProjectStartKey", pmsMasterFields.getOrDefault("subProjectStartKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "startDt", IntegrationFields.key.name())));
        replaceKeys.put("subProjectEndKey", pmsMasterFields.getOrDefault("subProjectEndKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "endDt", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjectNameKey", pmsMasterFields.getOrDefault("qmsProjectNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectName", IntegrationFields.key.name())));
        replaceKeys.put("pgsStatusKey", pmsMasterFields.getOrDefault("pgsStatusKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name())));

        replaceKeys.put("subProjectName", pmsMasterFields.getOrDefault("subProjectName", projectId));
        replaceKeys.put("subProjectStart", pmsMasterFields.getOrDefault("subProjectStart", "20-10-2020"));
        replaceKeys.put("subProjectEnd", pmsMasterFields.getOrDefault("subProjectEnd", "20-03-2028"));
        replaceKeys.put("qmsProjectName", pmsMasterFields.getOrDefault("qmsProjectName", projectName));
        replaceKeys.put("pgsStatus", pmsMasterFields.getOrDefault("pgsStatus", "Active"));

        PayloadBuilder.getResolvedDefinition(createProjectMasterDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, createProjectMasterDefinition, iTestContext);
        return response;
    }

    public Response assignProject(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> pmsFields) {
        RestRequestDefinition assignUserProjectDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/projectAllocation/allocate-project-mandatory-fields-only.post.json");
        String apiKey = smsConfigService.getAPIKey();
        String randomNumber= Faker.instance().idNumber().valid();

        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        String userName = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("projectCodeKey", pmsFields.getOrDefault("projectCodeKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "extProjectId", IntegrationFields.key.name())));
        replaceKeys.put("userNameKey", pmsFields.getOrDefault("userNameKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name())));
        replaceKeys.put("allocationStartDateKey", pmsFields.getOrDefault("allocationStartDateKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "startDt", IntegrationFields.key.name())));
        replaceKeys.put("allocationEndDateKey", pmsFields.getOrDefault("allocationEndDateKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "endDt", IntegrationFields.key.name())));
        replaceKeys.put("pfRecordIdKey", pmsFields.getOrDefault("pfRecordIdKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "pfRecordId", IntegrationFields.key.name())));
        replaceKeys.put("actionPerformedKey", pmsFields.getOrDefault("actionPerformedKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "actionPerformed", IntegrationFields.key.name())));

        replaceKeys.put("projectCode", pmsFields.getOrDefault("projectCode", smsConfigService.getRandomProjectIDNotAssociateToUser(userContext)));
        replaceKeys.put("userName", pmsFields.getOrDefault("userName", userName));
        replaceKeys.put("allocationStartDate", pmsFields.getOrDefault("allocationStartDate", "20-03-2020"));
        replaceKeys.put("allocationEndDate", pmsFields.getOrDefault("allocationEndDate", "20-03-2028"));
        replaceKeys.put("pfRecordId", pmsFields.getOrDefault("pfRecordId", randomNumber));
        replaceKeys.put("actionPerformed", pmsFields.getOrDefault("actionPerformed", "Created"));

        PayloadBuilder.getResolvedDefinition(assignUserProjectDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, assignUserProjectDefinition, iTestContext);
        return response;
    }

    public Response assignProjectWithOptionalFields(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> pmsFields, boolean IsOptionalFieldValueFilled) {
        RestRequestDefinition assignUserProjectDefinition = PayloadBuilder.
                mapJsonToRestDefinition("sms/integrations/inbound/projectAllocation/allocate-project-optional-fields.post.json");
        String apiKey = smsConfigService.getAPIKey();
        String randomNumber= Faker.instance().idNumber().valid();

        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, smsConfigService.userContext.getSecret().getEmail());
        String userName = String.valueOf(userDetails.get("empId"));

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("projectCodeKey", pmsFields.getOrDefault("projectCodeKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "extProjectId", IntegrationFields.key.name())));
        replaceKeys.put("userNameKey", pmsFields.getOrDefault("userNameKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "empId", IntegrationFields.key.name())));
        replaceKeys.put("allocationStartDateKey", pmsFields.getOrDefault("allocationStartDateKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "startDt", IntegrationFields.key.name())));
        replaceKeys.put("allocationEndDateKey", pmsFields.getOrDefault("allocationEndDateKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "endDt", IntegrationFields.key.name())));
        replaceKeys.put("pfRecordIdKey", pmsFields.getOrDefault("pfRecordIdKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "pfRecordId", IntegrationFields.key.name())));
        replaceKeys.put("actionPerformedKey", pmsFields.getOrDefault("actionPerformedKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "actionPerformed", IntegrationFields.key.name())));

        replaceKeys.put("skillRatingKey", pmsFields.getOrDefault("skillRatingKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectRating", IntegrationFields.key.name())));
        replaceKeys.put("roleKey", pmsFields.getOrDefault("roleKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectEmployeeRole", IntegrationFields.key.name())));
        replaceKeys.put("allocationPercKey", pmsFields.getOrDefault("allocationPercKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectAllocationPercent", IntegrationFields.key.name())));
        replaceKeys.put("userCTIDKey", pmsFields.getOrDefault("userCTIDKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "User_CTID", IntegrationFields.key.name())));
        replaceKeys.put("employeeLocationKey", pmsFields.getOrDefault("employeeLocationKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Employee_Location", IntegrationFields.key.name())));
        replaceKeys.put("currentLocationKey", pmsFields.getOrDefault("currentLocationKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Current_Location", IntegrationFields.key.name())));
        replaceKeys.put("allocation_StatusKey", pmsFields.getOrDefault("allocation_StatusKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Allocation_Status", IntegrationFields.key.name())));
        replaceKeys.put("mainProjectNameKey", pmsFields.getOrDefault("mainProjectNameKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Main_Project_Name", IntegrationFields.key.name())));
        replaceKeys.put("NAReasonKey", pmsFields.getOrDefault("NAReasonKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "NA_Reason", IntegrationFields.key.name())));
        replaceKeys.put("actionFormedKey", pmsFields.getOrDefault("actionFormedKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Action_Performed", IntegrationFields.key.name())));
        replaceKeys.put("availableOnKey", pmsFields.getOrDefault("availableOnKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "Available_on", IntegrationFields.key.name())));
        replaceKeys.put("allocationTimeKey", pmsFields.getOrDefault("allocationTimeKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "AllocationTime", IntegrationFields.key.name())));
        replaceKeys.put("statusKey", pmsFields.getOrDefault("statusKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name())));
        replaceKeys.put("allocationStatusKey", pmsFields.getOrDefault("allocationStatusKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "allocationStatus", IntegrationFields.key.name())));
        replaceKeys.put("allocationSubStatus1Key", pmsFields.getOrDefault("allocationSubStatus1Key", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "alcSubStatus1", IntegrationFields.key.name())));
        replaceKeys.put("allocationSubStatus2Key", pmsFields.getOrDefault("allocationSubStatus2Key", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "alcSubStatus2", IntegrationFields.key.name())));
        replaceKeys.put("allocationPoolCodeKey", pmsFields.getOrDefault("allocationPoolCodeKey", projectAllocationFieldValuesBasedOnDataKey(smsConfigService, userContext, "pfAlcPoolCode", IntegrationFields.key.name())));

        replaceKeys.put("projectCode", pmsFields.getOrDefault("projectCode", smsConfigService.getRandomProjectIDNotAssociateToUser(userContext)));
        replaceKeys.put("userName", pmsFields.getOrDefault("userName", userName));
        replaceKeys.put("allocationStartDate", pmsFields.getOrDefault("allocationStartDate", "20-03-2020"));
        replaceKeys.put("allocationEndDate", pmsFields.getOrDefault("allocationEndDate", "20-03-2028"));
        replaceKeys.put("pfRecordId", pmsFields.getOrDefault("pfRecordId", randomNumber));
        replaceKeys.put("actionPerformed", pmsFields.getOrDefault("actionPerformed", "Created"));

        replaceKeys.put("skillRating", pmsFields.getOrDefault("skillRating", IsOptionalFieldValueFilled ? "1" : ""));
        replaceKeys.put("role", pmsFields.getOrDefault("role", IsOptionalFieldValueFilled ? "Associate - II Role" : ""));
        replaceKeys.put("allocationPerc", pmsFields.getOrDefault("allocationPerc", IsOptionalFieldValueFilled ? "100" : ""));
        replaceKeys.put("userCTID", pmsFields.getOrDefault("userCTID", IsOptionalFieldValueFilled ? "CT123" : ""));
        replaceKeys.put("employeeLocation", pmsFields.getOrDefault("employeeLocation", IsOptionalFieldValueFilled ? "Pune Sez" : ""));
        replaceKeys.put("currentLocation", pmsFields.getOrDefault("currentLocationKey", IsOptionalFieldValueFilled ? "Banglore" : ""));
        replaceKeys.put("allocation_Status", pmsFields.getOrDefault("allocation_Status", IsOptionalFieldValueFilled ? "Active" : ""));
        replaceKeys.put("mainProjectName", pmsFields.getOrDefault("mainProjectName", IsOptionalFieldValueFilled ? "IOT CloudA03-18-June-2024" : ""));
        replaceKeys.put("NAReason", pmsFields.getOrDefault("NAReason", IsOptionalFieldValueFilled ? "Testing Reason" : ""));
        replaceKeys.put("actionFormed", pmsFields.getOrDefault("actionFormed", IsOptionalFieldValueFilled ? "Active Formed" : ""));
        replaceKeys.put("availableOn", pmsFields.getOrDefault("availableOn", IsOptionalFieldValueFilled ? "2024-11-11" : ""));
        replaceKeys.put("allocationTime", pmsFields.getOrDefault("allocationTime", IsOptionalFieldValueFilled ? "10:00:00" : ""));
        replaceKeys.put("status", pmsFields.getOrDefault("status", IsOptionalFieldValueFilled ? "Active" : ""));
        replaceKeys.put("allocationStatus", pmsFields.getOrDefault("allocationStatus", IsOptionalFieldValueFilled ? "Inprogress" : ""));
        replaceKeys.put("allocationSubStatus1", pmsFields.getOrDefault("allocationSubStatus1", IsOptionalFieldValueFilled ? "Inprogress-1" : ""));
        replaceKeys.put("allocationSubStatus2", pmsFields.getOrDefault("allocationSubStatus2", IsOptionalFieldValueFilled ? "Inprogress-2" : ""));
        replaceKeys.put("allocationPoolCode", pmsFields.getOrDefault("allocationPoolCode", IsOptionalFieldValueFilled ? "Inprogress 123" : ""));

        PayloadBuilder.getResolvedDefinition(assignUserProjectDefinition, replaceKeys);
        Response response = APIExecutor.execute(smsConfigService.userContext, assignUserProjectDefinition, iTestContext);
        return response;
    }

    public Response createProjectWithOptionalFields(SMSConfigService smsConfigService, UserContext userContext, ITestContext iTestContext, Map<String, Object> pmsMasterFields, boolean IsOptionalFieldValueFilled) {
        RestRequestDefinition createProjectMasterDefinition = PayloadBuilder
                .mapJsonToRestDefinition("sms/integrations/inbound/projectMaster/create-project-master-optional-fields.post.json");
        String apiKey = smsConfigService.getAPIKey();
        UserSkillService userSkillService = new UserSkillService();
        String projectId = "IA-" + getRandomStringId();
        String projectName = "Inbound-Automation-" + getRandomStringId();
        Map<String, Object> replaceKeys = new HashMap<>();

        replaceKeys.put("x-prismforce-key", apiKey);
        replaceKeys.put("subProjectNameKey", pmsMasterFields.getOrDefault("subProjectNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "extProjectId", IntegrationFields.key.name())));
        replaceKeys.put("subProjectStartKey", pmsMasterFields.getOrDefault("subProjectStartKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "startDt", IntegrationFields.key.name())));
        replaceKeys.put("subProjectEndKey", pmsMasterFields.getOrDefault("subProjectEndKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "endDt", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjectNameKey", pmsMasterFields.getOrDefault("qmsProjectNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectName", IntegrationFields.key.name())));

        replaceKeys.put("pgsStatusKey", pmsMasterFields.getOrDefault("pgsStatusKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "status", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjectSkillDataKey", pmsMasterFields.getOrDefault("qmsProjectSkillDataKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "skillIds", IntegrationFields.key.name())));
        replaceKeys.put("projectCustomerIDKey", pmsMasterFields.getOrDefault("projectCustomerIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectCustomerId", IntegrationFields.key.name())));
        replaceKeys.put("projectCustomerNameKey", pmsMasterFields.getOrDefault("projectCustomerNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectCustomerName", IntegrationFields.key.name())));
        replaceKeys.put("domainKey", pmsMasterFields.getOrDefault("domainKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectCustomerDomain", IntegrationFields.key.name())));
        replaceKeys.put("locationKey", pmsMasterFields.getOrDefault("locationKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectLocation", IntegrationFields.key.name())));
        replaceKeys.put("imageKey", pmsMasterFields.getOrDefault("imageKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "logo", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjectIDKey", pmsMasterFields.getOrDefault("qmsProjectIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "QMS_Project_ID", IntegrationFields.key.name())));
        replaceKeys.put("qmsStatusKey", pmsMasterFields.getOrDefault("qmsStatusKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "QMSstatus", IntegrationFields.key.name())));
        replaceKeys.put("qmsDescriptionKey", pmsMasterFields.getOrDefault("qmsDescriptionKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectDescription", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjStartDateKey", pmsMasterFields.getOrDefault("qmsProjStartDateKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "QMSProjStartDate", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjEndDateKey", pmsMasterFields.getOrDefault("qmsProjEndDateKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "QMSProjEndDate", IntegrationFields.key.name())));
        replaceKeys.put("subProjectStatusKey", pmsMasterFields.getOrDefault("subProjectStatusKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_Status", IntegrationFields.key.name())));
        replaceKeys.put("subProjectSecondaryFCOwnerNameKey", pmsMasterFields.getOrDefault("subProjectSecondaryFCOwnerNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_Secondary_FC_owner_name", IntegrationFields.key.name())));
        replaceKeys.put("subProjectSecondaryFCOwnerCTIDKey", pmsMasterFields.getOrDefault("subProjectSecondaryFCOwnerCTIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_Secondary_FC_owner_CTID", IntegrationFields.key.name())));
        replaceKeys.put("subProjectSecondaryFCOwnerEmpIDKey", pmsMasterFields.getOrDefault("subProjectSecondaryFCOwnerEmpIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_Secondary_FC_owner_EmpID", IntegrationFields.key.name())));
        replaceKeys.put("pgsProjectIDKey", pmsMasterFields.getOrDefault("pgsProjectIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "PGS_Project_ID", IntegrationFields.key.name())));
        replaceKeys.put("subProjectFinanceCodeSKKey", pmsMasterFields.getOrDefault("subProjectFinanceCodeSKKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_Finance_Code_SK", IntegrationFields.key.name())));
        replaceKeys.put("subProjectFinanceCodeRoleKey", pmsMasterFields.getOrDefault("subProjectFinanceCodeRoleKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_FinanceCodeRole", IntegrationFields.key.name())));
        replaceKeys.put("pgsProjectNameKey", pmsMasterFields.getOrDefault("pgsProjectNameKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "PGS_Project_Name", IntegrationFields.key.name())));
        replaceKeys.put("subProjectIDKey", pmsMasterFields.getOrDefault("subProjectIDKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "Sub_Project_ID", IntegrationFields.key.name())));
        replaceKeys.put("subProjectBillTypeKey", pmsMasterFields.getOrDefault("subProjectBillTypeKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectBillType", IntegrationFields.key.name())));
        replaceKeys.put("subProjectTypeKey", pmsMasterFields.getOrDefault("subProjectTypeKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectArcheType", IntegrationFields.key.name())));
        replaceKeys.put("subProjectVerticalKey", pmsMasterFields.getOrDefault("subProjectVerticalKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectBU", IntegrationFields.key.name())));
        replaceKeys.put("subProjectSubVerticalKey", pmsMasterFields.getOrDefault("subProjectSubVerticalKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectSubBU", IntegrationFields.key.name())));
        replaceKeys.put("subProjectLegalEntityKey", pmsMasterFields.getOrDefault("subProjectLegalEntityKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "legalEntity", IntegrationFields.key.name())));
        replaceKeys.put("qmsProjTypeKey", pmsMasterFields.getOrDefault("qmsProjTypeKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "projectType", IntegrationFields.key.name())));
        replaceKeys.put("engagementMaturityModelKey", pmsMasterFields.getOrDefault("engagementMaturityModelKey", projectMasterFieldValuesBasedOnDataKey(smsConfigService, userContext, "engagementMaturityModel", IntegrationFields.key.name())));

        replaceKeys.put("subProjectName", pmsMasterFields.getOrDefault("subProjectName", projectId));
        replaceKeys.put("subProjectStart", pmsMasterFields.getOrDefault("subProjectStart", "20-10-2020"));
        replaceKeys.put("subProjectEnd", pmsMasterFields.getOrDefault("subProjectEnd", "20-03-2028"));
        replaceKeys.put("qmsProjectName", pmsMasterFields.getOrDefault("qmsProjectName", projectName));

        replaceKeys.put("pgsStatus", pmsMasterFields.getOrDefault("pgsStatus", IsOptionalFieldValueFilled ? "Active" : ""));
        replaceKeys.put("qmsProjectSkillData", pmsMasterFields.getOrDefault("qmsProjectSkillData", IsOptionalFieldValueFilled ? userSkillService.getRandomSkillIDNotAssociateToUser(userContext) : ""));
        replaceKeys.put("projectCustomerID", pmsMasterFields.getOrDefault("projectCustomerId", IsOptionalFieldValueFilled ? "CT" : ""));
        replaceKeys.put("projectCustomerName", pmsMasterFields.getOrDefault("projectCustomerName", IsOptionalFieldValueFilled ? "CTS" : ""));
        replaceKeys.put("domain", pmsMasterFields.getOrDefault("domain", IsOptionalFieldValueFilled ? "Healthcare" : ""));
        replaceKeys.put("location", pmsMasterFields.getOrDefault("location", IsOptionalFieldValueFilled ? "Pune SEZ" : ""));
        replaceKeys.put("image", pmsMasterFields.getOrDefault("image", IsOptionalFieldValueFilled ? "" : ""));
        replaceKeys.put("qmsProjectID", pmsMasterFields.getOrDefault("qmsProjectID", IsOptionalFieldValueFilled ? "IOTCT-18-June-2024" : ""));
        replaceKeys.put("qmsStatus", pmsMasterFields.getOrDefault("qmsStatus", IsOptionalFieldValueFilled ? "Active" : ""));
        replaceKeys.put("qmsDescription", pmsMasterFields.getOrDefault("qmsDescription", IsOptionalFieldValueFilled ? "Implementation of client product to convert ICD 9 based hospitals to ICD 10" : ""));
        replaceKeys.put("qmsProjStartDate", pmsMasterFields.getOrDefault("qmsProjStartDate", IsOptionalFieldValueFilled ? "2020-09-07T00:00:00" : ""));
        replaceKeys.put("qmsProjEndDate", pmsMasterFields.getOrDefault("qmsProjEndDate", IsOptionalFieldValueFilled ? "2029-09-07T00:00:00" : ""));
        replaceKeys.put("subProjectStatus", pmsMasterFields.getOrDefault("subProjectStatus", IsOptionalFieldValueFilled ? "Active" : ""));
        replaceKeys.put("subProjectSecondaryFCOwnerName", pmsMasterFields.getOrDefault("Sub_Project_Secondary_FC_owner_name", IsOptionalFieldValueFilled ? "C" : ""));
        replaceKeys.put("subProjectSecondaryFCOwnerCTID", pmsMasterFields.getOrDefault("Sub_Project_Secondary_FC_owner_CTID", IsOptionalFieldValueFilled ? "CT0253" : ""));
        replaceKeys.put("subProjectSecondaryFCOwnerEmpID", pmsMasterFields.getOrDefault("subProjectSecondaryFCOwnerEmpID", IsOptionalFieldValueFilled ? "VaishaliD" : ""));
        replaceKeys.put("pgsProjectID", pmsMasterFields.getOrDefault("pgsProjectID", IsOptionalFieldValueFilled ? "IOTCT-18-June-2024" : ""));
        replaceKeys.put("subProjectFinanceCodeSK", pmsMasterFields.getOrDefault("subProjectFinanceCodeSK", IsOptionalFieldValueFilled ? "CODE01" : ""));
        replaceKeys.put("subProjectFinanceCodeRole", pmsMasterFields.getOrDefault("subProjectFinanceCodeRole", IsOptionalFieldValueFilled ? "Finance Lead" : ""));
        replaceKeys.put("pgsProjectName", pmsMasterFields.getOrDefault("pgsProjectName", IsOptionalFieldValueFilled ? "IOT CloudA03-18-June-2024" : ""));
        replaceKeys.put("subProjectID", pmsMasterFields.getOrDefault("subProjectID", IsOptionalFieldValueFilled ? "IOTCT-18-June-2024" : ""));
        replaceKeys.put("subProjectBillType", pmsMasterFields.getOrDefault("subProjectBillType", IsOptionalFieldValueFilled ? "" : ""));
        replaceKeys.put("subProjectType", pmsMasterFields.getOrDefault("subProjectType", IsOptionalFieldValueFilled ? "Internal" : ""));
        replaceKeys.put("subProjectVertical", pmsMasterFields.getOrDefault("subProjectVertical", IsOptionalFieldValueFilled ? "Digital" : ""));
        replaceKeys.put("subProjectSubVertical", pmsMasterFields.getOrDefault("subProjectSubVertical", IsOptionalFieldValueFilled ? "Sub-Digital" : ""));
        replaceKeys.put("subProjectLegalEntity", pmsMasterFields.getOrDefault("subProjectLegalEntity", IsOptionalFieldValueFilled ? "" : ""));
        replaceKeys.put("qmsProjType", pmsMasterFields.getOrDefault("qmsProjType", IsOptionalFieldValueFilled ? "Internal" : ""));
        replaceKeys.put("engagementMaturityModel", pmsMasterFields.getOrDefault("engagementMaturityModel", IsOptionalFieldValueFilled ? "" : ""));

        PayloadBuilder.getResolvedDefinition(createProjectMasterDefinition, replaceKeys);
        Response response = APIExecutor.execute(userContext, createProjectMasterDefinition, iTestContext);
        return response;
    }
    public void assignProjectToUser(ITestContext iTestContext,SMSConfigService smsConfigService,UserContext userContext) {
        ProjectService projectService = new ProjectService();
        Map<String, Object> replaceKeys = new HashMap<>();
        String randomNumber= Faker.instance().idNumber().valid();
        replaceKeys.put("projectCode", smsConfigService.getRandomProjectIDNotAssociateToUser(userContext));
        replaceKeys.put("pfRecordId", randomNumber);
        Response response = projectService.assignProject(smsConfigService, userContext, iTestContext, replaceKeys);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "Project Allocation API - Assigning Project to a User is not working" + response.getBody().prettyPrint());
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].pfRecordId"), randomNumber, "Project Allocation API - Assigning Project to a User is not working" + response.getBody().prettyPrint());
        iTestContext.setAttribute("replaceKeys", replaceKeys);
    }
    public void removeAssignedProjectTest(ITestContext iTestContext,SMSConfigService smsConfigService,UserContext userContext, Map<String, Object> replaceKeys) {
        replaceKeys.put("actionPerformed", "Deleted");
        ProjectService projectService = new ProjectService();
        Response response = projectService.assignProject(smsConfigService, userContext, iTestContext, replaceKeys);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "Project Allocation API - Removing Project to a User is not working" + response.getBody().prettyPrint());
    }

}