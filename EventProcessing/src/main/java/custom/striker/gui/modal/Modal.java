package custom.striker.gui.modal;

import custom.striker.gui.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import custom.striker.gui.ContentPanel;

/**
 * A modal to show to the user
 */
public class Modal extends Frame {

    private final JButton confirm;

    public Modal(Frame parent, String message) {
        super(parent);

        JLabel label = new JLabel(message);
        addComponent(label, BorderLayout.CENTER);

        confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        ContentPanel south = new ContentPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.add(confirm);
        addComponent(south, BorderLayout.SOUTH);

        setParentIsDisabled(true);
    }

    public void addActionListener(ActionListener listener) {
        confirm.addActionListener(listener);
    }

    public void setConfirmationText(String text) {
        confirm.setText(text);
    }
}
