package com.karthik178.listeners;

import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;



/**
 * Represents SMSConfigService
 * @author Karthik T
 */
@Log4j2
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {

        String methodName = new Throwable().getStackTrace()[0].getMethodName();
        logger.info(String.format("==========Test Started :: %s==========", methodName));

        ITestListener.super.onTestStart(result);
    }

    public void onFinish(ITestContext iTestContext) {
        String methodName = new Throwable().getStackTrace()[0].getMethodName();
        logger.info(String.format("==========Test Started :: %s==========", methodName));

        ITestListener.super.onFinish(iTestContext);
    }




}
