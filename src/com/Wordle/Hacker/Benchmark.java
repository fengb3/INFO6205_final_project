package com.Wordle.Hacker;

import com.Helper.Log;
import com.Helper.MathHelper;
import com.Wordle.Const;
import com.Wordle.Word;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.Wordle.Hacker.UncertaintyMap.saveUncertaintyMapToFile;

public class Benchmark
{
    static List<String> _words;
    static List<Integer> _wordIds;

    static HashMap<Integer, Integer> _benchmarkMap;

    public static void Run()
    {
        _words = Word.getAllPossibleWords();
        _wordIds = new ArrayList<>();

        for (int i = 0; i < _words.size(); i++)
        {
            _wordIds.add(i);
        }

        _benchmarkMap = new HashMap<>();

//        UncertaintyMap.init(true);

        for (int i = 0; i < _words.size(); i++)
        {
            int trys = testWord(i);

            if(_benchmarkMap.containsKey(trys))
            {
                _benchmarkMap.put(trys, _benchmarkMap.get(trys) + 1);
            }
            else
            {
                _benchmarkMap.put(trys, 1);
            }

            Log.ProgressBar("Testing word " + _words.get(i) + " with " + trys + " trys", i, _words.size());

        }

        Log.ClearConsole();

        saveBenchmarkToFile();
//        saveUncertaintyMapToFile();
    }

    public static int testWord(int wordId)
    {
        // copy wordIds
        List<Integer> fromList = new ArrayList<>(_wordIds);

        int trys = 0;

        StringBuilder sb = new StringBuilder();

//        HashMap<Double, Integer> currUncertaintyMap = new HashMap<>();

        sb.append("\t");
        sb.append(Word.getWord(wordId));
        sb.append("\n");

        while (true)
        {
            trys++;

            Log.isDebug = false;
            int guessWordId = Entropy.getBestGuessWord(fromList, trys);
            Log.isDebug = true;

            String guessWord = Word.getWord(guessWordId);

            short patternId = Pattern.checkPatternIdByLookUp(wordId, guessWordId);

            int[] pattern = Pattern.getPatternByPatternId(patternId);

            sb.append(trys);
            sb.append("\t");

            for (int i = 0; i < pattern.length; i++)
            {
                switch (pattern[i])
                {
                    case 0:
                        //                        Log.GreenBlock(guessWord.charAt(i));
                        sb.append(Log.getGreenBlockText(guessWord.charAt(i)));
                        break;
                    case 1:
                        sb.append(Log.getYellowBlockText(guessWord.charAt(i)));
                        break;
                    case 2:
                        sb.append(Log.getGrayBlockText(guessWord.charAt(i)));
                        break;
                    default:
                        break;
                }
            }

            sb.append("\n");

            if (patternId == Pattern.AllCorrectPatternId)
            {
                break;
            }

            fromList = Pattern.getWordsMatchPatternByLookUp(guessWordId, patternId, fromList);

//            int copyOfTry = trys;

//            currUncertaintyMap.put(MathHelper.safeLog2((double)fromList.size()), copyOfTry);
        }

        Log.ClearConsole();
        Log.Println(sb.toString());

//        for(double key : currUncertaintyMap.keySet())
//        {
//            UncertaintyMap.addData(key, trys - currUncertaintyMap.get(key));
//        }

        return trys;
    }

    public static void saveBenchmarkToFile()
    {
        try
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM_dd_HH_mm_ss");
            String fileName = "data\\benchmark_"+dtf.format(java.time.LocalDateTime.now()) + ".txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            int total = 0;
            int count = 0;

            for(int key : _benchmarkMap.keySet())
            {
                bw.write(key + "," + _benchmarkMap.get(key));
                bw.newLine();

                count += _benchmarkMap.get(key);
                total += key * _benchmarkMap.get(key);

                Log.ProgressBar("Saving benchmark to file", count, _words.size());
            }

            bw.write("Average: " + (double)total / count);

            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Run();
    }
}
