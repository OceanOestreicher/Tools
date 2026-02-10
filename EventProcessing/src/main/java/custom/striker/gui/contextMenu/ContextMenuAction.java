package custom.striker.gui.contextMenu;

/**
 * Functional interface for actions to be performed when a context menu item is clicked
 */
@FunctionalInterface
public interface ContextMenuAction {

    /**
     * Method to execute when the context menu item is clicked
     */
    void perform();
}
