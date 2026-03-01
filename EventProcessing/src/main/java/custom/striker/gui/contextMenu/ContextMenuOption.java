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

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(210, 210, 210);

    private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color hoverColor = new Color(220, 220, 220);
    private Color pressedColor = new Color(230, 230, 230);

    private final Set<ActionListener> listeners;
    private final JLabel optionLabel;

    /**
     * Creates a ContextMenuOption with the provided display name and action to perform when clicked
     * @param displayName Name to display for this option
     * @param action Action to perform when this option is clicked
     */
    protected ContextMenuOption(String displayName, ContextMenuAction action) {
        setBackgroundColor(backgroundColor);
        optionLabel = new JLabel(displayName);
        optionLabel.setBackground(backgroundColor);
        add(optionLabel);
        listeners = new HashSet<>();

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                updateBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateBackground(backgroundColor);
                fireActionPerformed();
                action.perform();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                updateBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateBackground(backgroundColor);
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void updateBackground(Color color) {
        setBackground(color);
        repaint();
        repaintMenu();
    }

    private void repaintMenu() {
        Container parent = getParent();
        if (parent != null) {
            parent.repaint();
            return;
        }
        Component ancestor = SwingUtilities.getAncestorOfClass(ContentPanel.class, this);
        if (ancestor != null) {
            ancestor.repaint();
        }
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
        updateBackground(backgroundColor);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getDefaultBackgroundColor() {
        return DEFAULT_BACKGROUND_COLOR;
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        } finally {
            g2.dispose();
        }
    }
}
