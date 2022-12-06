package com.Wordle.Hacker;

import com.Helper.Log;
import com.Wordle.Const;
import com.Wordle.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Pattern
{
    // region Pattern map
    private static HashMap<Short, int[]> _patternMap;

    public final static short AllCorrectPatternId = 0;

    /**
     * create all possible patterns for 2 words comparison
     * save them into a map
     * key is the id of the pattern
     * value is a array with length of 5 represent the pattern
     */
    public static void createPatternMap()
    {
        _patternMap = new HashMap<>();

        short count = 0;

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    for (int l = 0; l < 3; l++)
                    {
                        for (int m = 0; m < 3; m++)
                        {
                            int[] pattern = new int[5];
                            pattern[0] = i;
                            pattern[1] = j;
                            pattern[2] = k;
                            pattern[3] = l;
                            pattern[4] = m;

                            short key = count;
                            _patternMap.put(key, pattern);
                            count++;
                        }
                    }
                }
            }
        }
    }

    /**
     * get the pattern id of the pattern
     * @param index id of a pattern
     * @return the pattern as an array
     */
    public static int[] getPatternByPatternId(short index)
    {
        if (_patternMap == null)
        {
            createPatternMap();
        }

        return _patternMap.get(index);
    }

    /**
     * get the pattern id of a pattern
     * @param pattern the pattern as an array
     * @return the id of the pattern
     */
    public static short getPatternIdByPattern(int[] pattern)
    {
        if (_patternMap == null)
        {
            createPatternMap();
        }

        for (short i = 0; i < _patternMap.size(); i++)
        {
            int[] patternToCheck = _patternMap.get(i);
            if (patternToCheck[0] == pattern[0] &&
                patternToCheck[1] == pattern[1] &&
                patternToCheck[2] == pattern[2] &&
                patternToCheck[3] == pattern[3] &&
                patternToCheck[4] == pattern[4])
            {
                return i;
            }
        }

        // footy
        return -1;
    }

    /**
     * get all pattern id
     * @return a list of short represent the pattern id
     */
    public static List<Short> getAllPatternIds()
    {
        if (_patternMap == null)
        {
            createPatternMap();
        }

        List<Short> result = new ArrayList<>();
        for (short i = 0; i < _patternMap.size(); i++)
        {
            result.add(i);
        }

        return result;
    }

    // endregion

    // region pattern check

    // region pattern check - by compare

    /**
     *  check 2 words to get their pattern
     * @param word the word supposed to the answer
     * @param wordToCheck the word supposed to be the guess
     * @return an integer array with 5 elements, each element is 0, 1 or 2
     * 0 means the letter is correct
     * 1 means the letter is in the answer but in the wrong position
     * 2 means the letter is not in the answer
     */
    public static int[] checkPatternByCompare(String word, String wordToCheck)
    {
        int[] result = new int[5];

        boolean[][] equalityGrid = new boolean[Const.WORD_LENGTH][Const.WORD_LENGTH];

        Arrays.fill(result, GuessStatus.NOT_IN_THE_ANSWER.code);

        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            for (int j = 0; j <  Const.WORD_LENGTH; j++)
            {
                equalityGrid[i][j] = word.charAt(i) == wordToCheck.charAt(j);
            }
        }

        for (int i = 0; i <  Const.WORD_LENGTH; i++)
        {
            if (equalityGrid[i][i])
            {
                result[i] = GuessStatus.RIGHT_PLACE.code;

                for (int k = 0; k < 5; k++)
                {
                    equalityGrid[k][i] = false;
                    equalityGrid[i][k] = false;
                }
            }
        }

        for (int i = 0; i <  Const.WORD_LENGTH; i++)
        {
            for (int j = 0; j <  Const.WORD_LENGTH; j++)
            {
                if (equalityGrid[i][j])
                {
                    result[j] = GuessStatus.MIS_PLACE.code;

                    for (int k = 0; k < 5; k++)
                    {
                        equalityGrid[k][j] = false;
                        equalityGrid[i][k] = false;
                    }
                }
            }
        }

        return result;
    }

    /**
     * check 2 words to get their pattern
     *
     * @param word id of the word supposed to the answer
     * @param wordToCheck id of the word supposed to be the guess
     * @return the pattern id to look up in the pattern matrix
     */
    public static short checkPatternIdByCompare(String word, String wordToCheck)
    {
        int[] pattern = checkPatternByCompare(word, wordToCheck);
        return getPatternIdByPattern(pattern);
    }
    // endregion

    // region pattern check - by look up

    /**
     * check 2 words to get their pattern
     *
     * @param word1Id the word supposed to the answer
     * @param word2Id the word supposed to be the guess
     * @return the pattern id to look up in the pattern matrix
     */
    public static short checkPatternIdByLookUp(int word1Id, int word2Id)
    {
        if (_patternMatrix == null)
        {
            loadPatternMatrix(false);
        }

        return _patternMatrix[word1Id][word2Id];
    }

    /**
     * check 2 words to get their pattern
     *
     * @param word1 the word supposed to the answer
     * @param word2 the word supposed to be the guess
     * @return the pattern id to look up in the pattern matrix
     */
    public static short checkPatternIdByLookUp(String word1, String word2)
    {
        // get 2 words' id from the word list
        int word1Id = Word.getId(word1);
        int word2Id = Word.getId(word2);

        return checkPatternIdByLookUp(word1Id, word2Id);
    }

    /**
     * check 2 words to get their pattern
     * @param word1Id id of the word supposed to the answer
     * @param word2Id id of the word supposed to be the guess
     * @return an integer array with 5 elements, each element is 0, 1 or 2
     */
    public static int[] checkPatternByLookUp(int word1Id, int word2Id)
    {
        short patternId = checkPatternIdByLookUp(word1Id, word2Id);
        return getPatternByPatternId(patternId);
    }

    /**
     * check 2 words to get their pattern
     * @param word1 the word supposed to the answer
     * @param word2 the word supposed to be the guess
     * @return an integer array with 5 elements, each element is 0, 1 or 2
     */
    public static int[] checkPatternByLookUp(String word1, String word2)
    {
        int word1Id = Word.getId(word1);
        int word2Id = Word.getId(word2);

        return checkPatternByLookUp(word1Id, word2Id);
    }

    /**
     * check 2 words if they match the given pattern
     * @param word1Id id of the word supposed to the answer
     * @param word2Id id of the word supposed to be the guess
     * @param patternId id of the pattern to check
     * @return true if the 2 words match the pattern
     */
    public static boolean isMatchPatternByLookUp(int word1Id, int word2Id, int patternId)
    {
        short patternIdToCheck = checkPatternIdByLookUp(word1Id, word2Id);
        return patternIdToCheck == patternId;
    }

    /**
     * check 2 words if they match the given pattern
     * @param word2Id id of the word supposed to be the guess
     * @param patternId id of the pattern to check
     * @param fromList list of words possible to be the answer
     * @return a list of word ids that match the pattern
     */
    public static List<Integer> getWordsMatchPatternByLookUp(int word2Id,int patternId, List<Integer> fromList)
    {
        List<Integer> result = new ArrayList<>();

        for (int word1Id : fromList)
        {
            if (isMatchPatternByLookUp(word1Id, word2Id, patternId))
            {
                result.add(word1Id);
            }
        }

        return result;
    }

    // endregion

    // endregion

    // region Pattern Matrix

    /**
     * a matrix to store the pattern id each pair of 2 words
     */
    static short[][] _patternMatrix;

    /**
     *  create a matrix by looping through all the words
     */
    static void createPatternMatrix()
    {
        if (_patternMap == null)
        {
            createPatternMap();
        }

        List<String> allPossibleWords = Word.getAllWords();

        int len = allPossibleWords.size();

        _patternMatrix = new short[len][len];

        for (short i = 0; i < len; i++)
        {
            for (short j = 0; j < len; j++)
            {
                _patternMatrix[i][j] = checkPatternIdByCompare(allPossibleWords.get(i), allPossibleWords.get(j));
                Log.ProgressBar("Creating pattern matrix (" + j + "/" + len + ")", i, len);
            }
        }
        Log.ClearConsole();
    }

    /**
     * save the pattern matrix to a file
     */
    static void savePatternMatrixToFile()
    {
        if (_patternMatrix == null)
        {
            createPatternMatrix();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(Const.PATH_PATTEN_MATRIX);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);

            for (int i = 0; i < _patternMatrix.length; i++)
            {
                for (int j = 0; j < _patternMatrix[i].length; j++)
                {
                    bw.write(_patternMatrix[i][j] + ",");
                }
                bw.newLine();

                Log.ProgressBar("Saving pattern matrix to file", i, _patternMatrix.length);
            }
            bw.close();
            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * reading the pattern matrix from a file
     */
    static void readPatternMatrixFromFile()
    {
        List<String> words = Word.getAllWords();
        int len = words.size();
        _patternMatrix = new short[len][len];

        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_PATTEN_MATRIX);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++)
                {
                    _patternMatrix[lineCount][i] = Short.parseShort(values[i]);
                }
                lineCount++;

                Log.ProgressBar("Reading pattern matrix", lineCount, len);
            }

            br.close();
            Log.ClearConsole();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.Error("you may want to re-generate the pattern matrix file");
        }
    }

    /**
     * load the pattern matrix
     * @param regenerate true : re-generate the matrix by looping through all the words and compare them to get the pattern id
     *                   false : read the matrix from a file
     */
    public static void loadPatternMatrix(boolean regenerate)
    {
        if (regenerate)
        {
            createPatternMatrix();
            savePatternMatrixToFile();
        }
        else
        {
            readPatternMatrixFromFile();
        }
    }

    // endregion

    public static void main(String[] args)
    {
        // regenerate manually
        loadPatternMatrix(true);
    }
}
