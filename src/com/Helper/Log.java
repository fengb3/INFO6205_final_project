package com.Helper;

public class Log {
    public static void Println(Object o)
    {
        System.out.println(ConsoleColorTextBuilder.with(o.toString()).build());
    }

    public static void Print(Object o)
    {
        System.out.print(ConsoleColorTextBuilder.with(o.toString()).build());
    }

    public static void Error(Object o)
    {
        System.out.println(ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.RED)
                .build());
    }

    public static void Warning(Object o)
    {
        System.out.println(ConsoleColorTextBuilder.with(o.toString())
                .yellow()
                .build());
    }

    public static void GrayBlock(Object o)
    {
        System.out.print(ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.GRAY)
                .build());
    }

    public static void GreenBlock(Object o)
    {
        System.out.print(ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.GREEN)
                .build());
    }

    public static void YellowBlock(Object o)
    {
        System.out.print(ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.YELLOW)
                .build());
    }

    public static void Enter()
    {
        System.out.println();
    }

    public static void Space()
    {
        System.out.print(" ");
    }
}
