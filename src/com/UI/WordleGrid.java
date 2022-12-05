package com.UI;

import com.Helper.Log;
import com.Wordle.Const;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class WordleGrid extends JPanel
{
    HashMap<String, WordleGridTextField> _textFields;

    private int _currentRow = 0;

    public WordleGrid()
    {
        super();
        setWordleGrid();
        setSize(500, 550);
        setMinimumSize(new Dimension(500, 600));
        setMaximumSize(new Dimension(500, 600));
    }

    public void setWordleGrid()
    {
        int width = Const.WORD_LENGTH;
        int height = Const.WORD_TRIES;

        _textFields = new HashMap<>();

        setLayout(new GridLayout(height, width));

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                WordleGridTextField textField = new WordleGridTextField(row, col);
                textField.setEditable(false);
//                textField.setText(row + "," + col);
                add(textField);
                _textFields.put(row + "_" + col, textField);
            }
        }

        setDocumentListener();
    }

    public void enableRow(int row, boolean enabled)
    {
        if(row < 0 || row > Const.WORD_TRIES)
        {
            return;
        }

        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            String key = row + "_" + i;
            JTextField textField = _textFields.get(key);
            textField.setEditable(enabled);
        }
    }

    public void activeRow(int row)
    {
        enableRow(_currentRow, false);
        enableRow(row, true);
        _currentRow = row;
        _textFields.get(row + "_0").requestFocus();
    }

    public void clear()
    {
        int width = Const.WORD_LENGTH;
        int height = Const.WORD_TRIES;

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                String key = row + "_" + col;
                WordleGridTextField textField = _textFields.get(key);
                textField.setText("");
                textField.setCurrentGuessStatus(3);
            }
        }
    }

    public String getCurrentRowText()
    {
        String text = "";
        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            String key = _currentRow + "_" + i;
            JTextField textField = _textFields.get(key);
            text += textField.getText();
        }
        return text;
    }

    public void setCurrentRowTextAndPattern(String text, int[] pattern)
    {
        if(text.length() != Const.WORD_LENGTH)
        {
            Log.Error("WordleGrid.setCurrentRowTextAndPattern: text length is not " + Const.WORD_LENGTH);
            return;
        }

        if(pattern.length != Const.WORD_LENGTH)
        {
            Log.Error("WordleGrid.setCurrentRowTextAndPattern: pattern length is not " + Const.WORD_LENGTH);
            return;
        }

        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            String key = _currentRow + "_" + i;
            WordleGridTextField textField = _textFields.get(key);
            textField.setText(text.substring(i, i + 1));
            textField.setCurrentGuessStatus(pattern[i]);
        }
    }

    public void activeNextRow()
    {
        activeRow(_currentRow + 1 < Const.WORD_TRIES ? _currentRow + 1 : _currentRow);
    }

    private void setDocumentListener()
    {
        for (int row = 0; row < Const.WORD_TRIES; row++)
        {
            for (int col = 0; col < Const.WORD_LENGTH; col++)
            {
                String key = row + "_" + col;
                WordleGridTextField textField = _textFields.get(key);
                WordleGridTextField prev = null;
                WordleGridTextField next = null;

                if (col > 0)
                {
                    prev = _textFields.get(row + "_" + (col - 1));
                }
                if (col < Const.WORD_LENGTH - 1)
                {
                    next = _textFields.get(row + "_" + (col + 1));
                }

                textField.setDocument(new GridPlainDocument(row,col,prev,next));
            }
        }
    }

    public void setRowColor(int row, int[] pattern)
    {
        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            String key = row + "_" + i;
            WordleGridTextField textField = _textFields.get(key);
            textField.setCurrentGuessStatus(pattern[i]);
        }
    }

    public void setCurrentRowPattern(int[] pattern)
    {
        setRowColor(_currentRow, pattern);
    }

    public int[] getCurrentRowPattern()
    {
        int[] pattern = new int[Const.WORD_LENGTH];
        for (int i = 0; i < Const.WORD_LENGTH; i++)
        {
            String key = _currentRow + "_" + i;
            WordleGridTextField textField = _textFields.get(key);
            pattern[i] = textField.getCurrentGuessStatus();
        }
        return pattern;
    }

    public void setRightClickEvent()
    {
        add(RightClickMenu.getInstance());
        for (int row = 0; row < Const.WORD_TRIES; row++)
        {
            for (int col = 0; col < Const.WORD_LENGTH; col++)
            {
                String key = row + "_" + col;
                WordleGridTextField textField = _textFields.get(key);
                textField.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        if(e.getButton() != MouseEvent.BUTTON3)
                        {
                            return;
                        }

                        RightClickMenu.getInstance().setTextField(textField);
                        RightClickMenu.getInstance().show(e.getComponent(), e.getX(), e.getY());
                    }
                });
            }
        }
    }

    public int getCurrentRow()
    {
        return _currentRow;
    }

    public class GridPlainDocument extends PlainDocument
    {
        private int _row;
        private int _col;

        WordleGridTextField _prev;
        WordleGridTextField _next;

        public GridPlainDocument(int row, int col, WordleGridTextField prev, WordleGridTextField next)
        {
            super();
            _row = row;
            _col = col;
            _prev = prev;
            _next = next;
        }

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

            if (currentText.length() != 1)
            {
                return;
            }

            if (_next == null)
            {
                return;
            }

            _next.requestFocus();
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException
        {
            super.remove(offs, len);

            String currentText = getText(0, getLength());

            if (currentText.length() != 0)
            {
                return;
            }

            if (_prev == null)
            {
                return;
            }

            _prev.requestFocus();
        }

    }

    public class WordleGridTextField extends JTextField
    {
        private int _row;
        private int _column;

        private int _currentGuessStatus = -1;

        public WordleGridTextField(int row, int column)
        {
            super();
            _row = row;
            _column = column;
            setEditable(false);
            setHorizontalAlignment(JTextField.CENTER);
            setFont(new Font("Arial", Font.BOLD, 20));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        public int getRow()
        {
            return _row;
        }

        public void setRow(int _row)
        {
            this._row = _row;
        }

        public int getColumn()
        {
            return _column;
        }

        public void setColumn(int _column)
        {
            this._column = _column;
        }

        public int getCurrentGuessStatus()
        {
            return _currentGuessStatus;
        }

        public void setCurrentGuessStatus(int currentGuessStatus)
        {
            setBackground(Const.COLORS[currentGuessStatus]);
            this._currentGuessStatus = currentGuessStatus;
        }
    }

}

