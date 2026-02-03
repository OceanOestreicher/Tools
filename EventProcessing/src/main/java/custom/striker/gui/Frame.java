package custom.striker.gui;

import custom.striker.gui.debug.GuiDebuggingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A Frame used by a GUI application. This frame comes preconfigured with a content panel with a Border Layout and
 * has a default behavior of dispose on close
 */
public class Frame extends JFrame {

    private final Frame parent;
    private final ContentPanel content;
    private boolean parentIsDisabled = true;

    public Frame() {
        this(null);
    }

    /**
     * Constructor which links this frame to the parent frame. By default, the parent is disabled
     * while this frame is being displayed.
     * @param parent The parent frame of this frame.
     */
    public Frame(Frame parent) {
        content = new ContentPanel();

        content.setLayout(new BorderLayout());

        add(content);

        this.parent = parent;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (parent == null) {
                    return;
                }
                parent.setEnabled(true);
                GuiDebuggingService.unregisterFrame(Frame.this);
            }
        });

        GuiDebuggingService.registerFrame(this);
    }

    /**
     * Adds the component at the provided position in this Frame's content panel
     * @param component Component to add
     * @param borderLayoutPosition String representing a possible position of a BorderLayout to layout the component in.
     */
    public void addComponent(Component component, String borderLayoutPosition) {
        content.add(component, borderLayoutPosition);
    }

    public void setParentIsDisabled(boolean parentIsDisabled) {
        this.parentIsDisabled = parentIsDisabled;
    }

    public void display() {
        pack();
        setLocationRelativeTo(parent);
        if (parent != null) {
            parent.setEnabled(!parentIsDisabled);
        }
        setEnabled(true);
        setVisible(true);
    }

    public ContentPanel getContentPanel() {
        return content;
    }
}
