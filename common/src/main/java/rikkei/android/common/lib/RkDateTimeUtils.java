/*
 * Copyright (C) 2016 Rikkeisoft Co., Ltd.
 */

package rikkei.android.common.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * support date time convert .
 */

/**
 * Created by tuyenpx on 8/05/2016.
 */

public class RkDateTimeUtils {

    private static RkDateTimeUtils sRkDateTimeUtils;
    private static final int YESTERDAY_DATE = -1;
    private static final int TOMORROW_DATE = 1;


    public static RkDateTimeUtils getInstance() {
        if (sRkDateTimeUtils == null) {
            sRkDateTimeUtils = new RkDateTimeUtils();
        }
        return sRkDateTimeUtils;

    }

    /**
     * @param pattern : input partern to get current date time .
     * @return current date time String follow pattern .
     */

    public String getCurrentDateTime(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * @return Date object .
     */

    public Date getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * check yesterday time .
     *
     * @param timeInMilliSeconds : date need to check in Milliseconds
     * @return date is yesterday or not .
     */

    public boolean isYesterday(long timeInMilliSeconds) {
        final Calendar dateNeedcheck = Calendar.getInstance();
        dateNeedcheck.setTimeInMillis(timeInMilliSeconds);
        final Calendar yesterdayDate = Calendar.getInstance();
        yesterdayDate.add(Calendar.DATE, YESTERDAY_DATE);
        return yesterdayDate.get(Calendar.YEAR) == dateNeedcheck.get(Calendar.YEAR) && yesterdayDate.get(Calendar.DAY_OF_YEAR) == dateNeedcheck.get(Calendar.DAY_OF_YEAR);
    }


    /**
     * check tomorrow time .
     *
     * @param timeInMilliSeconds : date need to check in Milliseconds
     * @return date is tomorrow or not .
     */

    public boolean isTomorrow(long timeInMilliSeconds) {
        final Calendar dateNeedcheck = Calendar.getInstance();
        dateNeedcheck.setTimeInMillis(timeInMilliSeconds);
        final Calendar yesterdayDate = Calendar.getInstance();
        yesterdayDate.add(Calendar.DATE, TOMORROW_DATE);
        return yesterdayDate.get(Calendar.YEAR) == dateNeedcheck.get(Calendar.YEAR) && yesterdayDate.get(Calendar.DAY_OF_YEAR) == dateNeedcheck.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * @param date       : string date need to convert to Date object
     * @param oldPattern : pattern of in put date .
     * @param newPattern :  pattern of Date object need to convert to
     * @return String Date correct with input pattern
     * @throws ParseException if pattern is not defined by ISO , this one will be throws ParseException
     */

    public String convertString2Date(String date, String oldPattern, String newPattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldPattern, Locale.getDefault());
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(newPattern, Locale.getDefault());
        return formatter.format(testDate);
    }

    /**
     * @param date    : String date need to convert .
     * @param pattern : pattern use to convert
     * @return Date object
     * @throws ParseException if pattern is not defined by ISO , this one will be throws ParseException
     */

    public Date convertString2Date(String date, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.parse(date);
    }


    /**
     * @param date       : string date need to convert to Date object
     * @param newPattern :  pattern of Date object need to convert to
     * @return String Date  correct with input pattern
     * @throws ParseException if pattern is not defined by ISO , this one will be throws ParseException
     */

    public String convertDate2Date(Date date, String newPattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(newPattern, Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * @param date : date object need to convert to milliseconds
     * @return date in millisecond
     */

    public long convertDate2Milliseconds(Date date) {
        return date.getTime();
    }

    /**
     * @param date    : String date need to convert to milliseconds
     * @param pattern : pattern of String date need to convert
     * @return date in milliseconds .
     * @throws ParseException : if pattern is not defined by ISO , this one will be throws ParseException
     */

    public long convertString2Milliseconds(String date, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return convertDate2Milliseconds(formatter.parse(date));
    }

    /**
     * @param date : input date need to add
     * @param n    : count of month need to add
     * @return Date object after n months .
     */

    public Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * @param date : input date need to add
     * @param n    : count of day need to add
     * @return : Date object after n days .
     */

    public Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    /**
     * @param timeInMilliSeconds : long time need to convert to date
     * @return Date object with default format with long time inputted .
     */

    public Date convertLong2Date(long timeInMilliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return calendar.getTime();
    }

    /**
     * @param timeInMilliSeconds : long time need to convert to date
     * @return String Date with  format and long time inputted .
     */

    public String convertLong2Date(long timeInMilliSeconds, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * @param timeInMilliSeconds    : long time need to convert to String Date .
     * @param pattern : format time date .
     * @return String date correct with pattern inputted .
     */

    public String convertLong2String(long timeInMilliSeconds, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return formatter.format(calendar.getTime());
    }


}
