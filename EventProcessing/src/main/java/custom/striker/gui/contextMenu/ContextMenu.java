package custom.striker.gui.contextMenu;

import custom.striker.gui.ContentPanel;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A simple context menu implementation that appears near a specified parent component and disposes itself when the
 * parent is removed or hidden.
 */
public class ContextMenu extends JWindow implements ActionListener {

    private List<ContextMenuOption> options;

    /**
     * Create a context menu with the provided options. The menu will automatically dispose itself when the parent component
     * is removed from the component hierarchy or hidden.
     * @param parent Component to position the menu near and listen for removal/hidden events on
     * @param optionMap Map containing context menu options and the action to perform when they are selected.
     */
    protected ContextMenu(JComponent parent, Map<String, ContextMenuAction> optionMap) {
        ContentPanel content = new ContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        options = new ArrayList<>();

        for (String optionName : optionMap.keySet()) {
            ContextMenuOption option = new ContextMenuOption(optionName, optionMap.get(optionName));
            option.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            option.setAlignmentX(Component.LEFT_ALIGNMENT);
            options.add(option);
        }

        options.forEach((option) -> {
            content.add(option);
            option.addActionListener(this);
        });

        configureParentListeners(parent);
        add(content);
        pack();
    }

    public void setOptionBackgroundColor(Color color) {
        for (ContextMenuOption option : options) {
            option.setBackground(color);
        }
    }

    public void setOptionHoverColor(Color color) {
        for (ContextMenuOption option : options) {
            option.setHoverColor(color);
        }
    }

    public void setOptionTextColor(Color color) {
        for (ContextMenuOption option : options) {
            option.setForegroundColor(color);
        }
    }

    public void setOptionSelectedColor(Color color) {
        for (ContextMenuOption option : options) {
            option.setPressedColor(color);
        }
    }

    private void configureParentListeners(JComponent parent) {

        parent.addAncestorListener(new AncestorListener() {
            @Override public void ancestorAdded(AncestorEvent e) {}
            @Override public void ancestorMoved(AncestorEvent e) {}
            @Override public void ancestorRemoved(AncestorEvent e) {
                safeDispose();
            }
        });

        // Detect top-level window dispose or showing/displayability changes
        parent.addHierarchyListener(e -> {
            long flags = e.getChangeFlags();
            if ((flags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                Window w = javax.swing.SwingUtilities.getWindowAncestor(parent);
                if (w == null || !w.isDisplayable()) { // disposed -> not displayable
                    safeDispose();
                }
            }
            if ((flags & HierarchyEvent.SHOWING_CHANGED) != 0 && !parent.isShowing()) {
                safeDispose();
            }
        });
    }

    private void safeDispose() {
        if (!isDisplayable()) {
            return;
        }
        SwingUtilities.invokeLater(this::dispose);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }
}
