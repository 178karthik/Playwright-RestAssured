package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;

import static com.karthik178.configservice.sms.SMSConfigService.getClientId;

public class AccessRolesService {

    private static final Logger logger = LogManager.getLogger(AccessRolesService.class);


    public void updateAccessRoleWithUsersFilterForVirtusa(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-virtusa.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }
    public void updateAccessRoleWithUsersFilterForCitiusTech(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-citiustech.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }
    public void updateAccessRoleWithUsersFilterForCognizant(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-cognizant.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }
    public void updateAccessRoleWithUsersFilterForBrillio(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-brillio.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }
    public void updateAccessRoleWithUsersFilterForPersistent(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-persistent.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }
    public void updateAccessRoleWithUsersFilterForBirlasoft(UserContext userContext, ITestContext iTestContext,String clientKey, String usersFilter) {
        RestRequestDefinition saveRoleDef = PayloadBuilder.mapJsonToRestDefinition("sms/roles/save-role-birlasoft.json");

        SMSConfigService smsConfigService = new SMSConfigService(clientKey);
        String managerEmailId = smsConfigService.getUserEmailIdFromSecretFile(iTestContext,clientKey);
        /* Call getUserDetailsAPI */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, managerEmailId);
        /* Get RoleName From getUsersDetails API */
        String roleName = String.valueOf(userDetails.get("roleName"));
        /* Get RoleId From getUsersDetails API*/
        String roleId = String.valueOf(userDetails.get("roleId"));
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", getClientId(userContext));
        replaceKeys.put("roleName",roleName);
        replaceKeys.put("roleId",roleId);
        replaceKeys.put("usersFilter",usersFilter);
        PayloadBuilder.getResolvedDefinition(saveRoleDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveRoleDef);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Save Role API is not working");
    }

}
