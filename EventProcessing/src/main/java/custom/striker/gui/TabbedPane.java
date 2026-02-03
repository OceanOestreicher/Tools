package custom.striker.gui;

import custom.striker.gui.debug.GuiDebuggingService;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link JTabbedPane} that provides for support for debugging with the {@link GuiDebuggingService}
 */
public class TabbedPane extends JTabbedPane {

    @Override
    public void addTab(String title, Component component) {
        super.addTab(title, component);
        updateComponentHierarchy(component);
    }

    private void updateComponentHierarchy(Component comp) {
        GuiDebuggingService.updateHierarchyTree(this, comp);
    }
}
