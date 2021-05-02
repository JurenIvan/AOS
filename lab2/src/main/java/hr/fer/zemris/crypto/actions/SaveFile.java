package hr.fer.zemris.crypto.actions;

import hr.fer.zemris.crypto.domain.Utils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.*;

public class SaveFile extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Component parent;
    private final JTextComponent textComponent;

    public SaveFile(String name, Component parent, JTextComponent textComponent) {
        super(name);
        this.parent = parent;
        this.textComponent = textComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Save file");
        if (jfc.showSaveDialog(parent) != APPROVE_OPTION) {
            showMessageDialog(parent, "Nothing saved", "warning", INFORMATION_MESSAGE);
            return;
        }
        try {
            Files.writeString(jfc.getSelectedFile().toPath(), textComponent.getText());
        } catch (IllegalStateException | IOException e2) {
            showMessageDialog(parent, "Nothing Saved", "error", ERROR_MESSAGE);
        }
    }
}
