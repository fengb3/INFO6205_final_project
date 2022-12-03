package com.Wordle.Hacker;

import com.Helper.Log;
import com.Wordle.Const;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UncertaintyMap
{
    static HashMap<Double, List<Integer>> _uncertaintyMap;

    static HashMap<Double, Double> _uncertaintyMapAvg;

    static List<Double> _uncertaintyMapKeys;

    public static void init(boolean regenerate)
    {
        if (regenerate)
        {
            _uncertaintyMap = new HashMap<>();
            _uncertaintyMapAvg = new HashMap<>();
            _uncertaintyMapKeys = new ArrayList<>();
        }
        else
        {
            readUncertaintyMapFromFile();
        }
    }


    public static double getExpectGuess(double bits)
    {

        int start = 0;
        int end = _uncertaintyMapKeys.size() - 1;

        while(start < end)
        {
            int mid = (start + end) / 2;
            if (_uncertaintyMapKeys.get(mid) < bits)
            {
                start = mid + 1;
            } 
            else 
            {
                end = mid;
            }
        }

        return _uncertaintyMapAvg.get(_uncertaintyMapKeys.get(end));

    }

    public static void addData(double bits, int trys)
    {

        if(_uncertaintyMap == null)
        {
            _uncertaintyMap = new HashMap<>();
        }


        if(_uncertaintyMap.containsKey(bits))
        {
            _uncertaintyMap.get(bits).add(trys);
        }
        else
        {
            List<Integer> trysList = new ArrayList<>();
            trysList.add(trys);
            _uncertaintyMap.put(bits, trysList);
        }
    }

    public static void saveUncertaintyMapToFile()
    {
        try
        {

            FileOutputStream fos = new FileOutputStream(Const.PATH_UNCERTAINTY_MAP);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            List<Double> sortedKeys = new ArrayList<>(_uncertaintyMap.keySet());
            sortedKeys.sort(Double::compareTo);

            for (int i = 0; i < sortedKeys.size(); i++)
            {
                Double bits = sortedKeys.get(i);
                List<Integer> trysList = _uncertaintyMap.get(bits);
                int sum = 0;
                for (int trys : trysList)
                {
                    sum += trys;
                }
                double avg = (double) sum / trysList.size();
                bw.write(bits + "," + avg);
                bw.newLine();

                Log.ProgressBar("Saving UncertaintyMap to file", i, sortedKeys.size());
            }
            bw.close();

            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void readUncertaintyMapFromFile()
    {
        try
        {
            FileInputStream fis = new FileInputStream(Const.PATH_UNCERTAINTY_MAP);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            _uncertaintyMapAvg = new HashMap<>();
            _uncertaintyMapKeys = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                double bits = Double.parseDouble(data[0]);
                double avg = Double.parseDouble(data[1]);

                _uncertaintyMapAvg.put(bits, avg);
                _uncertaintyMapKeys.add(bits);

                Log.ProgressBar("Reading UncertaintyMap from file", _uncertaintyMapAvg.size(), 1000000);
            }
            Log.ClearConsole();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
