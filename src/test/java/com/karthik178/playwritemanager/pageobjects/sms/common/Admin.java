package com.karthik178.playwritemanager.pageobjects.sms.common;

import com.google.common.util.concurrent.Uninterruptibles;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Admin extends BasePlaywriteTest {

    private Page page;
    private Browserutils browserutils;

    private static final Logger logger = LogManager.getLogger(Admin.class);
    private Locator totalSkillsFoundInSkillMaster;
    
    private Locator allInsideTabsUnderAdmin;
    public Admin(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.allInsideTabsUnderAdmin = page.locator(".mat-tab-label .mat-tab-label-content");
        this.totalSkillsFoundInSkillMaster = page.locator(".con .bottom-con .caption");
    }


    /* Click On Any Tabs available Inside Admin,
    * e.g: 1) Under SkillGroUp, You can click tabs like: Specializations,Location Cluters,Services
    * e.g:2) Under Integrations ,You can click tabs like:API Configuration,Webhooks,Uploads
    * */
    @Step("Click On Any Inisde Tabs  Available Under Admin")
    public void clickOnTargetTab(String tabName) {

        List<Locator> allTabs = allInsideTabsUnderAdmin.all();
        for(Locator eachTab: allTabs)
        {
            String tabCompleteText =  browserutils.extractText(logger,eachTab);
            String fullTabName[] = tabCompleteText.split("\n");
            String onlyTabName = fullTabName[1].trim();
            if(tabName.equalsIgnoreCase(onlyTabName))
            {
                browserutils.clickOn(logger, eachTab, "Click On Tab name");
                break;
            }
        }

    }

    @Step("Click On Icon Button Based On Tooltip")
    public void clickOnIconButtonBasedToolTip(String toolTipName) {

        List<Locator> cloudIcons = page.locator(".search-icons .mat-icon").all();

        for(Locator eachCloudIcon : cloudIcons) {
            browserutils.hoverOn(logger, eachCloudIcon);
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            String currentTooltip = browserutils.extractText(logger, page.locator("mat-tooltip-component"));
            if (currentTooltip.equalsIgnoreCase(toolTipName)) {
                browserutils.clickOn(logger,eachCloudIcon, "Click on " + toolTipName );
                break;
            }
        }


    }

    @Step("Click On Icon/Image Based On underLying Text in Locator")
    public void clickOnIconBasedOnUnderLyingTextInLocator(String text)
    {
        List<Locator> allIcons=page.locator("mat-icon[role=img]").all();
        for(Locator targetlocator: allIcons)
        {
            String extractedIconText = browserutils.extractText(logger,targetlocator).trim();
            if(extractedIconText.equalsIgnoreCase(text))
            {
                browserutils.clickOn(logger,targetlocator,"Click On Given Icon");
            }
        }
    }
    public int getIndexOfColumnHeader(String columnName) {
        List<Locator> headerCells = page.getByRole(AriaRole.COLUMNHEADER).all();
        int i=0;
        for(Locator locator : headerCells) {
            String extractedColumnName = browserutils.extractText(locator);
            if (extractedColumnName.equalsIgnoreCase(columnName)) {
                LogHandler.logInfo(logger, String.format("Found %s column at index %d", columnName, i));
                return i;
            }
            i++;
        }
        throw new RuntimeException(String.format("No column found with name %s", columnName));
    }
    public String getCellValue(int rowIndex, int columnIndex) {
        Locator targetRow = page.locator("tbody").locator(page.locator(".element-row ")).nth(rowIndex);
        Locator targetCell = targetRow.locator(page.getByRole(AriaRole.CELL).nth(columnIndex));
        return browserutils.extractText(logger, targetCell);
    }
    @Step("Get Total Skills Found From SkillMaster")
    public int getTotalSkillsFoundFromSkillMaster() {

        String coursesFoundText = browserutils.extractText(logger,totalSkillsFoundInSkillMaster.first()).trim();
        String coursesFoundTextSplit[] =coursesFoundText.split("\s");
        String coursesCountText = coursesFoundTextSplit[5];
        int coursesCount = Integer.parseInt(coursesCountText);
        return coursesCount;
    }

}
