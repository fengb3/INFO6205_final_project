package com.UI;

import com.Wordle.Hacker.GuessStatus;

import java.awt.*;

public class RightClickMenu extends PopupMenu
{
    private static RightClickMenu instance = null;

    public static RightClickMenu getInstance()
    {
        if (instance == null)
        {
            instance = new RightClickMenu();
        }
        return instance;
    }

    private static WordleGrid.WordleGridTextField _textField;

    public RightClickMenu()
    {
        MenuItem itemGrey = new MenuItem("Grey");
        MenuItem itemYellow = new MenuItem("Yellow");
        MenuItem itemGreen = new MenuItem("Green");


        itemGrey.addActionListener(e -> setTextFieldGuessStatus(GuessStatus.NOT_IN_THE_ANSWER.code));

        itemYellow.addActionListener(e -> setTextFieldGuessStatus(GuessStatus.MIS_PLACE.code));

        itemGreen.addActionListener(e -> setTextFieldGuessStatus(GuessStatus.RIGHT_PLACE.code));

        add(itemGrey);
        add(itemYellow);
        add(itemGreen);
    }

    public static void setTextField(WordleGrid.WordleGridTextField textField)
    {
        _textField = textField;
    }

    private void setTextFieldGuessStatus(int status)
    {
        if (_textField == null) return;

        if (!_textField.isEditable()) return;

        _textField.setCurrentGuessStatus(status);
    }
}
