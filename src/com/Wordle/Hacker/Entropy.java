package com.Wordle.Hacker;

import com.Helper.Log;
import com.Helper.MathHelper;
import com.Wordle.Const;
import com.Wordle.Word;

import java.io.*;
import java.util.*;

public class Entropy
{

    // region information

    /**
     * calculates the entropy for each word in the wordlist
     *
     * @param fromList the list of words to calculate the entropy for
     * @return a map with the wordId as key and the entropy as value
     */
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

    /**
     * calculates the entropy for a single word in a given wordlist
     *
     * @param wordId   the word to calculate the entropy for
     * @param fromList the list of words to calculate the entropy for
     * @return the entropy of the word
     */
    private static double calcInfo(int wordId, List<Integer> fromList)
    {
        double info = 0;

        HashMap<Short, Integer> matchesPattern = new HashMap<>();

        for (int i = 0; i < fromList.size(); i++)
        {
            short patternId = Pattern.checkPatternIdByLookUp(wordId, fromList.get(i));

            if (matchesPattern.containsKey(patternId))
            {
                matchesPattern.put(patternId, matchesPattern.get(patternId) + 1);
            }
            else
            {
                matchesPattern.put(patternId, 1);
            }
        }

        for(Map.Entry<Short, Integer> entry : matchesPattern.entrySet())
        {
            double p = (double) entry.getValue() / fromList.size(); // calculate the probability of the pattern

            if (p == 0)
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
        if (_infoMapTotal == null)
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
        if (_infoMapTotal == null)
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

            for (Map.Entry<Integer, Double> entry : _infoMapTotal.entrySet())
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

    public static int getBestGuessWord(List<Integer> fromList, int currentTry, int forceStartWordId)
    {
        if (forceStartWordId != -1 && currentTry == 1)
        {
            return forceStartWordId;
        }

        HashMap<Integer, Double> infoMap;

        if (currentTry == 1)
        {
            infoMap = getEntropyMapTotal();
        }
        else
        {
            infoMap = calcInfoMap(fromList);
        }

        int bestWordId = -1;
        double bestScore = -1;

        for (int i = 0; i < fromList.size(); i++)
        {
            int wordId = fromList.get(i);
            double info = infoMap.get(wordId);

            double score = getScore(wordId, infoMap, fromList, currentTry);

            if (score > bestScore)
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
        double leftInfo = MathHelper.safeLog2((double) fromList.size());

        score = info;

        return score;
    }

    // endregion

    // region entropy 2 steps

    public static double calcInformationAtSecondSteps(int wordId)
    {
        List<Integer> wordIds = Word.getAllWordIds();

        double maxInfo = -1;
        int maxInfoWordId = -1;

        List<Short> patternIds = Pattern.getAllPatternIds();

        for (short patternId : patternIds)
        {
            List<Integer> fromList = Pattern.getWordsMatchPatternByLookUp(wordId, patternId, wordIds);
            double currInfo = calcInfo(wordId, fromList);

            if (currInfo > maxInfo)
            {
                maxInfo = currInfo;
                maxInfoWordId = patternId;
            }
        }

        return maxInfo;
    }

    public static HashMap<Integer, Double> calcInformation2Steps()
    {
        HashMap<Integer, Double> result = new HashMap<>();

        HashMap<Integer, Double> infoMap = getEntropyMapTotal();

        int i = 0;
        for (Map.Entry<Integer, Double> entry : infoMap.entrySet())
        {
            i++;
            int wordId = entry.getKey();
            double fistStepInfo = entry.getValue();
            double secondStepInfo = calcInformationAtSecondSteps(wordId);
            result.put(wordId, fistStepInfo + secondStepInfo);
            Log.ProgressBar("Calculating information 2 steps", i, infoMap.size());
        }
        Log.ClearConsole();

        return result;
    }

    public static void writeTopBeginnersToFile(int tops)
    {
        HashMap<Integer, Double> infoMap2steps = calcInformation2Steps();

        List<Map.Entry<Integer, Double>> sortedList = new ArrayList<>(infoMap2steps.entrySet());

        sortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        try
        {
            FileOutputStream fis = new FileOutputStream(Const.PATH_TOP_BEGINNERS);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(osw);

            int total = tops;
            int i = 0;

            for (Map.Entry<Integer, Double> entry : sortedList)
            {
                int id = entry.getKey();
                double entropy = entry.getValue();

                bw.write(id + "," + entropy);
                bw.newLine();

                Log.ProgressBar("Saving top 25 beginners", i, total);
                i++;

                if (i >= total)
                {
                    break;
                }
            }

            bw.close();
            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<Integer> readTopBeginnersFromFile()
    {
        List<Integer> result = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_TOP_BEGINNERS);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int i = 0;

            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                result.add(id);

                Log.ProgressBar("Reading top beginner words", i, 25);
                i++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<Integer, Double> readTopBenchmarkBeginners()
    {
        HashMap<Integer, Double> result = new HashMap<>();

        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_TOP_BEGINNERS_BENCHMARK_FULL);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int i = 0;

            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[1]);
                double step = Double.parseDouble(parts[2]);
                result.put(id, step);

                Log.ProgressBar("Reading top benchmark beginner words", i, 25);
                i++;
            }

            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static List<Integer> getTopBeginners()
    {

        HashMap<Integer, Double> topBeginners = readTopBenchmarkBeginners();

        List<Integer> result = new ArrayList<>(topBeginners.keySet());

        result.sort(Comparator.comparing(topBeginners::get));

        return result;
    }

    // endregion

    public static void main(String[] args)
    {
        writeTopBeginnersToFile(50);
    }

}
