package custom.striker.gui;

import custom.striker.gui.debug.DebugInspector;
import custom.striker.gui.debug.GuiDebuggingService;

import javax.swing.*;
import java.awt.*;

/**
 * Simple custom {@link JPanel}. Works with the {@link GuiDebuggingService} and {@link DebugInspector} to
 * provide debugging information about a JSwing application.
 */
public class ContentPanel extends JPanel {

    @Override
    public void add(Component comp, Object constraints)  {
        super.add(comp, constraints);
        updateComponentHierarchy(comp);
    }

    @Override
    public Component add(Component comp)  {
        super.add(comp);
        updateComponentHierarchy(comp);
        return comp;
    }

    @Override
    public void removeAll() {
        for (Component component: getComponents()) {
            GuiDebuggingService.removeComponentFromTree(component);
        }

        super.removeAll();
    }

    @Override
    public void remove(Component component) {
        super.remove(component);
        GuiDebuggingService.removeComponentFromTree(component);
    }

    private void updateComponentHierarchy(Component comp) {
        GuiDebuggingService.updateHierarchyTree(this, comp);
    }
}
