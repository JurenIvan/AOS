package hr.fer.zemris.crypto.screens;

import hr.fer.zemris.crypto.actions.ReadFile;
import hr.fer.zemris.crypto.actions.SaveFile;
import hr.fer.zemris.crypto.domain.CLogic;
import hr.fer.zemris.crypto.domain.enums.HashAlgorithms;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.*;

public class HashScreen extends JPanel {

    private final JTextArea inTextArea;
    private final JTextArea outTextArea;
    private final JButton calculateHash;
    private final JComboBox<HashAlgorithms> hashAlgorithm;

    public HashScreen() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        inTextArea = new JTextArea("Sample text", 15, 100);
        outTextArea = new JTextArea("Digest", 15, 100);
        outTextArea.setEnabled(false);

        calculateHash = new JButton("Digest");
        hashAlgorithm = new JComboBox<>(HashAlgorithms.values());
        initGui();
        initActions();
    }

    private void initActions() {
        calculateHash.addActionListener(e -> outTextArea.setText(CLogic.digest(inTextArea.getText(), (HashAlgorithms) hashAlgorithm.getSelectedItem())));
    }

    private void initGui() {
        setLayout(new BorderLayout());

        //top
        JPanel upper = new JPanel();
        upper.add(hashAlgorithm);
        add(upper, PAGE_START);

        //center
        JPanel center = new JPanel(new GridLayout(2, 1));
        JPanel upperCenter = new JPanel();
        JPanel upperCenter2 = new JPanel(new GridLayout(1, 1));
        JPanel lowerCenter = new JPanel();
        JPanel lowerCenter2 = new JPanel(new GridLayout(1, 1));
        upperCenter.add(new JLabel("Input text"));
        upperCenter.add(new JScrollPane(inTextArea));
        upperCenter2.add(new JButton(new ReadFile("Load file", this, inTextArea)));
        upperCenter.add(upperCenter2);
        lowerCenter.add(new JLabel("Hashed text"));
        lowerCenter.add(new JScrollPane(outTextArea));
        lowerCenter2.add(new JButton(new SaveFile("Save hash", this, outTextArea)));
        lowerCenter.add(lowerCenter2);
        center.add(upperCenter);
        center.add(lowerCenter);
        add(center, CENTER);

        //bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateHash);
        add(buttonPanel, PAGE_END);
    }
}
