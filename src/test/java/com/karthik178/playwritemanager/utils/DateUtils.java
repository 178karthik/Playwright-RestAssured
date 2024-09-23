package com.karthik178.playwritemanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final Logger logger = LogManager.getLogger(DateUtils.class);


    public static String getCurrentDateAndTime(String format) {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    public static String getYesterdayDateAndTime(String format) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    public static String getFutureDate(int offsetDays, String format) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateDays = now.plusDays(offsetDays);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(futureDateDays, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = futureDateDays.format(formatter);
        return formattedDateTime;


    }

    @Test
    public void test() {

        System.out.println(getFutureDate(365, "yyyy-mm-dd"));
    }


}
