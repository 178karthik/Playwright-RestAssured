package com.karthik178.listeners;

import com.microsoft.playwright.Page;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.playwritemanager.utils.JavaFakerUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.karthik178.apimanager.utils.LogHandler.attachScreenshotPNG;
import static com.karthik178.apimanager.utils.LogHandler.attachVideo;

public class UITestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(UITestListener.class);
    public static Path sPath ;

    @Override
    public void onTestStart(ITestResult result) {
        try {
                if (Objects.isNull(sPath)) {
                    sPath = Files.createTempDirectory(Paths.get("target"), "Screenshots-"+ JavaFakerUtils.getCurrentDateAndTime()+"---");
                }
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //LogHandler.logInfo(logger, result.getName());
        ITestListener.super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            Page page = (Page) result.getTestContext().getAttribute("page");
            if (Objects.nonNull(page)) {
                closeSession(page);
            }
            ITestListener.super.onTestSuccess(result);
        }catch (Error error) {
            LogHandler.logInfo(logger, "Error :: error : " + error);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Page page = (Page) result.getTestContext().getAttribute("page");
            if (Objects.nonNull(page)){
                attachScreenshotPNG(page, result.getMethod().getMethodName());
                page.context().close();
                Path videoPath = page.video().path();
                attachVideo(videoPath);
                closeSession(page);
            }
            ITestListener.super.onTestFailure(result);
        }catch (Error error) {
            LogHandler.logInfo(logger, "Error :: error : " + error);
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
    }

    @Step
    public void onFinish(ITestContext context) {
        Page page = (Page) context.getAttribute("page");
        ITestListener.super.onFinish(context);
    }

    @Step("Close the browser session")
    public void closeSession(Page page) {
        page.context().browser().close();
        LogHandler.logInfo(String.format("%s is closed successfull", page.toString()));
    }
}
