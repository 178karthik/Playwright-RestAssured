package com.karthik178.playwritemanager.utils;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JavaFakerUtils{

    private static final Logger logger = LogManager.getLogger(JavaFakerUtils.class);

    public static String getRandomStringId() {
        String randomNumber= Faker.instance().idNumber().valid();
        return randomNumber;
    }
    public static int getRandomNumberWithRange(int min, int max) {
        int randomNumber= Faker.instance().random().nextInt(min,max);
        return randomNumber;
    }
    public static String getRandomJobDesignation() {
        String  randomJobDesignation = Faker.instance().job().title().toString();
        return randomJobDesignation;
    }
    public static String getRandomWord() {
        String  randomWord = Faker.instance().university().name().toString();
        return randomWord;
    }

    public static String getCurrentDateAndTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }


}
