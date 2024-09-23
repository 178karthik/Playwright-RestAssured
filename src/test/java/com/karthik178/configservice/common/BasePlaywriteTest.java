package com.karthik178.configservice.common;

import com.karthik178.apimanager.model.Mode;
import com.karthik178.apimanager.model.Secret;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.sms.SMSConfigService;
import com.karthik178.configservice.sms.SessionManager;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Attachment;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public abstract class BasePlaywriteTest {

    protected Browserutils browserutils;
    protected String baseurl;
    protected String userName;
    protected String password;
    private Secret secret;
    protected Mode mode;
    protected Random random;

    protected SMSConfigService smsConfigService;
    protected UserContext userContext;

    private String clientKey;
    private boolean isAPIIncluded;

    public BasePlaywriteTest() {
        mode = ConfigHelper.playwrightConfigurations();
        random = new Random();
    }

    public BasePlaywriteTest(String clientKey) {
       this.clientKey = clientKey;
    }

    public BasePlaywriteTest(String clientKey, boolean isAPIIncluded) {
        this.clientKey = clientKey;
        this.isAPIIncluded = isAPIIncluded;
    }

    @BeforeClass(alwaysRun = true)
    public void setup() {
        this.browserutils = new Browserutils();
        secret = ConfigHelper.getSecret(clientKey);
        baseurl = secret.getUrl();
        userName = secret.getEmail();
        password = secret.getPassword();
        mode = ConfigHelper.playwrightConfigurations();
        if (isAPIIncluded) {
            smsConfigService = SessionManager.getSession(clientKey);
            userContext = smsConfigService.getUserContext();
        }
        LogHandler.logInfo(secret.prettyPrint());
    }

    public static void infoLogToAllure(Logger logger, String message) {
        logger.info(message);
        attachLogToAllure("INFO: " + message);
    }

    public static void errorLogToAllure(Logger logger, String message) {
        logger.error(message);
        attachLogToAllure("ERROR: " + message);
    }

    @Attachment(value = "{logMessage}", type = "text/plain")
    private static String attachLogToAllure(String logMessage) {
        return logMessage;
    }

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
    public String getCurrentDateAndTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    public String getCurrentDateAndTime(String format) {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    public String getYesterdayDateAndTime(String format) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    public String getRandomWord() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return generatedString;
    }
    public String getRandomStringId() {
        String randomStringId= String.format("%d-%d",this.getRandomNumberUsingNextInt(10001, 99999), this.getRandomNumberUsingNextInt(10001, 99999));
        return randomStringId;
    }
    public String getRandomSentence() {

        return  String.format("%s %s %s", getRandomWord(), getRandomWord(), getRandomWord());
    }


}
