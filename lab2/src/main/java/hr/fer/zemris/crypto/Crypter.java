package hr.fer.zemris.crypto;

import hr.fer.zemris.crypto.screens.*;

import javax.swing.*;
import java.awt.*;

public class Crypter extends JFrame {

    private static final int DEFAULT_HEIGHT = 800;
    private static final int DEFAULT_WIDTH = 1500;

    private final SimetricScreen simetricScreen;
    private final AsimetricScreen asimetricScreen;
    private final HashScreen hashScreen;
    private final ReceiveEnvelopeScreen receiveEnvelopeScreen;
    private final SendEnvelopeScreen sendEnvelopeScreen;

    public Crypter() {
        simetricScreen = new SimetricScreen();
        asimetricScreen = new AsimetricScreen();
        hashScreen = new HashScreen();
        receiveEnvelopeScreen = new ReceiveEnvelopeScreen();
        sendEnvelopeScreen = new SendEnvelopeScreen();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setLocationRelativeTo(null);

        initGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Crypter().setVisible(true));
    }

    private void initGUI() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        initializeScreens();
    }

    private void initializeScreens() {
        JTabbedPane screens = new JTabbedPane();
        screens.add("Simetric", simetricScreen);
        screens.add("Asimetric", asimetricScreen);
        screens.add("Hash", hashScreen);
        screens.add("Envelope & Signature - Encode", sendEnvelopeScreen);
        screens.add("Envelope & Signature - Decode", receiveEnvelopeScreen);
        getContentPane().add(screens);
    }
}
