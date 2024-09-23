package com.karthik178.apimanager.utils;

import com.microsoft.playwright.Page;
import com.karthik178.listeners.UITestListener;
import com.karthik178.playwritemanager.utils.JavaFakerUtils;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

public class LogHandler {

    private static final Logger logger = LogManager.getLogger(LogHandler.class);

    public LogHandler() {
        try {
            if (Objects.isNull(UITestListener.sPath)) {
                UITestListener.sPath =
                        Files.createTempDirectory(Paths.get("target"), "Screenshots-" + JavaFakerUtils.getCurrentDateAndTime()+"---");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logInfo(Logger logger, String message) {
        logger.info(message);
        attachLogToAllure(message);
    }

    public static void logInfo(String message) {
        logger.info(message);
        attachLogToAllure(message);
    }

    public static void logError(Logger logger, String message) {
        logger.error(message);
        attachLogToAllure("Failure :: " + message);
    }

    @Attachment(value = "{logMessage}", type = "text/plain")
    private static String attachLogToAllure(String logMessage) {
        return logMessage;
    }

    @Attachment(value = "failure-screenshot", type = "image/png", fileExtension = ".png")
    public static  byte[] attachScreenshotPNG(Page page, String testname) {
        try {
            Random random = new Random();
            String path =  String.format("%s/%s-%s.png",UITestListener.sPath, testname, Integer.toString(random.nextInt()*1000));
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get( path))
                    .setFullPage(true));
            return Files.readAllBytes(Paths.get(path));
        } catch (Error | IOException error) {
            throw new RuntimeException(error);
        }

    }

    @Attachment(value = "{testname}", type = "image/png", fileExtension = ".png")
    public static  byte[] attachScreenshotPNG2(Page page, String testname) {
        try {
            Random random = new Random();
            String path =  String.format("%s/%s-%s.png",UITestListener.sPath, testname, Integer.toString(random.nextInt()*1000));
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get( path))
                    .setFullPage(true));
            return Files.readAllBytes(Paths.get(path));
        } catch (Error | IOException error) {
            throw new RuntimeException(error);
        }

    }

    @Attachment(value = "ui-execution-video", type = "video/webm", fileExtension = ".webm")
    public static  byte[] attachVideo(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Error | IOException error) {
            throw new RuntimeException(error);
        }

    }

    @Description(value = "{logMessage}")
    public static String attachDescription(String logMessage) {
        return logMessage;
    }
}
