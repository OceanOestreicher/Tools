package custom.striker.gui.components;

import custom.striker.gui.ContentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

/**
 * A circular spinner overlay to indicate loading or processing.
 */
public class CircleSpinner extends ContentPanel {

    private static final int OVERLAY_ALPHA = 102; // 40%
    private static final int SPINNER_DIAMETER = 32;
    private static final float SPINNER_STROKE = 4f;
    private static final int ARC_SWEEP = 270;

    private final Timer timer;

    private double angleDeg = 0.0;
    private boolean spinning = false;

    public CircleSpinner() {
        setOpaque(false);
        setVisible(false);
        timer = new Timer(16, e -> {
            angleDeg += 6.0;
            if (angleDeg >= 360.0) {
                angleDeg -= 360.0;
            }
            repaint();
        });
    }

    public void setSpinning(boolean spinning) {
        if (this.spinning == spinning) {
            return;
        }
        this.spinning = spinning;
        setVisible(spinning);
        if (spinning) {
            timer.start();
        } else {
            timer.stop();
        }
        repaint();
    }

    public boolean isSpinning() {
        return spinning;
    }

    @Override
    public boolean contains(int x, int y) {
        // Let mouse events pass through when this visual-only overlay is visible.
        return false;
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!spinning) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2.setColor(new Color(0, 0, 0, OVERLAY_ALPHA));
            g2.fillRect(0, 0, getWidth(), getHeight());

            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
            double radius = SPINNER_DIAMETER / 2.0;
            double x = centerX - radius;
            double y = centerY - radius;

            g2.setStroke(new BasicStroke(SPINNER_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawOval((int) Math.round(x), (int) Math.round(y), SPINNER_DIAMETER, SPINNER_DIAMETER);

            Arc2D.Double arc = new Arc2D.Double(x, y, SPINNER_DIAMETER, SPINNER_DIAMETER, 0.0, ARC_SWEEP, Arc2D.OPEN);
            g2.setColor(new Color(245, 245, 245));
            AffineTransform old = g2.getTransform();
            g2.rotate(-Math.toRadians(angleDeg), centerX, centerY);
            g2.draw(arc);
            g2.setTransform(old);
        } finally {
            g2.dispose();
        }
    }
}
