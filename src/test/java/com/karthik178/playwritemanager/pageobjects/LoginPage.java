package com.karthik178.playwritemanager.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.configservice.common.ConfigHelper;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginPage extends BasePlaywriteTest {


    private String emailPlaceHolder = "Enter your business mail";
    private String passwordPlaceHolder = "Your Password";

    private Browserutils browserutils;

    private Page page;

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private Locator emailLocator;
    private Locator passwordLocator;
    public LoginPage(Page page) {
        this.browserutils = new Browserutils();
        this.page = page;
        this.emailLocator = page.getByPlaceholder("Enter your business mail").or(page.getByPlaceholder("Enter your email"));
        this.passwordLocator= page.getByPlaceholder("Your Password");
    }

    @Step("Navigate to Login Page")
    public void navigateToLoginPage(String baseurl) {
        page.context().clearCookies();
        browserutils.navigateToPage(logger, page, baseurl);
    }

    @Step("Set Username in login Page")
    public void setUserName(String userName){
        browserutils.fillTextBox(logger,emailLocator, userName, 15);
    }

    @Step("Set Password in login Page")
    public void setPassword(String password){
        browserutils.fillTextBox(logger, passwordLocator, password);
    }

    @Step("Click on sign in button")
    public void clickOnSubmitButton(){
        browserutils.heavyClick(logger, page, page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")), "Sign In");
    }

    @Step("Login {baseurl} :: {userName} :: {password}")
    public void Login(Page page,String baseurl,String userName,String password)
    {
        browserutils.navigateToPage(logger, page, baseurl);
        browserutils.fillTextBox(logger,emailLocator, userName);
        browserutils.fillTextBox(logger,passwordLocator, password);
        browserutils.heavyClick(logger, page, page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")), "Sign In");
        browserutils.waitForPageLoading(page);
    }

    @Step("Login {baseurl} :: {userName} :: {password}")
    public void Login(Page page,String baseurl,String userName,String password, String waitForText)
    {
        browserutils.navigateToPage(logger, page, baseurl);
        browserutils.fillTextBox(logger,emailLocator, userName);
        browserutils.fillTextBox(logger,passwordLocator, password);
        browserutils.clickOn(logger, page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")), "Sign In");
        Locator locator = page.getByText(waitForText);
        browserutils.isVisible(logger, locator, 60000);
    }

    @Step("Do Login")
    public void Login(Page page, String clientKey)
    {
        Secret secret = ConfigHelper.getSecret(clientKey);
        Login(page, secret.getUrl(), secret.getEmail(), secret.getPassword());
    }
}
