package custom.striker.gui.components.optionSelector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Component representing an option in an {@link OptionSelector}. Displays the provided text and performs the provided
 * action when clicked.
 */
public class Option extends JLabel {
    protected static final int OPTION_DIRTY = 1;
    protected static final int OPTION_SELECTED = 2;

    private static final Border INSETS = new EmptyBorder(7,7,7,7);

    private final OptionAction action;
    private String displayText;
    private final List<ActionListener> listeners;

    private boolean isSelected = false;
    private boolean isDirty = false;

    private Border selectedBorder = new LineBorder(Color.black);
    private Border unselectedBorder = new EmptyBorder(1,1,1,1);
    private Border hoveredBorder = new LineBorder(Color.gray);

    /**
     * Creates an Option with the provided display text and action to perform when clicked
     * @param text Text to display for this option
     * @param action Action to perform when this option is clicked
     */
    public Option(String text, OptionAction action) {
        super(text);

        this.displayText = text;
        this.action = action;
        this.listeners = new ArrayList<>();
        setBorder(new CompoundBorder(unselectedBorder, INSETS));
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                fireActionPerformed(OPTION_SELECTED);
                setSelected(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    setBorder(new CompoundBorder(hoveredBorder, INSETS));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    setBorder(new CompoundBorder(unselectedBorder, INSETS));
                }
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;

        if (selected) {
            setBorder(new CompoundBorder(selectedBorder, INSETS));
            performAction();
        } else {
            setBorder(new CompoundBorder(unselectedBorder, INSETS));
        }
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void fireActionPerformed(int command) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command + "");
        for (ActionListener listener: listeners) {
            listener.actionPerformed(event);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        displayText = text;
    }

    /**
     * Sets whether this option is dirty. A dirty option will display an asterisk next to its text.
     * @param dirty Whether this option is dirty
     */
    public void setDirty(boolean dirty) {
        this.isDirty = dirty;

        if (dirty) {
            super.setText(displayText + "*");
        } else {
            super.setText(displayText);
        }

        fireActionPerformed(OPTION_DIRTY);
    }

    public boolean isDirty() {
        return isDirty;
    }

    /**
     * Performs the action associated with this option. This is called automatically when this option is selected,
     * but can
     */
    public void performAction() {
        if (action == null) {
            return;
        }
        action.execute();
    }

    public void setSelectedBorder(Border selectedBorder) {
        this.selectedBorder = selectedBorder;
    }

    public void setUnselectedBorder(Border unselectedBorder) {
        this.unselectedBorder = unselectedBorder;
    }

    public void setHoveredBorder(Border hoveredBorder) {
        this.hoveredBorder = hoveredBorder;
    }
}
