package hr.fer.zemris.crypto.actions;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class ReadFile extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Component parent;
    private final JTextComponent textField;

    public ReadFile(String name, Component parent, JTextComponent textField) {
        super(name);
        this.parent = parent;
        this.textField = textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Open file");
        if (jfc.showOpenDialog(parent) != APPROVE_OPTION)
            return;

        Path openedFilePath = jfc.getSelectedFile().toPath();
        if (!Files.isReadable(openedFilePath)) {
            showMessageDialog(parent, "noReadPersmision", "error",
                    ERROR_MESSAGE);
            return;
        }

        try {
            textField.setText(Files.readString(openedFilePath));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
