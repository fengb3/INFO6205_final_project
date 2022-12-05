package com.UI;

import com.Helper.Log;
import com.Wordle.Hacker.Pattern;
import com.Wordle.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;

public class WindowWordle extends JFrame
{
    private JButton submitButton;
    private JButton wordleHackerButton;
    private JButton resetButton;
    private JPanel panelWordleGridContainer;
    private JPanel panelMain;
    private WordleGrid wordleGrid;

    private WindowHacker _windowHacker;

    private int currentWordId;

    public WindowWordle()
    {
        this.setTitle("Wordle");
        setContentPane(panelMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        wordleGrid = new WordleGrid();
        panelWordleGridContainer.setLayout(new GridLayout(1, 1));
        panelWordleGridContainer.add(wordleGrid);

        pack();
        setVisible(true);
        setResizable(false);

        reset();

        submitButton.addActionListener(e ->submit());

        resetButton.addActionListener(e -> reset());

        wordleHackerButton.addActionListener(e -> runHacker());

    }

    private void reset()
    {
        wordleGrid.clear();
        wordleGrid.activeRow(0);

        currentWordId = Word.getRandomWordId();
        Log.Println("Current word" + currentWordId + "-" + Word.getWord(currentWordId));

        if(_windowHacker != null)
        {
            _windowHacker.reset();
        }
    }

    private void submit()
    {
        String guess = wordleGrid.getCurrentRowText();

        guess = guess.toLowerCase();

        if(!Word.isValidWord(guess))
        {
            Log.Println("Invalid word");
            return;
        }

        int guessWordId = Word.getId(guess);
        short patternId = Pattern.checkPatternIdByLookUp(currentWordId, guessWordId);

        int[] pattern = Pattern.getPatternByPatternId(patternId);

        wordleGrid.setCurrentRowPattern(pattern);
        wordleGrid.activeNextRow();
        Log.Println("Guess: " + guess);

        if(_windowHacker != null)
        {
            _windowHacker.getWordleGrid().setCurrentRowTextAndPattern(guess,pattern);
            _windowHacker.submit();
        }
    }

    private void runHacker()
    {
        reset();
        _windowHacker = new WindowHacker();
    }


    public static void main(String[] args)
    {
        new WindowWordle();
    }
}
