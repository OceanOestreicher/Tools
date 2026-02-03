package custom.striker.gui;

import custom.striker.gui.debug.GuiDebuggingService;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link JScrollPane} that provides for support for debugging with the {@link GuiDebuggingService}
 */
public class ScrollPane extends JScrollPane {

    public ScrollPane(Component view) {
        super(view);
        updateComponentHierarchy(view);
    }

    private void updateComponentHierarchy(Component comp) {
        GuiDebuggingService.updateHierarchyTree(this, comp);
    }
}
