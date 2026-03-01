package custom.striker.gui.contextMenu;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.components.border.RoundedLineBorder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple context menu implementation that appears near a specified parent component and disposes itself when the
 * parent is removed or hidden.
 */
public class ContextMenu extends JWindow implements ActionListener {

    private final List<ContextMenuOption> options;
    private final int arc = 12;
    private final ContentPanel contentPanel;
    private Color menuBackgroundColor;

    /**
     * Create a context menu with the provided options. The menu will automatically dispose itself when the parent component
     * is removed from the component hierarchy or hidden.
     * @param parent Component to position the menu near and listen for removal/hidden events on
     * @param optionMap Map containing context menu options and the action to perform when they are selected.
     */
    protected ContextMenu(JComponent parent, Map<String, ContextMenuAction> optionMap) {
        options = new ArrayList<>();

        for (String optionName : optionMap.keySet()) {
            ContextMenuOption option = new ContextMenuOption(optionName, optionMap.get(optionName));
            option.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            option.setAlignmentX(Component.LEFT_ALIGNMENT);
            options.add(option);
        }

        menuBackgroundColor = ContextMenuOption.getDefaultBackgroundColor();
        contentPanel = buildContentPanel();

        options.forEach((option) -> {
            contentPanel.add(option);
            option.addActionListener(this);
        });

        configureParentListeners(parent);
        add(contentPanel);
        // Make the window background transparent so that only the rounded content panel is visible
        setBackground(new Color(0, 0, 0, 0));
        pack();
        applyWindowShape();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyWindowShape();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                applyWindowShape();
            }
        });
    }

    private ContentPanel buildContentPanel() {
        ContentPanel content = new ContentPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint rounded background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(menuBackgroundColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                g2.dispose();
            }

            @Override
            protected void paintChildren(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    Shape parentRound = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);

                    for (Component c : getComponents()) {
                        int cx = c.getX();
                        int cy = c.getY();
                        int cw = c.getWidth();
                        int ch = c.getHeight();
                        if (cw <= 0 || ch <= 0) continue;

                        Rectangle childRect = new Rectangle(cx, cy, cw, ch);
                        Area intersection = new Area(parentRound);
                        intersection.intersect(new Area(childRect));

                        if (intersection.contains(childRect)) {
                            // Child fully inside rounded parent — paint normally without additional clipping
                            Graphics gChild = g2.create(cx, cy, cw, ch);
                            try {
                                c.paint(gChild);
                            } finally {
                                gChild.dispose();
                            }
                        } else {
                            // Partial overlap — clip to the intersection translated into child's coord space
                            Graphics2D gChild = (Graphics2D) g2.create(cx, cy, cw, ch);
                            try {
                                AffineTransform at = AffineTransform.getTranslateInstance(-cx, -cy);
                                Shape childClip = at.createTransformedShape(intersection);
                                Shape prevClip = gChild.getClip();
                                gChild.setClip(childClip);
                                c.paint(gChild);
                                gChild.setClip(prevClip);
                            } finally {
                                gChild.dispose();
                            }
                        }
                    }
                } finally {
                    g2.dispose();
                }

                if (getBorder() != null) {
                    getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
                }
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new RoundedLineBorder(Color.BLACK, 12));
        content.setBackground(menuBackgroundColor);

        return content;
    }

    private void applyWindowShape() {
        try {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;
            int arc = 12;
            setShape(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        } catch (Throwable t) {
            // setShape may not be supported on some platforms / JVMs or could throw SecurityException.
            // Fall back to default rectangular window. Do nothing.
        }
    }

    public void setOptionBackgroundColor(Color color) {
        menuBackgroundColor = color;
        contentPanel.setBackground(menuBackgroundColor);
        contentPanel.repaint();
        for (ContextMenuOption option : options) {
            option.setBackgroundColor(color);
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
