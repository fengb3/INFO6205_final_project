package com.Helper;

public class StringHelper
{
    public static String format(double value, int decimalPlaces)
    {
        String format = "%." + decimalPlaces + "f";
        return String.format(format, value);
    }

}
