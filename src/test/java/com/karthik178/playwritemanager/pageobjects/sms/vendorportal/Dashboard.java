package com.karthik178.playwritemanager.pageobjects.sms.vendorportal;

import com.google.common.util.concurrent.Uninterruptibles;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.configservice.sms.SMSConfigService;
import com.karthik178.configservice.sms.UserService;
import com.karthik178.playwritemanager.pageobjects.sms.common.BaseModel;
import com.karthik178.playwritemanager.utils.Browserutils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Dashboard extends BasePlaywriteTest {
    private static final Logger logger = LogManager.getLogger(Dashboard.class);

    public Page page;
    private Browserutils browserutils;
    private Locator searchBar;
    private Locator searchResultsCount;


    public Dashboard(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.searchBar = page.locator("input[type='search']");
        this.searchResultsCount = page.locator("dataTable_info");
    }

    @Step("Verify Search Orders is Working")
    public void verifySearchOrdersIsWorking(String searchText,int expectedCount) {
        browserutils.fillTextBox(logger,searchBar,searchText);
        int actualCount = getSearchResultsCount();
        Assert.assertEquals(actualCount,expectedCount);
    }
    public int getSearchResultsCount(){
        String resultsText = browserutils.extractText(logger,searchResultsCount);
        String[] arr = resultsText.split(" ");
        // if we do not have 5th item, it would throw exception.
        // that's fine. we would want our test to fail anyway in that case!
        int count = Integer.parseInt(arr[5]);
        LogHandler.logInfo(String.format("Results %s",count));
        return count;
    }

    }
