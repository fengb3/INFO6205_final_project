package com.Helper;

public class Log
{

    public static boolean isDebug = true;

    public static void Println(Object o)
    {
        if (isDebug)
        {
            System.out.println(ConsoleColorTextBuilder.with(o.toString()).build());
        }
    }

    public static void Print(Object o)
    {
        if (isDebug)
        {
            System.out.print(ConsoleColorTextBuilder.with(o.toString()).build());
        }
    }

    public static void Error(Object o)
    {
        if (isDebug)
        {
            System.out.println(ConsoleColorTextBuilder.with(o.toString())
                                       .background(ConsoleColorTextBuilder.Color.RED)
                                       .build());
        }
    }

    public static void Warning(Object o)
    {
        if (isDebug)
        {
            System.out.println(ConsoleColorTextBuilder.with(o.toString())
                                       .yellow()
                                       .build());
        }
    }

    public static void GrayBlock(Object o)
    {
        if (isDebug)
        {
            System.out.print(ConsoleColorTextBuilder.with(o.toString())
                                     .background(ConsoleColorTextBuilder.Color.GRAY)
                                     .build());
        }
    }

    public static String GreenBlock(Object o)
    {
        if (isDebug)
        {
            String s = ConsoleColorTextBuilder.with(o.toString())
                    .background(ConsoleColorTextBuilder.Color.GREEN)
                    .build();
            System.out.print(s);

            return s;
        }

        return "";
    }

    public static void YellowBlock(Object o)
    {
        if (isDebug)
        {
            System.out.print(ConsoleColorTextBuilder.with(o.toString())
                                     .background(ConsoleColorTextBuilder.Color.YELLOW)
                                     .build());
        }
    }

    public static void CyanBlock(Object o)
    {
        if (isDebug)
        {
            System.out.print(ConsoleColorTextBuilder.with(o.toString())
                                     .background(ConsoleColorTextBuilder.Color.CYAN)
                                     .build());
        }
    }

    public static void Enter()
    {
        if (isDebug)
        {
            System.out.println();
        }
    }

    public static void Space()
    {
        if (isDebug)
        {
            System.out.print(" ");
        }
    }

    public static void ClearConsole()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static StringBuilder _sb = new StringBuilder();

    public static void ProgressBar(String message, int current, int total)
    {
        if (isDebug)
        {
            int barLength = 50;

            int percent = (int) ((double) current / total * 100);

            _sb.setLength(0);

            _sb.append("\r" + message + " (" + current + "/" + total + ") " + percent + "% |");

            for (int i = 0; i < barLength; i++)
            {
                if(i < (int) (barLength * (percent / 100.0)))
                {
                    _sb.append(getCyanBlockText(" "));
                }
                else
                {
                    _sb.append(getGrayBlockText(" "));
                }
            }

            _sb.append("|");

            System.out.print(_sb);
        }
    }

    public static String getGreenBlockText(Object o)
    {
        return ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.GREEN)
                .build();
    }

    public static String getYellowBlockText(Object o)
    {
        return ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.YELLOW)
                .build();
    }

    public static String getGrayBlockText(Object o)
    {
        return ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.GRAY)
                .build();
    }

    public static String getCyanBlockText(Object o)
    {
        return ConsoleColorTextBuilder.with(o.toString())
                .background(ConsoleColorTextBuilder.Color.CYAN)
                .build();
    }
}

