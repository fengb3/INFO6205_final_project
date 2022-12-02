package com.UI;

import com.Helper.MathHelper;
import com.Helper.StringHelper;
import com.Wordle.Const;
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
    //    private JLabel labelPoss0;
//    private JLabel labelPoss1;
//    private JLabel labelPoss2;
//    private JLabel labelPoss3;
//    private JLabel labelPoss4;
//    private JLabel labelPoss5;
//    private JLabel labelBits0;
//    private JLabel labelBits1;
//    private JLabel labelBits3;
//    private JLabel labelBits2;
//    private JLabel labelBits4;
//    private JLabel labelBits5;

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

        List<Map.Entry<Integer, Double>> list = new LinkedList<>(infoMap.entrySet());

        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        String[] columnNames = {"Top pick", "E [info]"};
        Object[][] data = new Object[num][2];

        for (int i = 0; i < Math.min(num, list.size()); i++)
        {
            String word = Word.getWord(list.get(i).getKey());
            data[i][0] = word;
            data[i][1] = StringHelper.format(list.get(i).getValue(), 3);
        }

        _tableEntropy = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane sp = new JScrollPane(_tableEntropy);
        tableContainer.setLayout(new GridLayout(1, 1));

        tableContainer.add(sp);

        _tableEntropy.setRowHeight(30);
        _tableEntropy.setFont(new Font("Arial", Font.PLAIN, 20));
        _tableEntropy.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));

        // put all from list to _currentWordIds
        _currentWordIds = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : list)
        {
            _currentWordIds.add(entry.getKey());
        }

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

    public void reset()
    {
        clearTable();
        _labelPoss.forEach(label -> label.setText(""));
        _labelBits.forEach(label -> label.setText(""));
        setTableWordList(50, Entropy.getEntropyMapTotal());
        _wordleGrid.activeRow(0);
        _wordleGrid.clear();

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

        List<Integer> newWordList = Pattern.getWordsMatchPatternByLookUp(guessId, patternId, _currentWordIds);

        double reduce = (double) newWordList.size() / _currentWordIds.size();

        _labelBits.get(_wordleGrid.getCurrentRow())
                .setText(StringHelper.format(MathHelper.safeLog2(reduce), 2) + " bits");

        _wordleGrid.activeNextRow();

        setTableWordList(50, Entropy.calcInfoMap(newWordList));


    }

    private void benchmark()
    {
    }


    public static void main(String[] args)
    {
        new WindowHacker();
    }

}
