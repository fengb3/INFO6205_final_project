package com.Wordle;

import com.ArrayHelper;
import com.Helper.Log;
import com.Helper.MathHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Word
{

    // region Word map
    public static List<String> _allWords;

    public static List<String> _possibleWords;
    public static HashMap<String, Integer> _wordIndexMap;

    public static List<String> getAllWords()
    {
        if (_allWords == null)
        {
            LoadAllWords();
        }

        return _allWords;
    }

    public static List<String> getAllPossibleWords()
    {
        if (_possibleWords == null)
        {
            LoadAllWords();
        }

        return _possibleWords;
    }

    public static String getWord(int id)
    {
        if (_allWords == null)
        {
            LoadAllWords();
        }

        return _allWords.get(id);
    }

    public static int getId(String word)
    {
        if (_wordIndexMap == null)
        {
            LoadAllWords();
        }

        return _wordIndexMap.get(word);
    }

    public static int getRandomWordId()
    {
        if (_allWords == null)
        {
            LoadAllWords();
        }

        String resultWord = _possibleWords.get(MathHelper.getRandomInt(0, _possibleWords.size() - 1));

        return getId(resultWord);
    }

    public static boolean isValidWord(String word)
    {
        if (_wordIndexMap == null)
        {
            LoadAllWords();
        }

        return _wordIndexMap.containsKey(word);
    }

    public static void LoadAllWords()
    {
        _allWords = new ArrayList<>();
        _wordIndexMap = new HashMap<>();
        _possibleWords = new ArrayList<>();

        File file = new File(Const.PATH_ALL_WORD);

        try (Scanner scanner = new Scanner(file))
        {
            int index = 0;
            while (scanner.hasNext())
            {
                String word = scanner.nextLine().trim();
                if (!word.isBlank())
                {
                    _allWords.add(word);
                    _wordIndexMap.put(word, index);
                    index++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        File file1 = new File(Const.PATH_POSSIBLE_WORD);

        try(Scanner sc = new Scanner(file1))
        {
            int index = 0;

            while(sc.hasNext())
            {
                String word = sc.nextLine().trim();
                if(!word.isBlank())
                {
                    _possibleWords.add(word);
                    index++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    // endregion


    // region Word Frequency

    public static HashMap<Integer, Double> _wordFrequencyMap;

    public static double getWordFrequency(int wordId)
    {
        if (_wordFrequencyMap == null)
        {
            LoadWordFrequency();
        }

        return _wordFrequencyMap.get(wordId);
    }

    public static double getWordFrequency(String word)
    {
        return getWordFrequency(getId(word));
    }

    private static void LoadWordFrequency()
    {
        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_WORD_FREQ);
            Scanner scanner = new Scanner(fis);

            _wordFrequencyMap = new HashMap<>();
            List<String> wordsSortedByFrequency = new ArrayList<>();

            int size = getAllWords().size();
            int i = 0;

            while (scanner.hasNext())
            {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String word = parts[0];
//                double freq = Double.parseDouble(parts[1]);

                wordsSortedByFrequency.add(word);

                Log.ProgressBar("Loading word frequency", i++, size);
            }
            scanner.close();
            Log.ClearConsole();

            int xWidth = Const.WIDTH_UNDER_SIGMOID;
            double c = xWidth *(-0.5 + (double) Const.SIGMOID_CENTRE / size);

            double[] xs = ArrayHelper.linspace(c - (double) xWidth / 2, c + (double) xWidth / 2, size);

            for(int j = 0; j < size; j++)
            {
                String word = wordsSortedByFrequency.get(j);
                int wordId = getId(word);
                double x = xs[j];
                double y = MathHelper.sigmoid(x);
                _wordFrequencyMap.put(wordId, y);

                Log.ProgressBar("Calculating word frequency" + word + " " + y, j, size);
            }

            Log.ClearConsole();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // endregion
}
