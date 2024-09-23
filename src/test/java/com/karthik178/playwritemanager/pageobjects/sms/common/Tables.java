package com.karthik178.playwritemanager.pageobjects.sms.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Tables extends BasePlaywriteTest {

    private Page page;
    private Browserutils browserutils;

    private static final Logger logger = LogManager.getLogger(Tables.class);
    private Locator columnHeader;

    private Locator allInsideTabsUnderAdmin;
    public Tables(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.columnHeader = page.locator("th[role='columnheader']");
    }

    @Step("Verify Given Column Exists in Table")
    public boolean verifyGivenColumnExistsInTable(String columnName) {
        boolean flag = false;
        List<Locator> allColumnHeaders = columnHeader.all();
        for(Locator columnHeader : allColumnHeaders)
        {
            String columnHeaderName = browserutils.extractText(logger,columnHeader).trim();
            if(columnHeaderName.contains(columnName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
