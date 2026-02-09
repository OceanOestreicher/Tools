package custom.striker.gui.contextMenu;

import custom.striker.gui.ContentPanel;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * A simple context menu implementation that appears near a specified parent component and disposes itself when the
 * parent is removed or hidden.
 */
public class ContextMenu extends JWindow {

    protected ContextMenu(JComponent parent, List<String> options) {
        ContentPanel content = new ContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        for (String option : options) {
            JLabel label = new JLabel(option);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setOpaque(false);
            content.add(label);
        }

        configureParentListeners(parent);
        add(content);
        pack();
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
}
