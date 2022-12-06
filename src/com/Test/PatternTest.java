package com.Test;

import com.Wordle.Hacker.Pattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternTest
{

    @Test
    void testCheckPatternByCompare01()
    {
        String answer = "bring";
        String guess = "bring";

        int[] truePattern = new int[]{0,0,0,0,0};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByCompare02()
    {
        String answer = "abide";
        String guess  = "speed";

        int[] truePattern = new int[]{2,2,1,2,1};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByCompare03()
    {
        String answer = "erase";
        String guess  = "speed";

        int[] truePattern = new int[]{1,2,1,1,2};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByCompare04()
    {
        String answer = "steal";
        String guess  = "speed";

        int[] truePattern = new int[]{0,2,0,2,2};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByLookUp01()
    {
        String answer = "bring";
        String guess = "bring";

        int[] truePattern = new int[]{0,0,0,0,0};
        int[] guessPattern = Pattern.checkPatternByLookUp(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByLookUp02()
    {
        String answer = "abide";
        String guess  = "speed";

        int[] truePattern = new int[]{2,2,1,2,1};
        int[] guessPattern = Pattern.checkPatternByLookUp(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByLookUp03()
    {
        String answer = "erase";
        String guess  = "speed";

        int[] truePattern = new int[]{1,2,1,1,2};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }

    @Test
    void testCheckPatternByLookUp04()
    {
        String answer = "steal";
        String guess  = "speed";

        int[] truePattern = new int[]{0,2,0,2,2};
        int[] guessPattern = Pattern.checkPatternByCompare(answer, guess);

        assertTrue(areSameArray(guessPattern, truePattern));
    }


    static boolean areSameArray(int[] a, int[] b)
    {
        if(a.length != b.length)
        {
            return false;
        }
        for(int i = 0; i < a.length; i++)
        {
            if(a[i] != b[i])
            {
                return false;
            }
        }
        return true;
    }
}