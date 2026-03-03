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
        // Record the event time so the menu itself can ignore the opening click when listening globally
        menu.setShowTriggerEventTime(event.getWhen());

        activeMenu = menu;
        activeMenu.setLocation(event.getXOnScreen() + 5, event.getYOnScreen() + 5);
        activeMenu.setVisible(true);
    }

    /**
     * Hide the provided menu if it is the active one and clear the active menu reference.
     * This centralizes active-menu tracking and ensures only the manager clears the active menu state.
     */
    public static void hideMenu(ContextMenu menu) {
        if (menu == null) return;
        if (activeMenu == menu) {
            activeMenu = null;
        }
        // Ensure the menu is hidden
        if (menu.isVisible()) {
            menu.setVisible(false);
        }
    }
}
