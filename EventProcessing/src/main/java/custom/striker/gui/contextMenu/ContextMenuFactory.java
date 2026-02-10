package custom.striker.gui.contextMenu;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory class for creating and managing context menus in the application.
 */
public class ContextMenuFactory {

    /**
     * Creates a new context menu with the specified options and attaches it to the given parent component.
     * @param parent the component to which the context menu will be attached
     * @param options the list of options to be displayed in the context menu
     * @return the created ContextMenu instance
     */
    public static ContextMenu createContextMenu(JComponent parent, Map<String, ContextMenuAction> options) {
        ContextMenu menu = new ContextMenu(parent, options);
        configureMouseAdapter(parent, menu);
        return menu;
    };

    private static void configureMouseAdapter(JComponent parent, ContextMenu menu) {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    ContextMenuManager.showMenu(menu, evt);
                }

                menu.setVisible(false);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    ContextMenuManager.showMenu(menu, evt);
                } else {
                    menu.setVisible(false);
                }
            }
        };

        parent.addMouseListener(adapter);
        parent.addMouseMotionListener(adapter);
    }
}
