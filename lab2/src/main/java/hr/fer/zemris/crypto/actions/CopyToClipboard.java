package hr.fer.zemris.crypto.actions;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.Serial;

public class CopyToClipboard extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JTextComponent textComponent;

    public CopyToClipboard(String name, JTextComponent textComponent) {
        super(name);
        this.textComponent = textComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(textComponent.getText()), null);
    }
}
