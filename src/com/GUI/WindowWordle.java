package com.GUI;

import com.Hacker.Entropy;
import com.Hacker.GuessPattern;
import com.Helper.Log;
import com.Wordle.Wordle;
import com.Wordle.WordleSystem;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WindowWordle extends JFrame {

    private JPanel panelMain;
    private JButton submitButton;
    private JButton wordleHackerButton;
    private JPanel panelContent;
    private JButton resetButton;

    private ArrayList<ArrayList<JTextField>> textFields = new ArrayList<>();

    public WindowWordle() {
        initComponents();
    }

    private void initComponents() {
        setContentPane(panelMain);
        setTitle("Wordle");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        panelContent.setLayout(new GridLayout(Wordle.MAX_TRIES,Wordle.WORD_LENGTH));

        setUpWordleList();

        setSize(600,800);
        setVisible(true);

        // region button listeners
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnSubmitButtonClicked();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnResetButtonClicked();
            }
        });
        wordleHackerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnWordleHackerButtonClicked();
            }
        });
        // endregion
    }

    private void setUpWordleList()
    {
        for (int i = 0; i < Wordle.MAX_TRIES; i++) {
            ArrayList<JTextField> row = new ArrayList<>();
            for (int j = 0; j < Wordle.WORD_LENGTH; j++) {
                JTextField textField = new JTextField("gg",1);
                textField.setText("");
                textField.setEditable(i==0);
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.BOLD, 40));
                panelContent.add(textField);
                row.add(textField);

                int copyOfj = j;
                int copyOfi = i;

                textField.setDocument(new PlainDocument() {

                    @Override
                    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                        if (str == null) return;

                        if (getLength() + str.length() <= 1) {
                            if (str.matches("[a-zA-Z]")) {
                                super.insertString(offset, str.toUpperCase(), attr);
                            }
                        }

                        String currentText = getText(0, getLength());

                        if(currentText.length() == 1)
                        {
                            int nextJ = copyOfj + 1 < Wordle.WORD_LENGTH ? copyOfj + 1 : copyOfj;

                            textFields.get(copyOfi).get(nextJ).requestFocus();
                        }
                    }

                    @Override
                    public void remove(int offs, int len) throws BadLocationException {
                        super.remove(offs, len);

                        String currentText = getText(0, getLength());

                        if(currentText.length() == 0)
                        {
                            int previousJ = Math.max(copyOfj - 1, 0);

                            textFields.get(copyOfi).get(previousJ).requestFocus();
                        }
                    }
                });
            }
            textFields.add(row);
        }
    }

    private void OnSubmitButtonClicked()
    {
        Log.Println("Submit button clicked");

        Submit();
    }

    private void OnResetButtonClicked()
    {
        Log.Println("Reset button clicked");

        Reset();

    }

    private void OnWordleHackerButtonClicked()
    {
        Log.Println("Wordle Hacker button clicked");

        new WindowHacker();
    }

    private void Reset() {
        WordleSystem.Reset();

        for (ArrayList<JTextField> row : textFields) {
            for (JTextField textField : row) {
                textField.setText("");
                textField.setEditable(false);
                textField.setBackground(Color.white);
            }
        }

        ArrayList<JTextField> firstRow = textFields.get(0);

        for (JTextField textField : firstRow) {
            textField.setEditable(true);
        }
    }

    private void Submit() {
        String guess = "";

        Wordle w = WordleSystem.Instance();

        var currentRow = WordleSystem.Instance().Tries;

        ArrayList<JTextField> row = textFields.get(currentRow);

        for (JTextField textField : row) {
            guess += textField.getText();
        }

        boolean isPossibleWord = WordleSystem.IsPossibleWord(guess);

        if(!isPossibleWord)
        {
            Log.Warning("Word is not possible");
            JOptionPane.showMessageDialog(this, "The word you entered is not a possible word");
            row.get(row.size()-1).requestFocus();
            return;
        }

        GuessPattern.GuessStatus[] guessStatus = WordleSystem.Guess(w, guess);

        for (int i = 0; i < guessStatus.length; i++) {
            switch (guessStatus[i])
            {
                case RIGHT_PLACE:
                    textFields.get(currentRow).get(i).setBackground(Color.green);
                    break;
                case MIS_PLACE:
                    textFields.get(currentRow).get(i).setBackground(Color.yellow);
                    break;
                case NOT_IN_THE_ANSWER:
                default:
                    textFields.get(currentRow).get(i).setBackground(Color.gray);
                    break;
            }
        }

        boolean success =  WordleSystem.CheckGuess(w,guess);

        if(success)
        {
            Log.Println("Word is correct");
            JOptionPane.showMessageDialog(this, "You won!");
            return;
        }

        if(w.Tries == Wordle.MAX_TRIES)
        {
            Log.Error("You lost");
            JOptionPane.showMessageDialog(this, "You lost!");
            return;
        }

        for (JTextField textField : row) {
            textField.setEditable(false);
        }

        ArrayList<JTextField> nextRow = textFields.get(currentRow + 1);

        for (JTextField textField : nextRow) {
            textField.setEditable(true);
        }

        nextRow.get(0).requestFocus();
    }

    public static void main(String[] args) {
        GuessPattern.LoadPossiblePatterns();
        GuessPattern.LoadPattenMatrix(false);
        Entropy.LoadInfoMap(false);
        new WindowWordle();
    }
}
