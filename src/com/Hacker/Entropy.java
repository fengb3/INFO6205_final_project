package com.Hacker;

import com.Const.Const;
import com.Helper.Log;
import com.Helper.MathHelper;
import com.Wordle.WordleSystem;

import java.io.*;
import java.util.*;

public class Entropy
{

    private static HashMap<String, List<Double>> _infoMap;

    public static List<Double> CalcInfo(char[] word, List<String> fromList)
    {
        List<Double> infoList = new ArrayList<>();

        List<GuessPattern.GuessStatus[]> patterns = GuessPattern.GetAllPossiblePatterns();

        for (GuessPattern.GuessStatus[] pattern : patterns)
        {
            int wordsCountMatches = 0;

            for (String wordToCheck : fromList)
            {
                char[] wordToCheckArray = wordToCheck.toCharArray();

                if (!GuessPattern.IsMatchPattern(word, wordToCheckArray, pattern))
                {
                    continue;
                }

                wordsCountMatches++;
            }

            double probability = (double) wordsCountMatches / (double) fromList.size();

            if (probability == 0.0)
            {
                continue;
            }

            infoList.add(probability * MathHelper.Log2(1 / probability));
        }

        return infoList;
    }

    public static HashMap<String, List<Double>> CalcInfoMap(List<String> fromList)
    {
        HashMap<String, List<Double>> entropyMap = new HashMap<>();

        for (int i = 0; i < fromList.size(); i++)
        {
            String word = fromList.get(i);

            List<Double> infoList = CalcInfo(word.toCharArray(), fromList);

            Log.Println("Calculating entropy (" + i + "/" + fromList.size() + ") " + word);

            entropyMap.put(word, infoList);
        }

        return entropyMap;
    }

    public static HashMap<String, List<Double>> GetInfoMap()
    {
        if (_infoMap == null)
        {
            LoadInfoMap(false);
        }

        return _infoMap;
    }

    public static void LoadInfoMap(boolean regenerate)
    {
        // start timing
        long startTime = System.currentTimeMillis();

        if (regenerate)
        {
            CreateAllEntropyMap();
            WriteEntropyMapToFile();
        }
        else
        {
            ReadEntropyMapFromFile();
        }

        // end timing
        long endTime = System.currentTimeMillis();

        // calculate time elapsed
        long timeElapsed = endTime - startTime;

        System.out.println("Load Entropy Done, Time Usage: " + timeElapsed + " ms, regenerate: " + regenerate);
    }

    public static void CreateAllEntropyMap()
    {
        _infoMap = CalcInfoMap(WordleSystem.GetPossibleWords());
    }

    public static void WriteEntropyMapToFile()
    {
        try
        {
            Log.Println("Write Entropy Map To File");

            FileOutputStream fis = new FileOutputStream(Const.PATH_ENTROPY_MAP);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(osw);

            int total = _infoMap.size();
            int i = 0;

            for (Map.Entry<String, List<Double>> entry : _infoMap.entrySet())
            {
                String key = entry.getKey();
                List<Double> value = entry.getValue();

                String line = key + "," +
                              String.join(",", value.stream().map(Object::toString).toArray(String[]::new));

                bw.write(line);
                bw.newLine();
                i++;

                Log.Println("Writing entropy map to file (" + i + "/" + total + ")" + key);
            }
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void ReadEntropyMapFromFile()
    {
        _infoMap = new HashMap<>();

        try
        {
            Log.Println("Read Entropy Map From File");

            FileInputStream fis = new FileInputStream(Const.PATH_ENTROPY_MAP);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int i = 0;

            int size = WordleSystem.GetPossibleWords().size();

            while ((line = br.readLine()) != null)
            {
                String[] lineSplit = line.split(",");

                String key = lineSplit[0];

                List<Double> value = new ArrayList<>();

                for (int j = 1; j < lineSplit.length; j++)
                {
                    value.add(Double.parseDouble(lineSplit[j]));
                }

                _infoMap.put(key, value);

                i++;

                Log.Println("Reading entropy map from file (" + i + " / " + size + ") " + key);
            }

            br.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * run this to generate entropy map
     */
    public static void main(String[] args)
    {
        LoadInfoMap(true);
    }
}
