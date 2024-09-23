package com.karthik178.playwritemanager.pageobjects.sms.common;

import com.google.common.util.concurrent.Uninterruptibles;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class BaseModel  extends BasePlaywriteTest {

    private static final Logger logger = LogManager.getLogger(BaseModel.class);

    public Page page;
    public Locator root;

    public BaseModel(Page page) {
        this.page = page;
        this.root = page.locator("mat-dialog-container");
        this.browserutils = new Browserutils();
    }

    @Step("Popup Model :: Verify Model is displayed")
    public void verifyModelIsPresent(String message) {
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        browserutils.isVisible(logger, root, 20000);
    }

    @Step("Popup Model :: Verify Title exist")
    public void verifyTitleExist(String title, String message) {
        Locator locator = page.getByText(title);
        browserutils.isVisible(logger, locator, message,180000);
    }

    @Step("Popup Model ::Verify button exist")
    public void verifyButtonExistOnModel(String buttonName) {
        browserutils.getButtonByName(page, buttonName);
    }

    @Step("Popup Modal :: Verify button exist with timeout")
    public void verifyButtonExistOnModel(String buttonName, int timeout) {
        browserutils.getButtonByName(page, buttonName, timeout);
    }

    @Step("Popup Model :: Click On Button")
    public void clickOnButton(String buttonName, String description) {
        Locator locator = browserutils.getButtonByName(page, buttonName);
        browserutils.clickOn(logger, locator, description);
        browserutils.waitForPageLoading(page);
    }
    @Step("Click On Button")
    public void clickOnButton(String buttonName,int index) {
        List<Locator> allButtons = page.locator("//span[normalize-space()='"+buttonName+"']").all();
        Locator button = allButtons.get(index);
        button.evaluate("element=>element.click()");
        browserutils.waitForPageLoading(page);
    }
    @Step("Popup Model ::Verify button exist")
    public boolean verifyButtonExist(String buttonName, int timeout) {
        Locator locator = page.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(buttonName));
        boolean flag = browserutils.waitForLocator(locator,timeout);
        return flag;
    }

}
