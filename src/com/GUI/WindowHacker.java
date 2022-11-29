package com.GUI;

import com.Hacker.Entropy;
import com.Hacker.GuessPattern;
import com.Helper.Log;
import com.Helper.MathHelper;
import com.Wordle.Wordle;
import com.Wordle.WordleSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WindowHacker extends JFrame
{

    private JPanel panelMain;
    private JPanel panelList;
    private JPanel panelWordleGrid;

    private JButton buttonSubmit;
    private JButton buttonReset;
    private JButton runBenchmarkButton;
    private JPanel panelButton;
    private JLabel labelPoss1;
    private JLabel labelPoss2;
    private JLabel labelPoss3;
    private JLabel labelPoss4;
    private JLabel labelPoss5;
    private JLabel labelBits1;
    private JLabel labelBits2;
    private JLabel labelBits3;
    private JLabel labelBits4;
    private JLabel labelBits5;
    private JLabel labelBits6;
    private JLabel labelPoss6;
    private JTable tableEntropy;

    private int _currentRow = 0;

    private ArrayList<ArrayList<JTextField>> _textFields = new ArrayList<>();

    private List<String> _currentWordList = new ArrayList<>();

    private List<JLabel> _possLabels = new ArrayList<>();

    private List<JLabel> _bitsLabels = new ArrayList<>();

    public WindowHacker()
    {
        initComponents();
    }

    private void initComponents()
    {
        setContentPane(panelMain);
        setTitle("Wordle Hacker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(RightClickMenu.getInstance());
        setSize(1000, 600);
        setResizable(false);
        setVisible(true);

        buttonReset.addActionListener(e -> onButtonResetClicked());

        buttonSubmit.addActionListener(e -> onButtonSubmitClicked());

        //region set up labels

        _possLabels.add(labelPoss1);
        _possLabels.add(labelPoss2);
        _possLabels.add(labelPoss3);
        _possLabels.add(labelPoss4);
        _possLabels.add(labelPoss5);
        _possLabels.add(labelPoss6);

        _bitsLabels.add(labelBits1);
        _bitsLabels.add(labelBits2);
        _bitsLabels.add(labelBits3);
        _bitsLabels.add(labelBits4);
        _bitsLabels.add(labelBits5);
        _bitsLabels.add(labelBits6);

        _possLabels.forEach(l ->
                            {
                                l.setText(" ");
                                l.setFont(new Font("Arial", Font.PLAIN, 20));
                            });
        _bitsLabels.forEach(l ->
                            {
                                l.setText(" ");
                                l.setFont(new Font("Arial", Font.PLAIN, 20));
                            });

        // endregion

        // region set up wordle grid

        setupWordleGrid();

        // endregion

        // region set up table

        initEntropyList(20, Entropy.GetInfoMap());

        // endregion
    }

    public static void main(String[] args)
    {
        new WindowHacker();
    }

    private void setupWordleGrid()
    {
        panelWordleGrid.setLayout(new GridLayout(Wordle.MAX_TRIES, Wordle.WORD_LENGTH));

        for (int i = 0; i < Wordle.MAX_TRIES; i++)
        {
            ArrayList<JTextField> row = new ArrayList<>();
            for (int j = 0; j < Wordle.WORD_LENGTH; j++)
            {
                JTextField textField = new JTextField("gg", 1);
                textField.setText("");
                textField.setEditable(i == _currentRow);
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.BOLD, 40));
                panelWordleGrid.add(textField);
                row.add(textField);

                int copyOfj = j;
                int copyOfi = i;

                textField.setDocument(new PlainDocument()
                {

                    @Override
                    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
                    {
                        if (str == null) return;

                        if (getLength() + str.length() <= 1)
                        {
                            if (str.matches("[a-zA-Z]"))
                            {
                                super.insertString(offset, str.toUpperCase(), attr);
                            }
                        }

                        String currentText = getText(0, getLength());

                        if (currentText.length() == 1)
                        {
                            int nextJ = copyOfj + 1 < Wordle.WORD_LENGTH ? copyOfj + 1 : copyOfj;

                            _textFields.get(copyOfi).get(nextJ).requestFocus();
                        }
                    }

                    @Override
                    public void remove(int offs, int len) throws BadLocationException
                    {
                        super.remove(offs, len);

                        String currentText = getText(0, getLength());

                        if (currentText.length() == 0)
                        {
                            int previousJ = Math.max(copyOfj - 1, 0);

                            _textFields.get(copyOfi).get(previousJ).requestFocus();
                        }
                    }
                });

                textField.addMouseListener(new java.awt.event.MouseAdapter()
                {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt)
                    {
                        if (evt.getButton() != java.awt.event.MouseEvent.BUTTON3)
                        {
                            return;
                        }

                        RightClickMenu.setTextField(textField);
                        RightClickMenu.getInstance().show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                });
            }
            _textFields.add(row);
        }
    }


    private void initEntropyList(int num, HashMap<String, List<Double>> infoMap)
    {
        clearEntropyList();

        panelList.setLayout(new BorderLayout());

        HashMap<String, Double> entropyMap2 = new HashMap<>();

        // calc entropy for each word
        for (String word : infoMap.keySet())
        {
            double entropy = 0;
            // calc entropy for each letter
            for (Double info : infoMap.get(word))
            {
                // add to list
                entropy += info;
            }

            entropyMap2.put(word, entropy);
        }

        List<Map.Entry<String, Double>> list = new LinkedList<>(entropyMap2.entrySet());

        // Sort the list
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Double> aa : list)
        {
            temp.put(aa.getKey(), aa.getValue());
        }

        List<String> entropyMap3 = new ArrayList<>(temp.keySet());

        String[] columnNames = {"Top Pick", "E [info]"};
        Object[][] data = new Object[num][2];
        for (int i = 0; i < Math.min(num, entropyMap3.size()); i++)
        {
            String word = entropyMap3.get(i);
            double entropy = entropyMap2.get(word);

            data[i][0] = word;
            data[i][1] = entropy;
        }

        tableEntropy = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane sp = new JScrollPane(tableEntropy);
        panelList.add(sp);

        tableEntropy.setRowHeight(30);
        tableEntropy.setFont(new Font("Arial", Font.BOLD, 20));
        tableEntropy.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));

        setCurrentWordList(new ArrayList<>(entropyMap2.keySet()));

        _possLabels.get(_currentRow).setText(entropyMap3.size() + " possible words");
    }

    private void clearEntropyList()
    {
        panelList.removeAll();
        panelList.revalidate();
        panelList.repaint();
    }

    private void setCurrentWordList(ArrayList<String> currentWordList)
    {
        boolean isStart = _currentWordList == null || _currentWordList.size() == 0;

        if (!isStart)
        {
            Log.Println("Reduce : " + currentWordList.size() + "/" + _currentWordList.size());
        }

        _currentWordList = currentWordList;
    }

    private void onButtonSubmitClicked()
    {
        String word = "";
        List<JTextField> row = _textFields.get(_currentRow);
        List<GuessPattern.GuessStatus> pattern = new ArrayList<>();

        for (JTextField textField : row)
        {
            word += textField.getText();

            Color background = textField.getBackground();

            GuessPattern.GuessStatus status;

            if (Color.GRAY.equals(background))
            {
                status = GuessPattern.GuessStatus.NOT_IN_THE_ANSWER;
            }
            else if (Color.GREEN.equals(background))
            {
                status = GuessPattern.GuessStatus.RIGHT_PLACE;
            }
            else if (Color.YELLOW.equals(background))
            {
                status = GuessPattern.GuessStatus.MIS_PLACE;
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Please set all the letters to either green, yellow or gray");
                return;
            }

            pattern.add(status);
        }

        if (word.length() != Wordle.WORD_LENGTH)
        {
            JOptionPane.showMessageDialog(null, "Please fill all the letters");
            return;
        }

        if (!WordleSystem.IsPossibleWord(word))
        {
            JOptionPane.showMessageDialog(null, "Please fill a possible word");
            return;
        }

        Log.Println(word);
        Log.Println(pattern.toString());

        List<String> newWordList = GuessPattern.GetWordListMatchPattern(word, _currentWordList, pattern);

        HashMap<String, List<Double>> newInfoMap = Entropy.CalcInfoMap(newWordList);


        _bitsLabels.get(_currentRow).setText(MathHelper.Log2(newInfoMap.size() / _currentWordList.size()) + " bits");

        _textFields.get(_currentRow).forEach(textField -> textField.setEditable(false));

        _currentRow++;

        initEntropyList(20, newInfoMap);

        _textFields.get(_currentRow).forEach(textField -> textField.setEditable(true));

        _textFields.get(_currentRow).get(0).requestFocus();

    }

    private void onButtonResetClicked()
    {
        _currentRow = 0;

        _textFields.forEach(row -> row.forEach(textField ->
                                               {
                                                   textField.setText("");
                                                   textField.setBackground(Color.WHITE);
                                                   textField.setEditable(false);
                                               }));

        _textFields.get(0).forEach(textField -> textField.setEditable(true));

        _possLabels.forEach(label -> label.setText(" "));
        _bitsLabels.forEach(label -> label.setText(" "));

        initEntropyList(20, Entropy.GetInfoMap());
    }


    private static class RightClickMenu extends PopupMenu
    {
        private static RightClickMenu _instance;

        public static RightClickMenu getInstance()
        {
            if (_instance == null)
            {
                _instance = new RightClickMenu();
            }

            return _instance;
        }

        private static JTextField _textField;

        public RightClickMenu()
        {
            initMenuItems();
        }

        public static void setTextField(JTextField textField)
        {
            _textField = textField;
        }


        private void initMenuItems()
        {
            MenuItem itemGrey = new MenuItem("Grey");
            MenuItem itemYellow = new MenuItem("Yellow");
            MenuItem itemGreen = new MenuItem("Green");


            itemGrey.addActionListener(e ->
                                       {
                                           setTextFieldColor(Color.GRAY);
                                       });

            itemYellow.addActionListener(e ->
                                         {
                                             setTextFieldColor(Color.YELLOW);
                                         });

            itemGreen.addActionListener(e ->
                                        {
                                            setTextFieldColor(Color.GREEN);
                                        });

            add(itemGrey);
            add(itemYellow);
            add(itemGreen);
        }

        private void setTextFieldColor(Color color)
        {
            if (_textField == null) return;

            if (!_textField.isEditable()) return;

            _textField.setBackground(color);
        }
    }
}
