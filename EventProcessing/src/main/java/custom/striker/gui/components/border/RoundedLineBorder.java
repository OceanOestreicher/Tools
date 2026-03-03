package custom.striker.gui.components.border;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * A simple rounded line border with configurable thickness and arc (corner radius).
 */
public class RoundedLineBorder extends AbstractBorder {

    private final Color color;
    private int thickness;
    private final int arc;

    public RoundedLineBorder(Color color, int arc) {
        this.color = color;
        this.thickness = 1;
        this.arc = Math.max(0, arc);
    }

    public void setThickness(int thickness) {
        this.thickness = Math.max(1, thickness);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int pad = thickness;
        return new Insets(pad, pad, pad, pad);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        int pad = thickness;
        insets.top = insets.left = insets.bottom = insets.right = pad;
        return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
        } finally {
            g2.dispose();
        }
    }
}
