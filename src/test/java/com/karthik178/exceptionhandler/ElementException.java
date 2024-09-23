package com.karthik178.exceptionhandler;

import org.testng.Assert;

public class ElementException  extends Exception{

    public ElementException(String message) {
        super(message);
        Assert.assertTrue(false, message);
    }
}
