package com.karthik178.apimanager.enums;

public enum Timeouts {

    XSmall(2000),
    Small(4000),
    Medium(6000),
    Large(8000),
    XLarge(10000),
    XXLarge(14000),
    XXXLarge(14000);

    private final int value;

    Timeouts(int i) {
        value = i;
    }

    public int getValue() { return value; }
}
