package com.Helper;

public class MathHelper
{
    public static Double logBase(Double base, Double number)
    {
        return Math.log(number) / Math.log(base);
    }

    public static Double safeLog2(Double number)
    {
        if(number <= 0)
        {
            return 0.0;
        }

        return logBase(2.0, number);
    }
}
