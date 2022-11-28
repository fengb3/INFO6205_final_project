package com.GUI;

import javax.swing.*;

public class ListItemEntropy extends JPanel
{
    private JLabel labelWord;
    private JLabel labelEntropy;

    public ListItemEntropy(String word, String entropy)
    {
        initComponents(word, entropy);
    }

    private void initComponents(String word, String entropy)
    {
        labelWord.setText(word);
        labelEntropy.setText(entropy);

        add(labelWord);
        add(labelEntropy);

        setVisible(true);
    }
}
