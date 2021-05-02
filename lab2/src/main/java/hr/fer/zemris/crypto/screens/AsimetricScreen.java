package hr.fer.zemris.crypto.screens;

import hr.fer.zemris.crypto.actions.ReadFile;
import hr.fer.zemris.crypto.actions.SaveFile;
import hr.fer.zemris.crypto.domain.CLogic;
import hr.fer.zemris.crypto.domain.RSAKeyPair;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

import static java.awt.BorderLayout.*;

public class AsimetricScreen extends JPanel {

    private final JTextArea myPrivateKeyTextField;
    private final JTextArea otherPublicKeyTextField;
    private final JButton myPrivateKeyLoadButton;
    private final JButton otherPublicKeyLoadButton;
    private final JTextArea inTextArea;
    private final JTextArea outTextArea;
    private final JButton encryptButton;
    private final JButton decryptButton;
    private final JButton generateButton;

    public AsimetricScreen() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        myPrivateKeyTextField = new JTextArea("12345678901234567890123456789012", 2, 90);
        myPrivateKeyLoadButton = new JButton(new ReadFile("Load key", this, myPrivateKeyTextField));
        otherPublicKeyTextField = new JTextArea("1234567890123456", 2, 90);
        otherPublicKeyLoadButton = new JButton(new ReadFile("Load key", this, otherPublicKeyTextField));
        inTextArea = new JTextArea("Sample text", 15, 100);
        outTextArea = new JTextArea("Encrypted text", 15, 100);
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        generateButton = new JButton("Generate");

        initGui();
        initActions();
    }

    private void initActions() {
        encryptButton.addActionListener(e -> outTextArea.setText(Base64.getEncoder().encodeToString(CLogic.encrypt(inTextArea.getText(), otherPublicKeyTextField.getText()))));
        decryptButton.addActionListener(e -> inTextArea.setText(CLogic.decrypt(outTextArea.getText(), myPrivateKeyTextField.getText())));

        generateButton.addActionListener(e -> {
            var keyPair = new RSAKeyPair();
            myPrivateKeyTextField.setText(keyPair.getPrivateKey());
            otherPublicKeyTextField.setText(keyPair.getPublicKey());
        });
    }

    private void initGui() {
        setLayout(new BorderLayout());

        //top
        JPanel keyPanel = new JPanel(new GridLayout(3, 1));
        JPanel upperTop = new JPanel();
        JPanel centerTop = new JPanel();
        JPanel lowerTop = new JPanel();
        upperTop.add(new JLabel("Sender private key:"));
        upperTop.add(new JScrollPane(myPrivateKeyTextField));
        upperTop.add(myPrivateKeyLoadButton);
        centerTop.add(new JLabel("Receiver public key: "));
        centerTop.add(new JScrollPane(otherPublicKeyTextField));
        centerTop.add(otherPublicKeyLoadButton);
        lowerTop.add(generateButton);
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
