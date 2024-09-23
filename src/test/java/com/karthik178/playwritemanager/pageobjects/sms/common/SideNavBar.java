package com.karthik178.playwritemanager.pageobjects.sms.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SideNavBar extends BasePlaywriteTest {

    private Page page;
    private Browserutils browserutils;

    private static final Logger logger = LogManager.getLogger(SideNavBar.class);

    private Locator root;
    public SideNavBar(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.root = page.getByRole(AriaRole.NAVIGATION);
    }


    @Step("Click Link on Side navbar")
    public void clickOnSideNaveBarTab(String tabName) {
        Locator link = root.locator(page.locator("a .item-name")).filter(new Locator.FilterOptions().setHasText(tabName));
        browserutils.heavyClick(logger,page,  link, "Click on " + tabName + "In side navbar", 60000);
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).isVisible()){
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).click();
        }

    }

    @Step("Click Link on Side navbar")
    public void clickOnSideNaveBarTab(String tabName, int extraWaitTimeInSeconds) {
        Locator link = root.locator(page.locator("a .item-name")).filter(new Locator.FilterOptions().setHasText(tabName));
        browserutils.heavyClick(logger,page,  link, "Click on " + tabName + "In side navbar",60000 + extraWaitTimeInSeconds*1000);
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).isVisible()){
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).click();
        }

    }

    @Step("Click Link on Side navbar")
    public void clickOnSideNaveBarTab(String tabName, boolean waitForSkipButton) {
        Locator link = root.locator(page.locator("a .item-name")).filter(new Locator.FilterOptions().setHasText(tabName));
        browserutils.heavyClick(logger,page,  link, "Click on " + tabName + "In side navbar", 60000);
        if (waitForSkipButton) {
            if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).isVisible()){
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).click();
            }
        }
    }

    public void clickOnSideNaveBarTab(String tabName, String visibilityCheck) {
        Locator link = root.locator(page.locator("a .item-name")).filter(new Locator.FilterOptions().setHasText(tabName));
        browserutils.heavyClick(logger,page, link, "Click on " + tabName + "In side navbar", 60000);
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).isVisible()){
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SKIP")).click();
        }
        Locator locator = page.getByText(visibilityCheck);
        browserutils.isVisible(logger, locator, 60000);
    }

    public void clickOnLogout() {
        Locator link = root.locator(page.locator("a .item-name")).filter(new Locator.FilterOptions().setHasText("Logout"));
        browserutils.clickOn(logger, link, "click on logout button");
    }
    public void clickOnSideNavBarTabWhichHasCommonNames(String tabName) {
       Locator locator = page.locator("//span[normalize-space()='"+tabName+"']");
       browserutils.heavyClick(logger,page,locator,String.format("Click On  %s Tab",tabName));

    }
    @Step("Verify Given Tab Present In SideNavBar")
    public boolean verifyGivenTabPresentInSideNavBar(String tabName) {
        boolean flag = false;
        List<Locator> allSideNavBarTabNames = page.locator(".side-nav .item-name").all();
        for(Locator sideNavBarTabName: allSideNavBarTabNames)
        {
            String actualAliasName = browserutils.extractText(logger,sideNavBarTabName).trim();
            if (actualAliasName.equalsIgnoreCase(tabName))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }



}
