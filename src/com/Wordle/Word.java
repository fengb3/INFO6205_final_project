package com.Wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Word
{
    public static List<String> _possibleWords;
    public static HashMap<String, Integer> _wordIndexMap;

    public static List<String> getAllWords()
    {
        if (_possibleWords == null)
        {
            LoadPossibleWords();
        }

        return _possibleWords;
    }

    public static String getWord(int id)
    {
        if (_possibleWords == null)
        {
            LoadPossibleWords();
        }

        return _possibleWords.get(id);
    }

    public static int getId(String word)
    {
        if (_wordIndexMap == null)
        {
            LoadPossibleWords();
        }

        return _wordIndexMap.get(word);
    }

    public static int getRandomWordId()
    {
        if (_possibleWords == null)
        {
            LoadPossibleWords();
        }

        return (int) (Math.random() * _possibleWords.size());
    }

    public static boolean isValidWord(String word)
    {
        if (_wordIndexMap == null)
        {
            LoadPossibleWords();
        }

        return _wordIndexMap.containsKey(word);
    }

    public static void LoadPossibleWords()
    {
        _possibleWords = new ArrayList<>();
        _wordIndexMap = new HashMap<>();

        File file = new File(Const.PATH_POSSIBLE_WORD);

        try (Scanner scanner = new Scanner(file))
        {
            int index = 0;
            while (scanner.hasNext())
            {
                String word = scanner.nextLine().trim();
                if (!word.isBlank())
                {
                    _possibleWords.add(word);
                    _wordIndexMap.put(word, index);
                    index++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}
