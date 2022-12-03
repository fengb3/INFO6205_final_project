package com.Wordle.Hacker;

import com.Helper.Log;
import com.Helper.MathHelper;
import com.Wordle.Const;
import com.Wordle.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entropy
{

    // region information

    public static HashMap<Integer, Double> calcInfoMap(List<Integer> fromList)
    {
        HashMap<Integer, Double> result = new HashMap<>();

        for (int i = 0; i < fromList.size(); i++)
        {
            int wordId = fromList.get(i);
            double info = calcInfo(wordId, fromList);
            String word = Word.getWord(wordId);
            result.put(wordId, info);
            Log.ProgressBar("Calculating information for " + word, i, fromList.size());
        }
        Log.ClearConsole();

        return result;
    }

    private static double calcInfo(int wordId, List<Integer> fromList)
    {
        double info = 0;

        short totalPatternNums = (short) Math.pow(3, 5);

        for(short patternId = 0; patternId < totalPatternNums; patternId++)
        {
            int wordsCountMatches = 0;

            for(int toCheckWordId : fromList)
            {
                if(Pattern.isMatchPatternByLookUp(wordId, toCheckWordId, patternId))
                {
                    wordsCountMatches++;
                }
            }

            double p = (double) wordsCountMatches / fromList.size();

            if(p == 0)
            {
                continue;
            }

            info += p * MathHelper.safeLog2(p);
        }

        return -info;
    }

    // endregion

    // region entropy map total

    static HashMap<Integer, Double> _infoMapTotal;

    public static HashMap<Integer, Double> getEntropyMapTotal()
    {
        if(_infoMapTotal == null)
        {
            loadInfoMap(false);
        }

        return _infoMapTotal;
    }

    private static HashMap<Integer, Double> calcInfoMapTotal()
    {
        List<String> words = Word.getAllWords();
        List<Integer> wordIds = new ArrayList<>();

        for (int i = 0; i < words.size(); i++)
        {
            wordIds.add(i);
        }

        return calcInfoMap(wordIds);
    }

    static void saveInfoMapTotal()
    {
        if(_infoMapTotal == null)
        {
            _infoMapTotal = calcInfoMapTotal();
        }

        try
        {
            FileOutputStream fis = new FileOutputStream(Const.PATH_INFO_MAP);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(osw);

            int total = _infoMapTotal.size();
            int i = 0;

            for(Map.Entry<Integer, Double> entry: _infoMapTotal.entrySet())
            {
                int id = entry.getKey();
                double entropy = entry.getValue();

                bw.write(id + "," + entropy);
                bw.newLine();

                Log.ProgressBar("Saving info map", i, total);
                i++;
            }

            bw.close();
            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void readInfoMapFromFile()
    {
        _infoMapTotal = new HashMap<>();

        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_INFO_MAP);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int i = 0;
            int size = Word.getAllWords().size();

            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                double entropy = Double.parseDouble(parts[1]);

                _infoMapTotal.put(id, entropy);

                Log.ProgressBar("Reading info map", i, size);
                i++;
            }

            br.close();
            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadInfoMap(boolean regenerate)
    {
        if (regenerate)
        {
            saveInfoMapTotal();
        }
        else
        {
            readInfoMapFromFile();
        }
    }

    // endregion

    // region get word

    public static int getBestGuessWord(List<Integer> fromList, int currentTry)
    {
        HashMap<Integer, Double> infoMap;

        if (currentTry == 1) {
            infoMap = getEntropyMapTotal();
        } else {
            infoMap = calcInfoMap(fromList);
        }

        int bestWordId = -1;
        double bestScore = -1;

        for (int i = 0; i < fromList.size(); i++) {
            int wordId = fromList.get(i);
            double info = infoMap.get(wordId);

            double score = getScore(wordId, infoMap, fromList, currentTry);

            if(score > bestScore)
            {
                bestWordId = wordId;
                bestScore = info;
            }

        }

        return bestWordId;
    }

    public static Double getScore(int wordId, HashMap<Integer, Double> infoMap, List<Integer> fromList, int currentTry)
    {
        double score = 0;

        double p = Word.getWordFrequency(wordId);
        double info = infoMap.get(wordId);
        double leftInfo = MathHelper.safeLog2((double)fromList.size());

        score = p + info;

        return score;
    }

    // endregion

    public static void main(String[] args)
    {
    }

}
