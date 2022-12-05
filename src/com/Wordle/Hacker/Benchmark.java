package com.Wordle.Hacker;

import com.Helper.Log;
import com.Wordle.Word;
import com.Wordle.Const;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.time.format.DateTimeFormatter;

public class Benchmark {
    static List<String> _words;
    static List<Integer> _wordIds;

    static HashMap<Integer, Integer> _benchmarkMap;

    public static void run() {
        _words = Word.getAllPossibleWords();
        _wordIds = new ArrayList<>();

        for (int i = 0; i < _words.size(); i++) {
            _wordIds.add(i);
        }

        _benchmarkMap = new HashMap<>();

        // UncertaintyMap.init(true);

        for (int i = 0; i < _wordIds.size(); i++) {
            int trys = testWord(i, -1);

            if (_benchmarkMap.containsKey(trys)) {
                _benchmarkMap.put(trys, _benchmarkMap.get(trys) + 1);
            } else {
                _benchmarkMap.put(trys, 1);
            }

            Log.ProgressBar("Testing word " + Word.getWord(i) + " with " + trys + " trys", i, _words.size());

        }

        Log.ClearConsole();

        saveBenchmarkToFile();
        // saveUncertaintyMapToFile();
    }

    public static int testWord(int wordId, int forceStartWordId) {
        // copy wordIds
        List<Integer> fromList = new ArrayList<>(_wordIds);

        int trys = 0;

        StringBuilder sb = new StringBuilder();

        // HashMap<Double, Integer> currUncertaintyMap = new HashMap<>();

        sb.append("\n\t");
        sb.append(Word.getWord(wordId));
        sb.append("\n");

        while (true) {
            trys++;

            Log.isDebug = false;
            int guessWordId;

            guessWordId = Entropy.getBestGuessWord(fromList, trys, forceStartWordId);
            
            Log.isDebug = true;

            String guessWord = Word.getWord(guessWordId);

            short patternId = Pattern.checkPatternIdByLookUp(wordId, guessWordId);

            int[] pattern = Pattern.getPatternByPatternId(patternId);

            sb.append(trys);
            sb.append("\t");

            for (int i = 0; i < pattern.length; i++) {
                switch (pattern[i]) {
                    case 0:
                        // Log.GreenBlock(guessWord.charAt(i));
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

            if (patternId == Pattern.AllCorrectPatternId) {
                break;
            }

            fromList = Pattern.getWordsMatchPatternByLookUp(guessWordId, patternId, fromList);

            // int copyOfTry = trys;

            // currUncertaintyMap.put(MathHelper.safeLog2((double)fromList.size()),
            // copyOfTry);
        }

        Log.ClearConsole();
        Log.Println(sb.toString());

        // for(double key : currUncertaintyMap.keySet())
        // {
        // UncertaintyMap.addData(key, trys - currUncertaintyMap.get(key));
        // }

        return trys;
    }

    public static void saveBenchmarkToFile() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM_dd_HH_mm_ss");
            String fileName = "data" + File.separator + "benchmark_" + dtf.format(java.time.LocalDateTime.now()) + ".txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            int total = 0;
            int count = 0;

            for (int key : _benchmarkMap.keySet()) {
                bw.write(key + "," + _benchmarkMap.get(key));
                bw.newLine();

                count += _benchmarkMap.get(key);
                total += key * _benchmarkMap.get(key);

                Log.ProgressBar("Saving benchmark to file", count, _words.size());
            }

            bw.write("Average: " + (double) total / count);

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // region benchmark force start

    static HashMap<Integer, Double> _topBeginnerMap;

    public static void runForceStart() {
        List<Integer> topBeginnerIds = Entropy.readTopBeginnersFromFile();
        _topBeginnerMap = new HashMap<>();
        _words = Word.getAllPossibleWords();
        _wordIds = new ArrayList<>();

        for (int i = 0; i < _words.size(); i++) {
            _wordIds.add(i);
        }

        for (int i = 0; i < topBeginnerIds.size(); i++) {
            double averageScore = 0;

            Log.ClearConsole();
            Log.ProgressBar("Testing beginner word " + Word.getWord(topBeginnerIds.get(i)), i,  topBeginnerIds.size());
            Log.Enter();

            for (int j = 0; j < _wordIds.size(); j++) {
                int trys = testWord(_wordIds.get(j), topBeginnerIds.get(i));
                averageScore += trys;
                Log.ProgressBar("Testing word " + Word.getWord(j) + " with " + trys + " trys",j, _wordIds.size());
            }

            averageScore = averageScore / _wordIds.size();
            _topBeginnerMap.put(topBeginnerIds.get(i), averageScore);

            // Log.Println(topBeginnerIds.get(i) + " " + averageScore);
        }

        saveForceStartBenchmarkToFile();
    }

    private static void saveForceStartBenchmarkToFile() {
        try {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM_dd_HH_mm_ss");
            String fileName = Const.PATH_TOP_BEGINNERS_BENCHMARK + dtf.format(java.time.LocalDateTime.now()) + ".txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(_topBeginnerMap.entrySet());

            sorted.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

            int total = _topBeginnerMap.size();
            int count = 0;

            for (Map.Entry<Integer,Double> entry : sorted) {
                bw.write(Word.getWord(entry.getKey()) + "," + entry.getKey() + "," + entry.getValue());
                bw.newLine();

                count += 1;

                Log.ProgressBar("Saving benchmark to file", count, total);
            }

            Log.ClearConsole();
            // bw.write("Average: " + (double) total / count);

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    public static void main(String[] args) {
//        runForceStart();
         run();
    }
}
