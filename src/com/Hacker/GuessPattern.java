package com.Hacker;

import com.Const.Const;
import com.Helper.Log;
import com.Wordle.WordleSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GuessPattern
{

    public enum GuessStatus
    {
        RIGHT_PLACE(1),
        MIS_PLACE(2),
        NOT_IN_THE_ANSWER(3);

        public int code = 0;

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

    private static ArrayList<GuessStatus[]> _possiblePatterns;

    private static HashMap<String, HashMap<String, GuessStatus[]>> _map;

    public static void LoadPossiblePatterns()
    {
        if (_possiblePatterns != null)
        {
            return;
        }

        _possiblePatterns = new ArrayList<>();

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
                            GuessStatus[] pattern = new GuessStatus[5];
                            pattern[0] = GuessStatus.values()[i];
                            pattern[1] = GuessStatus.values()[j];
                            pattern[2] = GuessStatus.values()[k];
                            pattern[3] = GuessStatus.values()[l];
                            pattern[4] = GuessStatus.values()[m];

                            _possiblePatterns.add(pattern);
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<GuessStatus[]> GetAllPossiblePatterns()
    {
        if (_possiblePatterns == null)
        {
            LoadPossiblePatterns();
        }

        return _possiblePatterns;
    }

    public static GuessStatus[] GetPattern(char[] word, char[] wordToCheck)
    {
        return GetPatternWithoutReadingMatrix(word, wordToCheck);
    }

    public static GuessStatus[] GetPatternWithoutReadingMatrix(char[] word, char[] wordToCheck)
    {
        boolean[][] equalityGrid = new boolean[5][5];

        GuessStatus[] result = new GuessStatus[5];

        // fill result with NOT_IN_THE_ANSWER
        Arrays.fill(result, GuessStatus.NOT_IN_THE_ANSWER);

        for (int i = 0; i < word.length; i++)
        {
            for (int j = 0; j < wordToCheck.length; j++)
            {
                if (word[i] == wordToCheck[j])
                {
                    equalityGrid[i][j] = true;
                }
            }
        }

        for(int i = 0; i < 5; i++)
        {
            if(equalityGrid[i][i])
            {
                result[i] = GuessStatus.RIGHT_PLACE;

                for(int k = 0; k < 5; k++)
                {
                    equalityGrid[k][i] = false;
                    equalityGrid[i][k] = false;
                }
            }
        }

        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if (equalityGrid[i][j])
                {
                    result[j] = GuessStatus.MIS_PLACE;

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

    public static List<String> GetWordListMatchPattern(String word, List<String> wordList, List<GuessStatus> pattern)
    {
        List<String> result = new ArrayList<>();

        String lower = word.toLowerCase();

        for (String wordToCheck : wordList)
        {
            if (!IsMatchPattern(wordToCheck,lower, pattern))
            {
                continue;
            }

            result.add(wordToCheck);
        }

        return result;
    }

    public static boolean IsMatchPattern(char[] word, char[] wordToCheck, GuessStatus[] pattern)
    {
        GuessStatus[] wordPattern = GetPattern(word, wordToCheck);

        for (int i = 0; i < wordPattern.length; i++)
        {
            if (wordPattern[i] != pattern[i])
            {
                return false;
            }
        }

        return true;
    }

    public static boolean IsMatchPattern(String word, String wordToCheck, List<GuessStatus> pattern)
    {
       return IsMatchPattern(word.toCharArray(), wordToCheck.toCharArray(), pattern.toArray(new GuessStatus[pattern.size()]));
    }


    public static HashMap<String, HashMap<String, GuessStatus[]>> GetPattenMatrix()
    {
        if (_map == null)
        {
            LoadPattenMatrix(false);
        }

        return _map;
    }

    public static void LoadPattenMatrix(boolean regenerate)
    {
        //start timing
        long startTime = System.currentTimeMillis();

        if (regenerate)
        {
            CreateAllPattenMatrix();
            WritePatternMatrixToFile(_map);
        }
        else
        {
            ReadPatternMatrixFromFile();
        }

        //end timing
        long endTime = System.currentTimeMillis();

        //calculate time

        long time = endTime - startTime;

        System.out.println("Load Pattern Matrix Done, Time Usage : " + time + " ms" + ", with " + _map.size() +
                           " words, regenerate: " + regenerate);
    }

    /**
     * Create a matrix of all possible patterns
     * for each word pair in the dictionary
     */
    public static void CreateAllPattenMatrix()
    {
        _map = new HashMap<>();

        List<String> possibleWords = WordleSystem.GetPossibleWords();

        _map = CreatePattenMap(possibleWords, possibleWords);
    }

    /**
     * Create a matrix 2 arrays of words
     */
    public static HashMap<String, HashMap<String, GuessStatus[]>> CreatePattenMap(List<String> words1, List<String> words2)
    {
        HashMap<String, HashMap<String, GuessStatus[]>> map = new HashMap<>();

        int[][][] matrix = CreatePatternMatrix2(words1, words2);

        Log.Println(
                "CreatePattenMap: " + words1.size() + " x " + words2.size() + " = " + matrix.length * matrix[0].length);

        for (int i = 0; i < words1.size(); i++)
        {
            String word1 = words1.get(i);
//            Log.Print("(" + i + "/" + words1.size() + ") " + word1);

            HashMap<String, GuessStatus[]> row = new HashMap<>();

            for (int j = 0; j < words2.size(); j++)
            {
                String word2 = words2.get(j);

                Log.Print("(" + i + "/" + words1.size() + ") " + word1 + "(" + j + "/" + words2.size() + ") " + word2);

                GuessStatus[] pattern = new GuessStatus[5];


                boolean isAllGray = true;

                for (int k = 0; k < 5; k++)
                {
                    pattern[k] = GuessStatus.GetByCode(matrix[i][j][k]);

                    Log.Print(pattern[k].code + " ");

                    if (pattern[k] != GuessStatus.NOT_IN_THE_ANSWER)
                    {
                        isAllGray = false;
                    }
                }

                if (!isAllGray)
                {
                    Log.Print("put");
                    row.put(word2, pattern);
                }

                Log.Enter();
            }

            map.put(word1, row);
        }

        return map;
    }


    public static int[][][] CreatePatternMatrix(List<String> wordList1, List<String> wordList2)
    {
        boolean[][][][] equality_grid = new boolean[wordList1.size()][wordList2.size()][5][5];

        int[][][] pattern_matrix = new int[wordList1.size()][wordList2.size()][5];

        Log.Print("CreatePatternMatrix: " + wordList1.size() + " x " + wordList2.size() + " = " +
                  equality_grid.length * equality_grid[0].length);

        // set all in pattern matrix to 3
        for (int i = 0; i < wordList1.size(); i++)
        {
            for (int j = 0; j < wordList2.size(); j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    pattern_matrix[i][j][k] = GuessStatus.NOT_IN_THE_ANSWER.code;
                }
                Log.Print(i * j + "/" + wordList1.size() * wordList2.size());
            }
        }

        Log.Println("CreateEqualityGrid");

        // create equality grid
        for (int i = 0; i < wordList1.size(); i++)
        {
            char[] word1 = wordList1.get(i).toCharArray();

            for (int j = 0; j < wordList2.size(); j++)
            {
                char[] word2 = wordList2.get(j).toCharArray();

                for (int k = 0; k < 5; k++)
                {
                    for (int l = 0; l < 5; l++)
                    {
                        equality_grid[i][j][k][l] = word1[k] == word2[l];
                    }
                }

                Log.Print("Creating equality grid (" + i * j + "/" + wordList1.size() * wordList2.size() + ")");
            }
        }

        Log.Println("LoadEqualityGrid _ green");

        // green
        for (int i = 0; i < wordList1.size(); i++)
        {
            for (int j = 0; j < wordList2.size(); j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    if (equality_grid[i][j][k][k])
                    {
                        pattern_matrix[i][j][k] = GuessStatus.RIGHT_PLACE.code;

                        for (int l = 0; l < 5; l++)
                        {
                            equality_grid[i][j][l][k] = false;
                            equality_grid[i][j][k][l] = false;
                        }
                    }
                }

                Log.Println("Loading pattern matrix of greening (" + i + "/" + wordList1.size() + ")(" + j + "/" +
                            wordList2.size() + ")");
            }
            Log.Enter();
        }

        Log.Println("LoadEqualityGrid _ yellow");

        // yellow
        for (int i = 0; i < wordList1.size(); i++)
        {
            for (int j = 0; j < wordList2.size(); j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    for (int l = 0; l < 5; l++)
                    {
                        if (equality_grid[i][j][k][l])
                        {
                            pattern_matrix[i][j][l] = GuessStatus.MIS_PLACE.code;

                            for (int m = 0; m < 5; m++)
                            {
                                equality_grid[i][j][m][l] = false;
                                equality_grid[i][j][k][m] = false;
                            }
                        }
                    }
                }

                Log.Println("Loading pattern matrix of yellow (" + i + "/" + wordList1.size() + ")(" + j + "/" +
                            wordList2.size() + ")");
            }
            Log.Enter();
        }

        return pattern_matrix;
    }

    public static int[][][] CreatePatternMatrix2(List<String> wordList1, List<String> wordList2)
    {
        int[][][] pattern_matrix = new int[wordList1.size()][wordList2.size()][5];

        for (int i = 0; i < wordList1.size(); i++)
        {
            String word1 = wordList1.get(i);

            for (int j = 0; j < wordList2.size(); j++)
            {
                String word2 = wordList2.get(j);

                GuessStatus[] pattern = GetPatternWithoutReadingMatrix(word1.toCharArray(), word2.toCharArray());

                for (int k = 0; k < 5; k++)
                {
                    pattern_matrix[i][j][k] = pattern[k].code;
                }
            }
        }

        return pattern_matrix;
    }

    public static void WritePatternMatrixToFile(HashMap<String, HashMap<String, GuessStatus[]>> matrix)
    {
        try
        {
            Log.Println("Start to write pattern matrix to file");
            FileOutputStream fis = new FileOutputStream(Const.PATH_PATTEN_MATRIX);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(osw);

            int total = matrix.size();
            int i = 0;
            int j = 0;

            for (String word : matrix.keySet())
            {
                j = 0;
                HashMap<String, GuessStatus[]> wordMatrix = matrix.get(word);
                int total2 = wordMatrix.size();

                for (String wordToCheck : wordMatrix.keySet())
                {

                    GuessStatus[] pattern = wordMatrix.get(wordToCheck);
                    String line = word + "," + wordToCheck + "," + pattern[0].code + "," + pattern[1].code + "," +
                                  pattern[2].code + "," + pattern[3].code + "," + pattern[4].code;
                    bw.write(line);
                    bw.newLine();

                    Log.Println("Writing pattern matrix to file (" + i + "/" + total + ")(" + j + "/" + total2 + ") " +
                                line);
                    j++;
                }
                i++;
            }
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void ReadPatternMatrixFromFile()
    {
        _map = new HashMap<>();

        try
        {
            Log.Println("Start to read pattern matrix from file");

            FileInputStream fis = new FileInputStream(Const.PATH_PATTEN_MATRIX);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line = br.readLine();

            int lineCount = 0;

            int size = WordleSystem.GetPossibleWords().size() * WordleSystem.GetPossibleWords().size();


            while (line != null)
            {
                lineCount++;

                Log.Println("Reading pattern matrix from file (" + lineCount + " / " + size +") " + line);

                String[] parts = line.split(",");
                String word = parts[0];
                String wordToCheck = parts[1];
                GuessStatus[] pattern = new GuessStatus[5];
                pattern[0] = GuessStatus.GetByCode(Integer.parseInt(parts[2]));
                pattern[1] = GuessStatus.GetByCode(Integer.parseInt(parts[3]));
                pattern[2] = GuessStatus.GetByCode(Integer.parseInt(parts[4]));
                pattern[3] = GuessStatus.GetByCode(Integer.parseInt(parts[5]));
                pattern[4] = GuessStatus.GetByCode(Integer.parseInt(parts[6]));

                HashMap<String, GuessStatus[]> wordMatrix;

                if (_map.containsKey(word))
                {
                    wordMatrix = _map.get(word);
                }
                else
                {
                    wordMatrix = new HashMap<>();
                    _map.put(word, wordMatrix);
                }

                wordMatrix.put(wordToCheck, pattern);

                line = br.readLine();
            }
            br.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        LoadPattenMatrix(true);
    }

}
