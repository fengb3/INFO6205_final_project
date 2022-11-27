package com.Helper;

public class MathHelper
{

    public static double LogBase(double base, double value)
    {
        return Math.log(value) / Math.log(base);
    }

    public static double Log2(double n) {
        return LogBase(2, n);
    }
}
