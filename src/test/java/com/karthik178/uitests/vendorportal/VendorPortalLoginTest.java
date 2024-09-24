package com.karthik178.uitests.vendorportal;

import com.karthik178.configservice.common.BasePlaywriteTest;
import com.microsoft.playwright.Page;
import com.karthik178.playwritemanager.pageobjects.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Features;
import io.qameta.allure.Story;
import org.testng.ITestContext;
import org.testng.annotations.Test;
@Epic("Vendorportal")
@Features({@Feature("Login")})
@Story("Verify Login is Successful")
public class VendorPortalLoginTest extends BasePlaywriteTest {
    private static String clientKey = "mike.admin";
    public VendorPortalLoginTest() {
        super(clientKey);
    }
    @Test(description = "Verify Login is Successful")
    public void verifyLoginIsSuccessful(ITestContext iTestContext) {
        Page page = browserutils.getPageSession(iTestContext);
        LoginPage loginPage = new LoginPage(page);
        loginPage.Login(page,clientKey,"Dashboard");

    }
}
