package com.Wordle.Hacker;

public enum GuessStatus
{
    RIGHT_PLACE(0),
    MIS_PLACE(1),
    NOT_IN_THE_ANSWER(2);

    public int code = -1;

    GuessStatus(int code)
    {
        this.code = code;
    }

    public static GuessStatus GetByCode(int code)
    {
        for (GuessStatus status : GuessStatus.values())
        {
            if (status.code == code)
            {
                return status;
            }
        }
        return null;
    }
}
