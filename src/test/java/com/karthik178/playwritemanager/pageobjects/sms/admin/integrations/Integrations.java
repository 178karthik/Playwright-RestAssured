package com.karthik178.playwritemanager.pageobjects.sms.admin.integrations;

import com.google.common.util.concurrent.Uninterruptibles;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.playwritemanager.pageobjects.sms.common.Admin;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Integrations {


    private static final Logger logger = LogManager.getLogger(Integrations.class);

    public Page page;

    private Browserutils browserutils;
    private Locator allColumnHeaders;
    private Locator allRows;


    public Integrations(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.allColumnHeaders=page.locator("th[role='columnheader']");
        this.allRows=page.locator("tr");
    }


    public int getIndexOfColumnHeader(String columnName) {
        Map<Integer, String> map = new HashMap<>();
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

    public int getRowIndexBasedOnColumnNameAndCellValue(String columnName, String cellValue) {
        int fileNameColumIndex = getIndexOfColumnHeader("File Name");
        Locator allRows = page.locator("tbody").locator(page.getByRole(AriaRole.ROW));
        int i=0;
        for(Locator row : allRows.all()) {
            Locator fileNameLocator = row.locator(page.getByRole(AriaRole.CELL)).nth(fileNameColumIndex);
            String extractedCellText = browserutils.extractText(logger, fileNameLocator);
            if (extractedCellText.equalsIgnoreCase(cellValue)) {
                LogHandler.logInfo(logger, String.format("Found %s filename row at index %d", extractedCellText, i));
                return i;
            }
            i++;
        }
        throw new RuntimeException(String.format("%s not present in column %s",cellValue, columnName));
    }


    public String getCellValue(int rowIndex, int columnIndex) {
        Locator targetRow = page.locator("tbody").locator(page.getByRole(AriaRole.ROW)).nth(rowIndex);
        Locator targetCell = targetRow.locator(page.getByRole(AriaRole.CELL).nth(columnIndex));
        return browserutils.extractText(logger, targetCell);
    }

    public void refreshIntegrationsPageAndNavigateToUploads() {
        page.reload();
        Admin admin = new Admin(page);
        browserutils.waitForPageLoading(page);
        admin.clickOnTargetTab("Uploads");
        browserutils.waitForLocator(page.getByText("Integration Type"), 15);
    }

    public String getUploadStatusBasedOnFileName(String fileName) {
        fileName = Paths.get(fileName).getFileName().toString();
        String uploadStatus = "";
        int statusColumnIndex = getIndexOfColumnHeader("Status");
        int fileNameRowIndex = getRowIndexBasedOnColumnNameAndCellValue("File Name", fileName);
        int maxRetries = 20;
        do {
            uploadStatus = getCellValue(fileNameRowIndex, statusColumnIndex);
            System.out.println("Status value :: " + uploadStatus);
            if (uploadStatus.equalsIgnoreCase("completed")) {
                return uploadStatus;
            }
            if (uploadStatus.equalsIgnoreCase("errors")) {
               break;
            }
            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
            refreshIntegrationsPageAndNavigateToUploads();
            maxRetries--;
        } while (maxRetries > 0);
        return uploadStatus;
    }

    @Step("Get Upload Status with filename")
    public String getUploadStatusBasedOnFileName(String fileName, String columnName) {
        fileName = Paths.get(fileName).getFileName().toString();
        String uploadStatus="";
        int fileNameColumIndex=getIndexOfColumnHeader(columnName);
        List<Locator> allRows = this.allRows.all();
        int rowIndex=1;
        for(int i=1; i< allRows.size(); i++)
        {
            String fileNameLocatorString="tr:nth-child("+i+") td:nth-child("+fileNameColumIndex+")";
            Locator fileNameLocator=page.locator(fileNameLocatorString);
            String fileNameInEachRow= browserutils.extractText(logger,fileNameLocator).trim();
            if(fileNameInEachRow.equalsIgnoreCase(fileName)){
                int indexOfUploadStatusColumn=getIndexOfColumnHeader("Status");
                String uploadStatusLocatorString="tr:nth-child("+rowIndex+") td:nth-child("+indexOfUploadStatusColumn+")";
                Locator uploadStatusLocator=page.locator(uploadStatusLocatorString);
                uploadStatus= browserutils.extractText(logger, uploadStatusLocator).trim();
                int maxRetries=0;
                while(uploadStatus.equalsIgnoreCase("Pending") && maxRetries<=10)
                {
                 page.reload();
                 Admin admin = new Admin(page);
                 browserutils.waitForPageLoading(page);
                 admin.clickOnTargetTab("Uploads");
                 browserutils.waitForPageLoading(page);
                 System.out.println("Refreshing...");
                 uploadStatus= browserutils.extractText(logger, uploadStatusLocator).trim();
                 maxRetries++;
                 if(!(uploadStatus.equalsIgnoreCase("Pending")))
                 {
                     break;
                 }
                }
                break;
            }
            rowIndex++;
        }
        System.out.println("The Upload status is"+uploadStatus);
        return uploadStatus;

    }



}




