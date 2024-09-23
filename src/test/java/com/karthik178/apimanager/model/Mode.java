package com.karthik178.apimanager.model;

public class Mode {

    private boolean headless;
    private int slowMotion;
    private int defaultTimeout;
    private String browser;

    private int heavyPagesLoadingTime;

    private boolean enableVideosForFailureTests;

    public Mode() {
    }

    public Mode(boolean headless, int slowMotion, int defaultTimeout, String browser) {
        this.headless = headless;
        this.slowMotion = slowMotion;
        this.defaultTimeout = defaultTimeout;
        this.browser = browser;
    }

    public Mode(boolean headless, int slowMotion, int defaultTimeout, String browser, int heavyPagesLoadingTime) {
        this.headless = headless;
        this.slowMotion = slowMotion;
        this.defaultTimeout = defaultTimeout;
        this.browser = browser;
        this.heavyPagesLoadingTime = heavyPagesLoadingTime;
    }

    public Mode(boolean headless, int slowMotion, int defaultTimeout, String browser, int heavyPagesLoadingTime, boolean enableVideosForFailureTests) {
        this.headless = headless;
        this.slowMotion = slowMotion;
        this.defaultTimeout = defaultTimeout;
        this.browser = browser;
        this.heavyPagesLoadingTime = heavyPagesLoadingTime;
        this.enableVideosForFailureTests = enableVideosForFailureTests;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getSlowMotion() {
        return slowMotion;
    }

    public void setSlowMotion(int slowMotion) {
        this.slowMotion = slowMotion;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public int getHeavyPagesLoadingTime() {
        return heavyPagesLoadingTime;
    }

    public boolean isEnableVideosForFailureTests() {
        return enableVideosForFailureTests;
    }

    public void setEnableVideosForFailureTests(boolean enableVideosForFailureTests) {
        this.enableVideosForFailureTests = enableVideosForFailureTests;
    }

    public void setHeavyPagesLoadingTime(int heavyPagesLoadingTime) {
        this.heavyPagesLoadingTime = heavyPagesLoadingTime;
    }

    @Override
    public String toString() {
        return "\n=======Browser configurations=======\n" +
                "headless=" + headless +
                "\nslowMotion=" + slowMotion +
                "\ndefaultTimeout=" + defaultTimeout + " Seconds" +
                "\nbrowser='" + browser + '\'' +
                "\nheavyPagesLoadingTime=" + heavyPagesLoadingTime + " Seconds" +
                "\nisVideosForFailureEnabled=" + enableVideosForFailureTests;
    }
}
