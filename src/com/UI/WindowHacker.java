package com.UI;

import com.Helper.MathHelper;
import com.Helper.StringHelper;
import com.Wordle.Const;
import com.Wordle.Hacker.Benchmark;
import com.Wordle.Hacker.Entropy;
import com.Wordle.Hacker.Pattern;
import com.Wordle.Word;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class WindowHacker extends JFrame
{
    private JButton submitButton;
    private JButton benchmarkButton;
    private JButton resetButton;
    private JPanel panelGridContainer;
    private JPanel panelMain;
    private JPanel tableContainer;
    private JPanel panelLabelPossContainer;
    private JPanel panelLabelBitsContainer;

    JTable _tableEntropy;

    private WordleGrid _wordleGrid;

    private List<Integer> _currentWordIds;

    private List<JLabel> _labelPoss;
    private List<JLabel> _labelBits;

    public WindowHacker()
    {
        setTitle("Hacker");
        setContentPane(panelMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setUpGrid();
        setUpLabel();

        reset();

        pack();
        setVisible(true);
        setResizable(false);

        submitButton.addActionListener(e -> submit());
        benchmarkButton.addActionListener(e -> benchmark());
        resetButton.addActionListener(e -> reset());
    }

    public void setUpGrid()
    {
        panelGridContainer.setLayout(new GridLayout(1, 1));
        _wordleGrid = new WordleGrid();
        panelGridContainer.add(_wordleGrid);
        _wordleGrid.setRightClickEvent();
    }

    public void setUpLabel()
    {
        _labelBits = new ArrayList<>();
        _labelPoss = new ArrayList<>();

        panelLabelPossContainer.setLayout(new GridLayout(6, 1));
        panelLabelBitsContainer.setLayout(new GridLayout(6, 1));

        for(int i = 0; i < Const.WORD_TRIES; i++)
        {
            JLabel labelPoss = new JLabel();
            JLabel labelBits = new JLabel();

            _labelPoss.add(labelPoss);
            _labelBits.add(labelBits);

            panelLabelPossContainer.add(labelPoss);
            panelLabelBitsContainer.add(labelBits);
        }
    }

    public void setTableWordList(int num, HashMap<Integer, Double> infoMap)
    {
        clearTable();

        _currentWordIds = new ArrayList<>(infoMap.keySet());


        List<TableItemData> list = new ArrayList<>();

        // if it is first step, get best beginner
        if(_wordleGrid.getCurrentRow() == 0)
        {
            List<Integer> bestBeginners = Entropy.getTopBeginners();

            for (int i = 0; i < bestBeginners.size(); i++)
            {
                int id = bestBeginners.get(i);
                list.add(new TableItemData(id, infoMap.get(id), Word.getWordFrequency(id)));
            }
        }
        // else get from rest of the word and sort them by entropy
        else
        {
            for(Map.Entry<Integer, Double> entry : infoMap.entrySet())
            {
                TableItemData
                        item = new TableItemData(entry.getKey(), entry.getValue(), Word.getWordFrequency(entry.getKey()));
                list.add(item);
            }
            list.sort(TableItemData::compareTo);
        }

        String[] columnNames = {"Top pick", "E [info]", "P"};
        Object[][] data = new Object[num][3];

        for (int i = 0; i < Math.min(num, list.size()); i++)
        {
            String word = Word.getWord(list.get(i).wordId);
            data[i][0] = word;
            data[i][1] = StringHelper.format(list.get(i).info, 3);
            data[i][2] = StringHelper.format(list.get(i).freq, 3);
        }

        _tableEntropy = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane sp = new JScrollPane(_tableEntropy);
        tableContainer.setLayout(new GridLayout(1, 1));

        tableContainer.add(sp);

        _tableEntropy.setRowHeight(30);
        _tableEntropy.setFont(new Font("Arial", Font.PLAIN, 20));
        _tableEntropy.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));


        int count = _currentWordIds.size();

        _labelPoss.get(_wordleGrid.getCurrentRow())
                .setText(count+ " possibilities, " + StringHelper.format(MathHelper.safeLog2((double)count),2) + " bits");


    }

    public void clearTable()
    {
        tableContainer.removeAll();
        tableContainer.repaint();
        tableContainer.revalidate();
    }


    public WordleGrid getWordleGrid()
    {
        return _wordleGrid;
    }

    public void reset()
    {
        clearTable();
        _labelPoss.forEach(label -> label.setText(""));
        _labelBits.forEach(label -> label.setText(""));
//        setTableWordList(50, Entropy.getEntropyMapTotal());
        _wordleGrid.activeRow(0);
        _wordleGrid.clear();
        setTableWordList(50, Entropy.getEntropyMapTotal());

    }

    public void submit()
    {
        String guess = _wordleGrid.getCurrentRowText();
        guess = guess.toLowerCase();
        if(!Word.isValidWord(guess))
        {
            JOptionPane.showMessageDialog(null, "Invalid word");
            return;
        }
        int guessId = Word.getId(guess);

        int row = _wordleGrid.getCurrentRow();

        int[] pattern = _wordleGrid.getCurrentRowPattern();

        int patternId = Pattern.getPatternIdByPattern(pattern);

        if(patternId == -1)
        {
            JOptionPane.showMessageDialog(null, "Invalid pattern");
            return;
        }

        List<Integer> newWordList = Pattern.getWordsMatchPatternByLookUp(guessId, patternId, _currentWordIds);

        double reduce = (double) newWordList.size() / _currentWordIds.size();

        _labelBits.get(_wordleGrid.getCurrentRow())
                .setText(StringHelper.format(MathHelper.safeLog2(reduce), 2) + " bits");

        _wordleGrid.activeNextRow();

        setTableWordList(50, Entropy.calcInfoMap(newWordList));
    }

    private void benchmark()
    {
        Benchmark.run();
    }

    public class TableItemData implements Comparable<TableItemData>
    {
        public int wordId;
        public double info;
        public double freq;

        public TableItemData(int wordId, double info, double freq)
        {
            this.wordId = wordId;
            this.info = info;
            this.freq = freq;
        }

        @Override
        public int compareTo(TableItemData t)
        {
            if(this.info + this.freq > t.info + t.freq)
                return -1;
            else if(this.info + this.freq < t.info + t.freq)
                return 1;
            else
                return 0;
        }

        public double GetScore()
        {
            return this.info + this.freq;
        }
    }


    public static void main(String[] args)
    {
        new WindowHacker();
    }

}
