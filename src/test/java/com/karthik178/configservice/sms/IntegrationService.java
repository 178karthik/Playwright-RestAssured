package com.karthik178.configservice.sms;

import com.google.common.util.concurrent.Uninterruptibles;
import com.karthik178.apimanager.enums.ErrorMessage;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.common.BaseRestTest;
import com.karthik178.exceptionhandler.NoIntegrationLogsFoundException;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents IntegrationService
 * @author Karthik T
 */
public class IntegrationService extends BaseRestTest {

    private static final Logger logger = LogManager.getLogger(IntegrationService.class);

    public static Response getIntegrationLogs(ITestContext iTestContext, UserContext userContext, String searchKey) throws NoIntegrationLogsFoundException {
        return getIntegrationLogsPolling(iTestContext, userContext, searchKey, "outbound");
    }

    public static Response getInboundIntegrationLogs(ITestContext iTestContext, UserContext userContext, String searchKey) throws NoIntegrationLogsFoundException {
        return getIntegrationLogsPolling(iTestContext, userContext, searchKey, "inbound");
    }

    private static Response getIntegrationLogsResponse(ITestContext iTestContext, UserContext userContext, String searchKey) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/search.integration.logs.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("logType", "outbound");
        replaceKeys.put("searchQuery", searchKey);
        String fromDate=getUTCFormattedTime()+"18:30:00.000Z";
        replaceKeys.put("fromDt", fromDate);
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef, iTestContext);
        return response;
    }

    private static Response getInboundIntegrationLogsResponse(ITestContext iTestContext, UserContext userContext, String searchKey) {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/search.integration.logs.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("logType", "inbound");
        replaceKeys.put("searchQuery", searchKey);
        String fromDate=getUTCFormattedTime()+"18:30:00.000Z";
        replaceKeys.put("fromDt", fromDate);
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef, iTestContext);
        return response;
    }

    private static Response getIntegrationLogsPolling(ITestContext iTestContext, UserContext userContext, String searchKey, String type) throws NoIntegrationLogsFoundException {
        Response response;
        int MAX_RETRIES = 9;
        boolean result;
        do {
            MAX_RETRIES--;
            result = retry(MAX_RETRIES, iTestContext, userContext, searchKey, type);
            if (result == true) {
                response = Objects.equals(type.toLowerCase(), "inbound") ? getInboundIntegrationLogsResponse(iTestContext, userContext, searchKey) : getIntegrationLogsResponse(iTestContext, userContext, searchKey);
                return response;
            }
        } while (result == false && MAX_RETRIES > 0);
        Assert.assertTrue(result, "No logs found");
        throw new NoIntegrationLogsFoundException("Integration log not found for " + searchKey);
    }

    @Step("Polling for Integration log response")
    private static boolean retry(int MAX_RETRIES, ITestContext iTestContext, UserContext userContext, String searchKey, String type) {
        Response response = Objects.equals(type.toLowerCase(), "inbound") ? getInboundIntegrationLogsResponse(iTestContext, userContext, searchKey) : getIntegrationLogsResponse(iTestContext, userContext, searchKey);
        System.out.println(response.jsonPath().get("logs").toString());
        Assert.assertEquals(response.getStatusCode(), 200, "API request is failed");
        Assert.assertEquals(response.jsonPath().get("status"), "Success", ErrorMessage.API_FAILURE);
        if (response.jsonPath().getInt("cnt") > 0) {
            System.out.println(response.jsonPath().prettify());
            logger.info(String.format("Integration logs found for %s after %d attemps", searchKey, 10 - MAX_RETRIES));
            return true;
        }
        logger.info("Integration log not yet found for " + searchKey);
        Uninterruptibles.sleepUninterruptibly(1500, TimeUnit.MICROSECONDS);
        return false;
    }

    public Response searchLogsAndVerifyMoreFilter(ITestContext context, String searchValue, UserContext userContext) throws InterruptedException {
        RestRequestDefinition saveClientDef = PayloadBuilder.mapJsonToRestDefinition("sms/integrations/webhooks/search.integration.filters.logs.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("clientId", SMSConfigService.getClientId(userContext));
        replaceKeys.put("logType", "outbound");
        replaceKeys.put("searchQuery", searchValue);
        replaceKeys.put("fromDate", "");
        PayloadBuilder.getResolvedDefinition(saveClientDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, saveClientDef, context);
        System.out.println(response.jsonPath().get("logs").toString());
        Assert.assertEquals(response.getStatusCode(), 200, "API request is failed");
        Assert.assertEquals(response.jsonPath().get("status"), "Success", ErrorMessage.API_FAILURE);
        Assert.assertTrue(response.getBody().jsonPath().getInt("cnt") > 0, "No logs found");
        return response;
    }
}
