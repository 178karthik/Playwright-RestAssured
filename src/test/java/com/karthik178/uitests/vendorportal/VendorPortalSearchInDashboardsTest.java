package com.karthik178.uitests.vendorportal;

import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.pageobjects.LoginPage;
import com.karthik178.playwritemanager.pageobjects.sms.vendorportal.Dashboard;
import com.microsoft.playwright.Page;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Features;
import io.qameta.allure.Story;
import org.testng.ITestContext;
import org.testng.annotations.Test;

@Epic("Vendorportal")
@Features({@Feature("Dashboards")})
@Story("Vendor Portal Search In Dashboards")
public class VendorPortalSearchInDashboardsTest extends BasePlaywriteTest {
    private static String clientKey = "mike.admin";
    public VendorPortalSearchInDashboardsTest() {
        super(clientKey);
    }
    @Test(description = "Verify Login is Successful")
    public void verifyLoginIsSuccessful(ITestContext iTestContext) {
        Page page = browserutils.getPageSession(iTestContext);
        LoginPage loginPage = new LoginPage(page);
        loginPage.Login(page,clientKey,"Dashboard");
        Dashboard dashboard = new Dashboard(page);
        dashboard.verifySearchOrdersIsWorking("miami",10);

    }
}
