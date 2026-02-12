package custom.striker.gui.contextMenu;

import custom.striker.gui.ContentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Component representing an option in a {@link ContextMenu}. Displays the provided name and performs the provided
 * action when clicked
 */
public class ContextMenuOption extends ContentPanel {

    private Color backgroundColor = new Color(220, 220, 220);
    private Color hoverColor = new Color(230, 230, 230);
    private Color pressedColor = new Color(245, 245, 245);

    private final Set<ActionListener> listeners;
    private final JLabel optionLabel;

    /**
     * Creates a ContextMenuOption with the provided display name and action to perform when clicked
     * @param displayName Name to display for this option
     * @param action Action to perform when this option is clicked
     */
    protected ContextMenuOption(String displayName, ContextMenuAction action) {
        optionLabel = new JLabel(displayName);
        add(optionLabel);
        listeners = new HashSet<>();

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(backgroundColor);
                fireActionPerformed();
                action.perform();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(backgroundColor);
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    protected void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SELECTED");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
    }

    public void setForegroundColor(Color foregroundColor) {
        optionLabel.setForeground(foregroundColor);
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }
}
