package com.Wordle;

import java.awt.*;

public class Const
{
    // path
    public static final String PATH_POSSIBLE_WORD = "data/allowed_words.txt";
    public static final String PATH_PATTEN_MATRIX = "data/patten_matrix.txt";
    public static final String PATH_INFO_MAP = "data/entropy_map.txt";
    public static final String PATH_WORD_FREQ = "data/word_freq.txt";

    // word count
    public static final int COMMON_WORD_INDEX = 3000;
    public static final int WIDTH_UNDER_SIGMOID = 10;

    // color
    public static final Color DarkGreen = new Color(0, 152, 0);
    public static final Color Gray = new Color(128, 128, 128);
    public static final Color Yellow = new Color(255, 255, 0);
    public static final Color White = new Color(255, 255, 255);

    // wordle
    public static final int WORD_LENGTH = 5;
    public static final int WORD_TRIES = 6;
    public static final Color[] COLORS = new Color[]{DarkGreen, Yellow, Gray, White};
}
