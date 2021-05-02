package hr.fer.zemris.crypto.screens;

import hr.fer.zemris.crypto.actions.CopyToClipboard;
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

import static hr.fer.zemris.crypto.domain.CLogic.encrypt;
import static hr.fer.zemris.crypto.domain.Utils.getPrivateKey;
import static java.awt.BorderLayout.*;
import static java.util.Objects.requireNonNull;

public class SendEnvelopeScreen extends JPanel {

    private static final String PRIVATE_KEY_CONST = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIPpbPRFD60utrIw0gxl3tsSLXPkZK1psQGn87fl29io6w8b3JQj07XyA/LYkeJgce3nIUqjQcf4Dx0XIIvF9CHRqXi2S5xSrRBDTbqpqi86OK4OmHq2kQVma+UCVXoCTAOkQukeSUdohXo7xrqZHNHy2vHDaqlEIz2yEwbw0owpAgMBAAECgYAaVU/914eQoAGywtI7zV5Wx2MxfoemorHGTdv2ezmPH/GE26AGJrzN1pl5Mki1M2GrB3f66WppXBjQStyhaowYpm+AGxEFm4RgtNEDdt6k0Pes9gODaEedIps9ff3+VSYSwu9IKnS1dcEdTAUCmrIm4NUJxay7pKFmVLDGdyaAAQJBAMzbDL4fMh5ZTM2IZDeCZULznrvgDLC3X0gVPL+FnkXcK/W+YSlitirBSukKY4b7qyz9HlZqlowCD0HRj45sfCkCQQCk2E/wFvSxFs6ICF65pwquzVkZJNgpDS5mpvW5Q2q7UEg2qqp6h1qoptUxdxQ2/8wjG1B3iYcZUeepWewp95ABAkEAxegxjVNyBePb0QydVQtCbMYTagnv+KGPCYKK57Rczb0BMy4zi9nzh6apii4hLPzhyFrY/j+HJcevmbSs7blc0QJANS7022kMmPXavFi29v7Fm4/05v/UHap7BfNn49W0YaqNIdX+GnoA7dilf8gDZZsxqUHuvCJKzAh4zVKNtxyAAQJBAIHEwS9L+xESUEEA1y0XYjTH0mWLUOkxdZjfpjiI4vneYBLf0Rg6fGLQWjNJUGUK4RH8njeAIH4pHb3xKfzCRQQ=";
    private static final String PUBLIC_KEY_CONST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCD6Wz0RQ+tLrayMNIMZd7bEi1z5GStabEBp/O35dvYqOsPG9yUI9O18gPy2JHiYHHt5yFKo0HH+A8dFyCLxfQh0al4tkucUq0QQ026qaovOjiuDph6tpEFZmvlAlV6AkwDpELpHklHaIV6O8a6mRzR8trxw2qpRCM9shMG8NKMKQIDAQAB";

    private final JTextArea myPrivateKeyTextField;
    private final JTextArea otherPublicKeyTextField;
    private final JTextArea keyTextField;
    private final JButton myPrivateKeyLoadButton;
    private final JButton otherPublicKeyLoadButton;
    private final JButton keyLoadButton;
    private final JTextArea inTextArea;
    private final JTextArea outTextArea;
    private final JTextArea outRsaIVTextArea;
    private final JTextArea outRsaKeyTextArea;
    private final JTextArea outHashTextArea;
    private final JButton encryptButton;
    private final JTextArea ivTextField;
    private final JButton ivLoadButton;
    private final JComboBox<SimetricAlgorithm> algorithmComboBox;
    private final JComboBox<EncodingMode> encodingModeComboBox;
    private final JComboBox<HashAlgorithms> hashAlgorithm;


    public SendEnvelopeScreen() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        keyTextField = new JTextArea("12345678901234567890123456789012", 2, 100);
        keyLoadButton = new JButton(new ReadFile("Load", this, keyTextField));
        myPrivateKeyTextField = new JTextArea(PRIVATE_KEY_CONST, 2, 100);
        myPrivateKeyLoadButton = new JButton(new ReadFile("Load", this, myPrivateKeyTextField));
        otherPublicKeyTextField = new JTextArea(PUBLIC_KEY_CONST, 2, 100);
        otherPublicKeyLoadButton = new JButton(new ReadFile("Load", this, otherPublicKeyTextField));
        ivTextField = new JTextArea("1234567890123456", 2, 100);
        ivLoadButton = new JButton(new ReadFile("Load", this, otherPublicKeyTextField));
        inTextArea = new JTextArea("Sample text", 5, 100);
        outTextArea = new JTextArea("Encrypted content", 2, 100);
        outRsaIVTextArea = new JTextArea("Encrypted symmetric iv", 2, 100);
        outRsaKeyTextArea = new JTextArea("Encrypted symmetric key", 2, 100);
        outHashTextArea = new JTextArea("Message hash", 2, 100);
        encryptButton = new JButton("Encrypt");
        algorithmComboBox = new JComboBox<>(SimetricAlgorithm.values());
        encodingModeComboBox = new JComboBox<>(EncodingMode.values());
        hashAlgorithm = new JComboBox<>(HashAlgorithms.values());

        outTextArea.setEnabled(false);
        outRsaIVTextArea.setEnabled(false);
        outRsaKeyTextArea.setEnabled(false);
        outHashTextArea.setEnabled(false);

        initGui();
        initActions();
    }

    private void initActions() {
        encryptButton.addActionListener(e -> {
            outTextArea.setText(encrypt(inTextArea.getText(), keyTextField.getText(), ivTextField.getText(), (SimetricAlgorithm) requireNonNull(algorithmComboBox.getSelectedItem()), (EncodingMode) requireNonNull(encodingModeComboBox.getSelectedItem())));
            outRsaKeyTextArea.setText(Base64.getEncoder().encodeToString(CLogic.encrypt(inTextArea.getText(), otherPublicKeyTextField.getText())));
            outRsaIVTextArea.setText(Base64.getEncoder().encodeToString(CLogic.encrypt(ivTextField.getText(), otherPublicKeyTextField.getText())));
            outRsaKeyTextArea.setText(Base64.getEncoder().encodeToString(CLogic.encrypt(keyTextField.getText(), otherPublicKeyTextField.getText())));
            var hash = CLogic.digest(inTextArea.getText(), (HashAlgorithms) requireNonNull(hashAlgorithm.getSelectedItem()));
            outHashTextArea.setText(Base64.getEncoder().encodeToString(CLogic.encrypt(hash, getPrivateKey(myPrivateKeyTextField.getText()))));
        });
    }

    private void initGui() {
        setLayout(new BorderLayout());

        //top
        JPanel keyPanel = new JPanel(new GridLayout(5, 1));
        JPanel upperTopTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel upperTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel centerTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel lowerTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel settingsTop = new JPanel();
        upperTopTop.add(new JLabel("Symmetric key:"));
        upperTopTop.add(new JScrollPane(keyTextField));
        upperTopTop.add(keyLoadButton);
        upperTop.add(new JLabel("Sender private key:"));
        upperTop.add(new JScrollPane(myPrivateKeyTextField));
        upperTop.add(myPrivateKeyLoadButton);
        centerTop.add(new JLabel("Receiver public key: "));
        centerTop.add(new JScrollPane(otherPublicKeyTextField));
        centerTop.add(otherPublicKeyLoadButton);
        lowerTop.add(new JLabel("IV key: "));
        lowerTop.add(new JScrollPane(ivTextField));
        lowerTop.add(ivLoadButton);
        settingsTop.add(algorithmComboBox);
        settingsTop.add(encodingModeComboBox);
        settingsTop.add(hashAlgorithm);
        keyPanel.add(upperTopTop);
        keyPanel.add(upperTop);
        keyPanel.add(centerTop);
        keyPanel.add(lowerTop);
        keyPanel.add(settingsTop);
        add(keyPanel, PAGE_START);

        //center
        JPanel center = new JPanel(new GridLayout(2, 1));
        JPanel upperCenter = new JPanel();
        JPanel upperCenter2 = new JPanel(new GridLayout(2, 1));
        upperCenter.add(new JLabel("Text to encode"));
        upperCenter.add(new JScrollPane(inTextArea));
        upperCenter2.add(new JButton(new ReadFile("Load file", this, inTextArea)));
        upperCenter2.add(new JButton(new SaveFile("Save file", this, inTextArea)));
        upperCenter.add(upperCenter2);

        JPanel lowerCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lowerCenter.add(new JLabel("Encrypted content"));
        lowerCenter.add(new JScrollPane(outTextArea));
        lowerCenter.add(new JButton(new CopyToClipboard("Copy", outTextArea)));
        lowerCenter.add(new JLabel("Encrypted symmetric key"));
        lowerCenter.add(new JScrollPane(outRsaKeyTextArea));
        lowerCenter.add(new JButton(new CopyToClipboard("Copy", outRsaKeyTextArea)));
        lowerCenter.add(new JLabel("Encrypted symmetric iv"));
        lowerCenter.add(new JScrollPane(outRsaIVTextArea));
        lowerCenter.add(new JButton(new CopyToClipboard("Copy", outRsaIVTextArea)));
        lowerCenter.add(new JLabel("Signed hash"));
        lowerCenter.add(new JScrollPane(outHashTextArea));
        lowerCenter.add(new JButton(new CopyToClipboard("Copy", outHashTextArea)));
        center.add(upperCenter);
        center.add(lowerCenter);
        add(center, CENTER);

        //bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        add(buttonPanel, PAGE_END);
    }
}
