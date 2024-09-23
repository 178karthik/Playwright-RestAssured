package com.karthik178.playwritemanager.utils;


import com.google.common.util.concurrent.Uninterruptibles;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.karthik178.apimanager.enums.Timeouts;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.playwritemanager.pageobjects.sms.common.BaseModel;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class Browserutils extends BasePlaywriteTest {


    private static final Logger logger = LogManager.getLogger(Browserutils.class);

    @Step("Initialize Playwright Session")
    public Page getPageSession(ITestContext iTestContext)  {
        try {

            Playwright playwright = Playwright.create();

            ArrayList<String> arguments=new ArrayList<>();
            arguments.add("--start-maximized");

            LogHandler.logInfo(logger, mode.toString());
            Browser browser;
            switch(mode.getBrowser().toLowerCase()) {
                case "firefox":
                    browser =  playwright.firefox().launch(new BrowserType.LaunchOptions()
                            .setHeadless(mode.isHeadless())
                            .setSlowMo(mode.getSlowMotion()));
                    break;
                default:
                    browser =  playwright.chromium().launch(new BrowserType.LaunchOptions()
                            .setHeadless(mode.isHeadless())
                            .setSlowMo(mode.getSlowMotion())
                            .setArgs(arguments));
            }
            BrowserContext context;
            if (mode.isEnableVideosForFailureTests()) {
                context = browser.newContext(new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get("target/videos")).setViewportSize(null));
            } else {
                context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
            }
            Page page = context.newPage();
            page.setDefaultTimeout(mode.getDefaultTimeout()*1000);
            iTestContext.setAttribute("page", page);
            LogHandler.logInfo(logger, "Session launch success");
            return page;
        }catch (Error error){
            logger.error("Session launch failure");
        }
        throw new RuntimeException("Failure");

    }


    public void navigateToPage(Logger logger,Page page, String url) {
        try {
            page.navigate(url);
            waitForPageLoading(page);
            LogHandler.logInfo(logger, String.format("Navigate to url : %s is success", url));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Navigate to url : %s is failure, Error ",url, error));
        }

    }


    public void fillTextBox(Logger logger, Locator locator, String value) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            locator.fill(value);
            LogHandler.logInfo(logger, String.format("%s :: fill '%s'", locator, value));
        }catch (Error error){
            Assert.assertTrue(false, String.format("%s :: fill '%s' :: %s", locator, value, error));
        }

    }

    public void fillTextBox(Logger logger, Locator locator, String value, int timeout) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            locator.fill(value);
            LogHandler.logInfo(logger, String.format("%s :: fill '%s'", locator, value));
        }catch (Error error){
            Assert.assertTrue(false, String.format("%s :: fill '%s' :: %s", locator, value, error));
        }

    }

    public boolean isVisible(Logger logger, Locator locator, String message) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
        boolean isLocatorVisible = locator.isVisible();
        if (isLocatorVisible) {
            LogHandler.logInfo(logger, String.format("[%s] is visible", locator.toString().replace("Locator@", "")));
            return  isLocatorVisible;
        }
        LogHandler.logError(logger, String.format("%s is not visible", locator));
        Assert.assertTrue(false ,String.format("%s \nLocator :: %s is not visibile", message, locator));
        throw new RuntimeException(String.format("%s \nLocator :: %s is not visibile", message, locator));
    }
    public boolean isVisible(Logger logger, Locator locator, String message, int timeout) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        boolean isLocatorVisible = locator.isVisible();
        if (isLocatorVisible) {
            LogHandler.logInfo(logger, String.format("[%s] is visible", locator.toString().replace("Locator@", "")));
            return  isLocatorVisible;
        }
        LogHandler.logError(logger, String.format("%s is not visible", locator));
        Assert.assertTrue(false ,String.format("%s \nLocator :: %s is not visibile", message, locator));
        throw new RuntimeException(String.format("%s \nLocator :: %s is not visibile", message, locator));
    }

    public int getLocatorCount(Logger logger, Locator locator) {
        Uninterruptibles.sleepUninterruptibly(2,TimeUnit.SECONDS);
        int locatorCount = locator.count();
        LogHandler.logError(logger, String.format("Locator : %s , Count : %d", locator, locatorCount));
        return locatorCount;

    }

    public boolean isVisible(Logger logger, Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
        boolean isLocatorVisible = locator.isVisible();
        if (isLocatorVisible) {
            LogHandler.logInfo(logger, String.format("[%s] is visible", locator.toString().replace("Locator@", "")));
            return  isLocatorVisible;
        }
        LogHandler.logError(logger, String.format("%s is not visible", locator));
        Assert.assertTrue(false, String.format("%s is not visible", locator));
        throw new RuntimeException(String.format("%s is not visible", locator));
    }

    public boolean isVisible(Logger logger, Locator locator, int timeout) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        boolean isLocatorVisible = locator.isVisible();
        if (isLocatorVisible) {
            LogHandler.logInfo(logger, String.format("%s is visible", locator));
            return  isLocatorVisible;
        }
        LogHandler.logError(logger, String.format("%s is not visible", locator));
        Assert.assertTrue(false, String.format("%s is not visible", locator));
        throw new RuntimeException(String.format("%s is not visible", locator));
    }

    public void clickOn(Logger logger, Locator locator, String description) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Large.getValue()));
            locator.click();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: click on %s is success", locator, description));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }

    public void clickOn(Logger logger, Locator locator, String description, int timeout) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            locator.click();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: click on %s", locator, description));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }

    public void heavyClick(Logger logger, Page page, Locator locator, String description, int timeout) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            locator.click();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: click on %s", locator, description));
            waitForPageLoading(page);
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }

    public void heavyClick(Logger logger, Page page, Locator locator, String description) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Large.getValue()));
            locator.click();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: click on %s", locator, description));
            waitForPageLoading(page);
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }


    public void fillIn(Logger logger, Locator locator, String value, String description) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            locator.fill(value);
            LogHandler.logInfo(logger, String.format("Locator :: %s :: entered text %s in %s", locator, value, description));
        }catch (Error error){
            LogHandler.logError(logger, String.format("FAILED :: Locator :: %s :: enter text %s in %s \nerror",
                    locator, value, description, error));
        }

    }

    public void fillIn(Logger logger, Locator locator, String value, String description, int timeout) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            locator.fill(value);
            LogHandler.logInfo(logger, String.format("Locator :: %s :: click on %s", locator, description));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }

    public void clearAndfillIn(Logger logger, Locator locator, String value, String description) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            locator.clear();
            locator.fill(value);
            LogHandler.logInfo(logger, String.format("Locator :: %s :: entered text %s in %s", locator, value, description));
        }catch (Error error){
            LogHandler.logError(logger, String.format("FAILED :: Locator :: %s :: enter text %s in %s \nerror",
                    locator, value, description, error));
        }

    }

    public String getInputValue(Logger logger, Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            String val = locator.inputValue();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: Retrieve input value %s", locator, val));
            return val;
        }catch (Error error){
            LogHandler.logInfo(logger, String.format("Locator :: %s :: Trying to Retrieve input value", locator));
            throw new RuntimeException(String.format("Locator :: %s :: Trying to Retrieve input value", locator));
        }

    }

    public void check(Logger logger, Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            locator.check();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: Click on checkbox", locator));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator %s is not clicked", locator));
        }

    }



    public Locator filterLinkbyName(Logger logger, Page page, String linkName){
        try {
            Locator locator = page.locator("a").filter(new Locator.FilterOptions().setHasText(linkName));
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
            return locator;
        }catch (Error error){
            logger.error(String.format("Unable to fine %s link", linkName));
            throw new RuntimeException(String.format("Unable to fine %s link", linkName));
        }
    }

    public Locator filterLinkbyName(Logger logger, Page page, String linkName, int timeout){
        try {
            Locator locator = page.locator("a").filter(new Locator.FilterOptions().setHasText(linkName));
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            return locator;
        }catch (Error error){
            logger.error(String.format("Unable to fine %s link", linkName));
            throw new RuntimeException(String.format("Unable to fine %s link", linkName));
        }
    }

    public boolean  verifyNameExistInLocator(Logger logger, Locator locator, String name, String message) {
        boolean result = false;
        try {
            Locator targetCard = locator.filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile(name)));
            result = isVisible(logger, targetCard, message);
        } catch (Error error) {
            Assert.assertTrue(false, String.format("%s \n Error :%s", message, error));
        }
        return result;
    }

    public boolean  verifyNameExistInLocator(Logger logger, Locator locator, String name, String message, int timeout) {
        boolean result = false;
        try {
            Locator targetCard = locator.filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile(name)));
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            result = isVisible(logger, targetCard, message);
        } catch (Error error) {
            Assert.assertTrue(false, String.format("%s \n Error :%s", message, error));
        }
        return result;
    }


    public boolean  verifyNameExistInLocator(Logger logger, Page page, Locator locator, String name, String message) {
        boolean result = false;
        try {
            Locator targetCard = locator.filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile(name)));
            result = isVisible(logger, targetCard, message);
        } catch (Error error) {
            Assert.assertTrue(false, String.format("%s \n Error :%s", message, error));
        }
        return result;
    }

    public boolean isDisabled(Logger logger, Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Medium.getValue()));
        boolean isLocatorDisabled = locator.isDisabled();
        if (isLocatorDisabled) {
            LogHandler.logInfo(logger, String.format("[%s] is disabled", locator.toString().replace("Locator@", "")));

        } else {
            LogHandler.logInfo(logger, String.format("[%s] is not disabled", locator.toString().replace("Locator@", "")));
        }
        return  isLocatorDisabled;
    }



    public Locator filterLocatorBasedOnSelectorAndText(Logger logger, Page page, String selectorTag, String targetText) {
        try {
            Locator locator = page.locator(selectorTag).filter(new Locator.FilterOptions().setHasText(targetText));
            isVisible(logger, locator, String.format("Searching for html elment with tag %s and with text %s", selectorTag, targetText));
            logger.info(String.format("Locator %s ", locator));
            return locator;
        }catch (Error error){
            logger.error(String.format("Error : ", error));
            throw  new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public Locator filterLocatorBasedOnSelectorAndText(Logger logger, Page page, String selectorTag, String targetText, int timeout) {
        try {
            Locator locator = page.locator(selectorTag).filter(new Locator.FilterOptions().setHasText(targetText));
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            logger.info(String.format("Locator %s ", locator));
            return locator;
        }catch (Error error){
            logger.error(String.format("Error : ", error));
            throw  new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public void reload(Page page, int additionWaitTimeInSeconds) {
        page.reload();
        waitForPageLoading(page, additionWaitTimeInSeconds);
    }


    public void scrollDownUntilLocatorIsVisible(Locator locator) {
        try {
            logger.info(String.format("Scrolling Down until %s is Visible ", locator));
            locator.scrollIntoViewIfNeeded();
            logger.info(String.format("Locator %s is found after scrolling down ", locator));
        } catch (Error error) {
            logger.info(String.format("Locator %s is not even found after scrolling down ", locator));
            logger.error(String.format("Error : ", error));
            throw new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public String getLocatorAttribute(Logger logger, Locator locator, String attributeName) {
        String attributeValue =  locator.getAttribute(attributeName);
        logger.info(String.format("Attribute : %s , Value : %s  :: Locator ::%s", attributeValue, attributeName, attributeValue));
        return attributeValue;

    }

    public void clearTheInputField(Locator locator) {
        try {
            logger.info(String.format("Clearing the input field for the Locator %s ", locator));
            locator.clear();
        } catch (Error error) {
            logger.info(String.format("Unable to Clear the input field for the Locator %s  ", locator));
            logger.error(String.format("Error : ", error));
            throw new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public String extractText(Locator locator) {
        try {
            String extractedText = locator.innerText();
            LogHandler.logInfo(logger, String.format("Extracting the Text from the Locator %s :: %s", locator, extractedText));
            return extractedText;
        } catch (Error error) {
            LogHandler.logError(logger, String.format("Unable to Extract the Text from the Locator %s  ", locator));
            LogHandler.logError(logger, String.format("Error : ", error));
            throw new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public String extractText(Logger logger, Locator locator) {
        try {
            String extractedText = locator.innerText();
            LogHandler.logInfo(logger, String.format("Extracting the Text from the Locator %s :: %s", locator, extractedText));
            return extractedText;
        } catch (Error error) {
            LogHandler.logInfo(logger,String.format("Unable to Extract the Text from the Locator %s  ", locator));
            LogHandler.logError(logger,String.format("Error : ", error));
            throw new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public List<String> extractAllInnerTexts(Logger logger, Locator locator) {
        try {
            List<String> extractedText = locator.allInnerTexts();
            LogHandler.logInfo(logger, String.format("Extracting the Text from the Locator %s :: %s", locator, extractedText));
            return extractedText;
        } catch (Error error) {
            LogHandler.logInfo(logger,String.format("Unable to Extract the Text from the Locator %s  ", locator));
            LogHandler.logError(logger,String.format("Error : ", error));
            throw new RuntimeException(String.format("Error :: %s", error));
        }
    }

    public String selectFromDropDown(Logger logger, Locator locator, String value, String description) {
        LogHandler.logInfo(logger, String.format("'%s' To be Selected Value: %s", description, value));
        locator.selectOption(value);
        String selectedItem = locator.inputValue();
        LogHandler.logInfo(logger, String.format("'%s'::  Selected Value: %s", description, selectedItem));
        return selectedItem;
    }

    public Locator getButtonByName(Page page, String name) {
        Locator locator = page.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(name));
        isVisible(logger, locator);
        return locator;
    }

    public Locator getButtonByName(Page page, String name, int timeout) {
        Locator locator = page.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(name));
        isVisible(logger, locator, timeout);
        return locator;
    }

    public boolean checkIfButtonExist(Page page, String name) {

        Locator locator = page.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(name));
        boolean isLocatorVisible = locator.isVisible();
        return isLocatorVisible;
    }

    public void waitForPageLoading(Page page) {
        int MAX_LIMIT = mode.getHeavyPagesLoadingTime();
        waitForPageLoading(page, MAX_LIMIT);
    }

    public void waitForPageLoading(Page page, int timeToWaitInSeconds) {
        int isLoadingSpinnerVisible = 0;
        int MAX_LIMIT = timeToWaitInSeconds;
        logger.info("Waiting for page load : Max Timeout :: "+ MAX_LIMIT + " Seconds");
        do {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            if (MAX_LIMIT%5 == 0){
                logger.info("Waited for :: " + (mode.getHeavyPagesLoadingTime() - MAX_LIMIT+1) + " Seconds");
            }
            Locator root = page.locator("app-shared-dialog");
            Locator loadingLocator = root.getByText("Loading Application");
            Locator loadingLocator2 = root.getByText("Loading");
            Locator checkingLocator = root.getByText("Checking");
            Locator pleaseWaitLoadingLocator = root.getByText("Please waitPlease wait while");
            isLoadingSpinnerVisible = loadingLocator.or(checkingLocator).or(loadingLocator2).or(pleaseWaitLoadingLocator).count();
            if (isLoadingSpinnerVisible == 0) {
                logger.info("Page load completed");
            }
            MAX_LIMIT--;
        }while (isLoadingSpinnerVisible !=0 && MAX_LIMIT > 0);
        if (MAX_LIMIT <= 0) {
            throw new RuntimeException("Page is still loading");
        }
    }

    public boolean waitForLocator(Locator locator, int timeToWaitInSeconds) {
        int isLocatorVisible = 0;
        int MAX_LIMIT = timeToWaitInSeconds;
        do {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            if (MAX_LIMIT%5 == 0){
                logger.info("Waiting for Locator :: " + locator);
            }
            isLocatorVisible = locator.count();
            if (isLocatorVisible >= 1) {
                return true;
            }
            MAX_LIMIT--;
        }while (isLocatorVisible !=0 && MAX_LIMIT > 0);
        return false;
    }


    public void hoverOn(Logger logger, Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Large.getValue()));
            locator.hover();
            LogHandler.logInfo(logger, String.format("Locator :: %s :: hover", locator));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Locator :: %s :: hover", locator));
        }

    }
    public void uploadFile(Logger logger,Page page,String filePath) {
        try {
            Path path = Path.of(filePath);
            page.evaluate("document.getElementById('fileInput').style.visibility = 'visible';");
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            page.setInputFiles("input#fileInput",path);
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            BaseModel model = new BaseModel(page);
            model.clickOnButton("UPLOAD","Click On Upload Button");
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            if(model.verifyButtonExist("OK",8))
            {
                model.clickOnButton("OK","Click On OK Button");
            }

            LogHandler.logInfo(logger, String.format("Uploading file :: %s", filePath));
        }catch (Error error){
            LogHandler.logError(logger, String.format("Unable to upload file :: %s", filePath));
        }

    }
    public void forceclick(Logger logger, Locator locator, String description) {
        try {
            // Wait for the element to become visible, but if it's not, still attempt to forcefully click
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(Timeouts.Large.getValue()));

            // Force the click action
            locator.click(new Locator.ClickOptions().setForce(true));

            LogHandler.logInfo(logger, String.format("Locator :: %s :: forced click on %s is success", locator, description));
        } catch (Error error) {
            LogHandler.logError(logger, String.format("Locator %s could not be forcefully clicked", locator));
        }
    }


}
