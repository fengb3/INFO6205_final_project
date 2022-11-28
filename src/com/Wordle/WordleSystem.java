package com.Wordle;

import com.Const.Const;
import com.Hacker.GuessPattern;
import com.Helper.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordleSystem {

    private static Set<String> _possibleWords;

    private static Wordle _instance;


    public static Wordle Instance() {
        if (_instance == null) {
            _instance = new Wordle();
            InitWordle(_instance);
        }

        return _instance;
    }

    public static void Reset() {
        InitWordle(_instance);
    }

    public static void InitWordle(Wordle wordle) {

        List<String> possibleWords = GetPossibleWords();
        Random r = new Random();

        String word = possibleWords.get(r.nextInt(possibleWords.size() - 1));

        wordle.Word = word.toCharArray();
        wordle.Tries = 0;

        System.out.println("current word: " + word);
    }

    public static List<String> GetPossibleWords() {

        if (_possibleWords != null) {
            return _possibleWords.stream().toList();
        }

        _possibleWords = new HashSet<>();

        File file = new File(Const.PATH_POSSIBLE_WORD);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String word = scanner.nextLine().trim();
                if (!word.isBlank()) {
                    _possibleWords.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return _possibleWords.stream().toList();
    }

    public static boolean CheckGuess(Wordle wordle, String guess) {
        GuessPattern.GuessStatus[] statuses = Guess(wordle, guess);
        wordle.Tries++;
        char[] guessChars = guess.toCharArray();

        boolean isAllRight = true;

        for (int i = 0; i < guessChars.length; i++) {
            switch (statuses[i]) {
                case RIGHT_PLACE:
                    Log.GreenBlock(guessChars[i]);
                    break;
                case MIS_PLACE:
                    Log.YellowBlock(guessChars[i]);
                    isAllRight = false;
                    break;
                case NOT_IN_THE_ANSWER:
                default:
                    Log.GrayBlock(guessChars[i]);
                    isAllRight = false;
                    break;
            }
            Log.Space();
        }

        Log.Enter();

        return isAllRight;
    }

    public static GuessPattern.GuessStatus[] Guess(Wordle wordle, String word) {

        if(!IsPossibleWord(word)) {
            Log.Error(word + "is not a possible word!");
            return null;
        }

        GuessPattern.GuessStatus[] guessPattern = GuessPattern.GetPattern(wordle.Word, word.toLowerCase().toCharArray());

        return guessPattern;
    }

    public static boolean IsPossibleWord(String word) {
        return GetPossibleWords().contains(word.toLowerCase());
    }

    public static boolean IsPossibleWord(char[] chars) {
        String word = "";

        for (char c : chars) {
            word += c;
        }

        return IsPossibleWord(word);
    }
}
