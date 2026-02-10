package custom.striker.gui.contextMenu;

import java.awt.event.MouseEvent;

/**
 * Utility class to manage the visibility of context menus. Ensures that only one menu is visible at a time.
 */
public class ContextMenuManager {

    private static ContextMenu activeMenu = null;

    /**
     * Shows the provided context menu at the location of the provided mouse event. Hides any other active menu.
     * @param menu The menu to show
     * @param event The mouse event to get the location to show the menu at
     */
    public static void showMenu(ContextMenu menu, MouseEvent event) {
        if (activeMenu != null && activeMenu != menu && activeMenu.isVisible()) {
            activeMenu.setVisible(false);
        }
        activeMenu = menu;
        activeMenu.setVisible(true);
        activeMenu.setLocation(event.getXOnScreen() + 5, event.getYOnScreen() + 5);
    }
}
