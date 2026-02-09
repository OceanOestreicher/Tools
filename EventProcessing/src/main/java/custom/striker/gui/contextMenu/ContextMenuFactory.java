package custom.striker.gui.contextMenu;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Factory class for creating and managing context menus in the application.
 * Ensures that only one context menu is active at a time and handles mouse events to show/hide the menu.
 */
public class ContextMenuFactory {

    private static ContextMenu activeMenu = null;

    private static MouseAdapter activeAdapter = null;

    /**
     * Creates a new context menu with the specified options and attaches it to the given parent component.
     * @param parent the component to which the context menu will be attached
     * @param options the list of options to be displayed in the context menu
     * @return the created ContextMenu instance
     */
    public static ContextMenu createContextMenu(JComponent parent, List<String> options) {
        if (activeMenu != null) {
            activeMenu.dispose();
        }

        ContextMenu menu = new ContextMenu(parent, options);
        activeMenu = menu;
        configureMouseAdapter(parent);
        return menu;
    };

    private static void configureMouseAdapter(JComponent parent) {
        if (activeAdapter != null) {
            parent.removeMouseListener(activeAdapter);
        }
        activeAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showMenu(evt);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    showMenu(evt);
                } else {
                    activeMenu.dispose();
                }
            }

            private void showMenu(MouseEvent evt) {
                activeMenu.setLocation(evt.getXOnScreen() + 5, evt.getYOnScreen() + 5);
                activeMenu.setVisible(true);
            }
        };

        parent.addMouseListener(activeAdapter);
        parent.addMouseMotionListener(activeAdapter);
    }
}
