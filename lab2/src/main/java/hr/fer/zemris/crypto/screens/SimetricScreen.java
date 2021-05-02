package hr.fer.zemris.crypto.screens;

import hr.fer.zemris.crypto.actions.ReadFile;
import hr.fer.zemris.crypto.actions.SaveFile;
import hr.fer.zemris.crypto.domain.enums.EncodingMode;
import hr.fer.zemris.crypto.domain.enums.SimetricAlgorithm;

import javax.swing.*;
import java.awt.*;

import static hr.fer.zemris.crypto.domain.CLogic.decrypt;
import static hr.fer.zemris.crypto.domain.CLogic.encrypt;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static java.util.Objects.requireNonNull;

public class SimetricScreen extends JPanel {

    private final JTextArea ivTextField;
    private final JTextArea keyTextField;
    private final JButton keyLoadButton;
    private final JButton ivLoadButton;
    private final JTextArea inTextArea;
    private final JTextArea outTextArea;
    private final JButton encryptButton;
    private final JButton decryptButton;
    private final JComboBox<SimetricAlgorithm> algorithmComboBox;
    private final JComboBox<EncodingMode> encodingModeComboBox;

    public SimetricScreen() {
        setBorder(BorderFactory.createLineBorder(GRAY, 2));

        keyTextField = new JTextArea("12345678901234567890123456789012", 1, 90);
        keyLoadButton = new JButton(new ReadFile("Load key", this, keyTextField));
        ivTextField = new JTextArea("1234567890123456", 1, 90);
        ivLoadButton = new JButton(new ReadFile("Load iv ", this, ivTextField));
        inTextArea = new JTextArea("Sample text", 15, 100);
        outTextArea = new JTextArea("Encrypted text", 15, 100);
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        algorithmComboBox = new JComboBox<>(SimetricAlgorithm.values());
        encodingModeComboBox = new JComboBox<>(EncodingMode.values());

        initGui();
        initActions();
    }

    private void initActions() {
        encryptButton.addActionListener(e -> outTextArea.setText(encrypt(inTextArea.getText(), keyTextField.getText(), ivTextField.getText(), (SimetricAlgorithm) requireNonNull(algorithmComboBox.getSelectedItem()), (EncodingMode) requireNonNull(encodingModeComboBox.getSelectedItem()))));

        decryptButton.addActionListener(e -> inTextArea.setText(decrypt(outTextArea.getText(), keyTextField.getText(), ivTextField.getText(), (SimetricAlgorithm) requireNonNull(algorithmComboBox.getSelectedItem()), (EncodingMode) requireNonNull(encodingModeComboBox.getSelectedItem()))));

        ivTextField.setEnabled(false);
        encodingModeComboBox.addActionListener(e -> ivTextField.setEnabled(encodingModeComboBox.getSelectedItem() != EncodingMode.ECB));
    }

    private void initGui() {
        setLayout(new BorderLayout());

        //top
        JPanel keyPanel = new JPanel(new GridLayout(3, 1));
        JPanel upperTop = new JPanel();
        JPanel centerTop = new JPanel();
        JPanel lowerTop = new JPanel();
        upperTop.add(new JLabel("Simetric key:"));
        upperTop.add(keyTextField);
        upperTop.add(keyLoadButton);
        centerTop.add(new JLabel("Init vector: "));
        centerTop.add(ivTextField);
        centerTop.add(ivLoadButton);
        lowerTop.add(algorithmComboBox);
        lowerTop.add(encodingModeComboBox);
        keyPanel.add(upperTop);
        keyPanel.add(centerTop);
        keyPanel.add(lowerTop);
        add(keyPanel, PAGE_START);

        //center
        JPanel center = new JPanel(new GridLayout(2, 1));
        JPanel upperCenter = new JPanel();
        JPanel upperCenter2 = new JPanel(new GridLayout(2, 1));
        JPanel lowerCenter = new JPanel();
        JPanel lowerCenter2 = new JPanel(new GridLayout(2, 1));
        upperCenter.add(new JLabel("Decrypted text"));
        upperCenter.add(new JScrollPane(inTextArea));
        upperCenter2.add(new JButton(new ReadFile("Load file", this, inTextArea)));
        upperCenter2.add(new JButton(new SaveFile("Save file", this, inTextArea)));
        upperCenter.add(upperCenter2);
        lowerCenter.add(new JLabel("Encrypted text"));
        lowerCenter.add(new JScrollPane(outTextArea));
        lowerCenter2.add(new JButton(new ReadFile("Load file", this, outTextArea)));
        lowerCenter2.add(new JButton(new SaveFile("Save file", this, outTextArea)));
        lowerCenter.add(lowerCenter2);
        center.add(upperCenter);
        center.add(lowerCenter);
        add(center, CENTER);


        //bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        add(buttonPanel, PAGE_END);
    }
}
