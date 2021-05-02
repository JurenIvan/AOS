package hr.fer.zemris.crypto.screens;

import hr.fer.zemris.crypto.actions.ReadFile;
import hr.fer.zemris.crypto.actions.SaveFile;
import hr.fer.zemris.crypto.domain.CLogic;
import hr.fer.zemris.crypto.domain.Utils;
import hr.fer.zemris.crypto.domain.enums.EncodingMode;
import hr.fer.zemris.crypto.domain.enums.HashAlgorithms;
import hr.fer.zemris.crypto.domain.enums.SimetricAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

import static java.awt.BorderLayout.*;
import static java.util.Objects.requireNonNull;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class ReceiveEnvelopeScreen extends JPanel {

    private static final String PRIVATE_KEY_CONST = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIPpbPRFD60utrIw0gxl3tsSLXPkZK1psQGn87fl29io6w8b3JQj07XyA/LYkeJgce3nIUqjQcf4Dx0XIIvF9CHRqXi2S5xSrRBDTbqpqi86OK4OmHq2kQVma+UCVXoCTAOkQukeSUdohXo7xrqZHNHy2vHDaqlEIz2yEwbw0owpAgMBAAECgYAaVU/914eQoAGywtI7zV5Wx2MxfoemorHGTdv2ezmPH/GE26AGJrzN1pl5Mki1M2GrB3f66WppXBjQStyhaowYpm+AGxEFm4RgtNEDdt6k0Pes9gODaEedIps9ff3+VSYSwu9IKnS1dcEdTAUCmrIm4NUJxay7pKFmVLDGdyaAAQJBAMzbDL4fMh5ZTM2IZDeCZULznrvgDLC3X0gVPL+FnkXcK/W+YSlitirBSukKY4b7qyz9HlZqlowCD0HRj45sfCkCQQCk2E/wFvSxFs6ICF65pwquzVkZJNgpDS5mpvW5Q2q7UEg2qqp6h1qoptUxdxQ2/8wjG1B3iYcZUeepWewp95ABAkEAxegxjVNyBePb0QydVQtCbMYTagnv+KGPCYKK57Rczb0BMy4zi9nzh6apii4hLPzhyFrY/j+HJcevmbSs7blc0QJANS7022kMmPXavFi29v7Fm4/05v/UHap7BfNn49W0YaqNIdX+GnoA7dilf8gDZZsxqUHuvCJKzAh4zVKNtxyAAQJBAIHEwS9L+xESUEEA1y0XYjTH0mWLUOkxdZjfpjiI4vneYBLf0Rg6fGLQWjNJUGUK4RH8njeAIH4pHb3xKfzCRQQ=";
    private static final String PUBLIC_KEY_CONST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCD6Wz0RQ+tLrayMNIMZd7bEi1z5GStabEBp/O35dvYqOsPG9yUI9O18gPy2JHiYHHt5yFKo0HH+A8dFyCLxfQh0al4tkucUq0QQ026qaovOjiuDph6tpEFZmvlAlV6AkwDpELpHklHaIV6O8a6mRzR8trxw2qpRCM9shMG8NKMKQIDAQAB";

    private final JTextArea privateKeyTextField;
    private final JTextArea publicKeyTextField;
    private final JButton myPrivateKeyLoadButton;
    private final JButton otherPublicKeyLoadButton;
    private final JTextArea contentTextArea;
    private final JTextArea inTextArea;
    private final JTextArea inRsaIVTextArea;
    private final JTextArea inRsaKeyTextArea;
    private final JTextArea inHashTextArea;
    private final JButton encryptButton;
    private final JComboBox<SimetricAlgorithm> algorithmComboBox;
    private final JComboBox<EncodingMode> encodingModeComboBox;
    private final JComboBox<HashAlgorithms> hashAlgorithm;


    public ReceiveEnvelopeScreen() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        privateKeyTextField = new JTextArea(PRIVATE_KEY_CONST, 2, 100);
        myPrivateKeyLoadButton = new JButton(new ReadFile("Load", this, privateKeyTextField));
        publicKeyTextField = new JTextArea(PUBLIC_KEY_CONST, 2, 100);
        otherPublicKeyLoadButton = new JButton(new ReadFile("Load", this, publicKeyTextField));
        contentTextArea = new JTextArea("Sample text", 5, 100);
        inTextArea = new JTextArea("Encrypted content", 2, 100);
        inRsaIVTextArea = new JTextArea("Encrypted symmetric iv", 2, 100);
        inRsaKeyTextArea = new JTextArea("Encrypted symmetric key", 2, 100);
        inHashTextArea = new JTextArea("Message hash", 2, 100);
        encryptButton = new JButton("Decode");
        algorithmComboBox = new JComboBox<>(SimetricAlgorithm.values());
        encodingModeComboBox = new JComboBox<>(EncodingMode.values());
        hashAlgorithm = new JComboBox<>(HashAlgorithms.values());

        contentTextArea.setEnabled(false);

        initGui();
        initActions();
    }

    private void initActions() {
        encryptButton.addActionListener(e -> {
            var decodedSymmetricKey = CLogic.decrypt(inRsaKeyTextArea.getText(), privateKeyTextField.getText());
            var decodedSymmetricIV = CLogic.decrypt(inRsaIVTextArea.getText(), privateKeyTextField.getText());
            var decodedContent = CLogic.decrypt(inTextArea.getText(), decodedSymmetricKey, decodedSymmetricIV, (SimetricAlgorithm) requireNonNull(algorithmComboBox.getSelectedItem()), (EncodingMode) requireNonNull(encodingModeComboBox.getSelectedItem()));
            var decodedHash = CLogic.decrypt(Base64.getDecoder().decode(inHashTextArea.getText()), Utils.getPublicKey(publicKeyTextField.getText()));
            var calculatedHash = CLogic.digest(decodedContent, (HashAlgorithms) hashAlgorithm.getSelectedItem());

            if (!calculatedHash.equals(decodedHash)) {
                JOptionPane.showMessageDialog(null, "Hash is not ok!", "PROBLEM", ERROR_MESSAGE);
            }

            contentTextArea.setText(decodedContent);
        });
    }

    private void initGui() {
        setLayout(new BorderLayout());

        //top
        JPanel keyPanel = new JPanel(new GridLayout(3, 1));
        JPanel upperTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel centerTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel settingsTop = new JPanel();
        upperTop.add(new JLabel("Receiver private key:"));
        upperTop.add(new JScrollPane(privateKeyTextField));
        upperTop.add(myPrivateKeyLoadButton);
        centerTop.add(new JLabel("Sender public key: "));
        centerTop.add(new JScrollPane(publicKeyTextField));
        centerTop.add(otherPublicKeyLoadButton);
        settingsTop.add(algorithmComboBox);
        settingsTop.add(encodingModeComboBox);
        settingsTop.add(hashAlgorithm);
        keyPanel.add(upperTop);
        keyPanel.add(centerTop);
        keyPanel.add(settingsTop);
        add(keyPanel, PAGE_START);

        //center
        JPanel center = new JPanel(new GridLayout(2, 1));
        JPanel upperCenter = new JPanel();
        JPanel upperCenter2 = new JPanel(new GridLayout(2, 1));
        upperCenter.add(new JLabel("Text to encode"));
        upperCenter.add(new JScrollPane(contentTextArea));
        upperCenter2.add(new JButton(new ReadFile("Load file", this, contentTextArea)));
        upperCenter2.add(new JButton(new SaveFile("Save file", this, contentTextArea)));
        upperCenter.add(upperCenter2);

        JPanel lowerCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lowerCenter.add(new JLabel("Encrypted content"));
        lowerCenter.add(new JScrollPane(inTextArea));
        lowerCenter.add(new JButton(new ReadFile("Load", this, inTextArea)));
        lowerCenter.add(new JLabel("Encrypted symmetric key"));
        lowerCenter.add(new JScrollPane(inRsaKeyTextArea));
        lowerCenter.add(new JButton(new ReadFile("Load", this, inRsaKeyTextArea)));
        lowerCenter.add(new JLabel("Encrypted symmetric iv"));
        lowerCenter.add(new JScrollPane(inRsaIVTextArea));
        lowerCenter.add(new JButton(new ReadFile("Load", this, inRsaIVTextArea)));
        lowerCenter.add(new JLabel("Signed hash"));
        lowerCenter.add(new JScrollPane(inHashTextArea));
        lowerCenter.add(new JButton(new ReadFile("Load", this, inHashTextArea)));
        center.add(lowerCenter);
        center.add(upperCenter);
        add(center, CENTER);

        //bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        add(buttonPanel, PAGE_END);
    }
}
