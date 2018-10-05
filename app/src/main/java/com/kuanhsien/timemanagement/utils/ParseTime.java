package com.kuanhsien.timemanagement.utils;

public class ParseTime {

    public static String intToHourMin(int intInput) {

        int hours = intInput / 60;
        int minutes = intInput % 60;

        String strOutput;

        if (hours == 0) {
            strOutput = minutes + " min";
        } else if (minutes == 0) {
            strOutput = hours + "hr";
        } else {
            strOutput = hours + "hr " + minutes + " min";
        }

        return strOutput;
    }
}
