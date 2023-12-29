package com.uts.mobprog210040138.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatterHelpers {
    private static final SimpleDateFormat SHORT_FORMATTER = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat LONG_FORMATTER = new SimpleDateFormat("dd MMM yyyy HH:mm a");

    // format 19 Jan 2023
    public static String formatShortDate(String inputDate) {
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormatter.parse(inputDate);
            return SHORT_FORMATTER.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    // format 19 Jan 2023 13:20 AM
    public static String formatLongDate(String inputDate) {
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormatter.parse(inputDate);
            return LONG_FORMATTER.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
}
