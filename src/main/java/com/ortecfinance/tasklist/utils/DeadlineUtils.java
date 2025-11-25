package com.ortecfinance.tasklist.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DeadlineUtils {

    public static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";

    public static LocalDate parseString(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        return LocalDate.parse(strDate, formatter);
    }

    public static String getDeadlineLabel(LocalDate deadline) {
        if (deadline == null) {
            return "no deadline";
        }

        if (deadline.equals(LocalDate.now())) {
            return "today";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        return deadline.format(formatter);
    }
}
