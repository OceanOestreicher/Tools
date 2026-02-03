package custom.striker.gui.debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.Frame;
import custom.striker.gui.TabbedPane;
import custom.striker.gui.ScrollPane;

/**
 * Utility to aid with debugging Swing applications. Uses the following jvm args:
 * <br><br>
 *    - debugGui: Enables the debugger. Toggle it with Ctrl + i
 * <br><br>
 *    - filterSwingComponents: Filters out any components that are simple JSwing components.
 *
 * The Inspector automatically filters out basic components that this library provides such as {@link ContentPanel},
 * {@link ScrollPane} etc.
 */
public class DebugInspector {

    public static final boolean DEBUG_ENABLED = Boolean.getBoolean("debugGui");
    private static final boolean FILTER_SWING_COMPONENTS = Boolean.getBoolean("filterSwingComponents");
    private static boolean debugMode = false;
    private static JWindow debugTooltip;
    private static String displayedText = "";
    private static final List<Class<?>> COMPONENT_CLASSES_TO_IGNORE = List.of(
            ContentPanel.class,
            ScrollPane.class,
            TabbedPane.class
    );

    /**
     * Install the debug inspector on the provided {@link Frame}
     * @param frame Frame to enable debug inspecting on
     */
    public static void installDebugInspector(Frame frame) {
        KeyEventDispatcher debugDispatcher = new KeyEventDispatcher() {
            private boolean ctrlDown = false;

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                        ctrlDown = true;
                    } else if (e.getKeyCode() == KeyEvent.VK_I && ctrlDown) {
                        debugMode = !debugMode;
                        System.out.println("Gui Debug Mode: " + (debugMode ? "ON" : "OFF"));
                        if (!debugMode) hideTooltip();
                        return true;
                    }
                } else if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlDown = false;
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(debugDispatcher);

        AWTEventListener debugListener = event -> {
            if (!debugMode || !(event instanceof MouseEvent me) || me.getID() != MouseEvent.MOUSE_MOVED || !frame.isEnabled() || !frame.isFocused()) {
                return;
            }
            // Get the source component where the event originated
            Component sourceComponent = (Component) me.getSource();

            // Get the point relative to that source component
            Point pointRelativeToSource = me.getPoint();

            // Convert the point to be relative to the frame the component is in
            Point pointRelativeToFrame = SwingUtilities.convertPoint(sourceComponent, pointRelativeToSource, frame);

            List<Component> componentsAtPoint = GuiDebuggingService.getComponentsAtPoint(frame, pointRelativeToFrame).stream()
                    .filter(component -> !COMPONENT_CLASSES_TO_IGNORE.contains(component.getClass()) &&
                            !(FILTER_SWING_COMPONENTS && component.getClass().getName().contains("javax.swing")))
                    .toList();

            if (componentsAtPoint != null && !componentsAtPoint.isEmpty()) {
                String className = componentsAtPoint.stream()
                        .map(component -> component.getClass().getName())
                        .reduce("", (acc, val) -> acc + val + "\n");
                className = className.substring(0, className.lastIndexOf("\n"));
                Point screenPoint = me.getLocationOnScreen();
                showTooltip(className, screenPoint);
            } else {
                hideTooltip();
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(debugListener, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(debugDispatcher);
                Toolkit.getDefaultToolkit().removeAWTEventListener(debugListener);

                if (debugTooltip != null) {
                    debugTooltip.dispose();
                }
            }
        });
    }

    private static void showTooltip(String text, Point location) {
        int xOffset = 15;
        int yOffset = 20;

        if (debugTooltip == null) {
            debugTooltip = new JWindow();
            debugTooltip.setFocusableWindowState(false);
            debugTooltip.setAlwaysOnTop(true);
            debugTooltip.getContentPane().setLayout(new BorderLayout());
        }

        // Check if the text is different
        if (!text.equals(displayedText)) {
            displayedText = text;
            JTextArea textArea = new JTextArea(text);
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            textArea.setBackground(new Color(255, 255, 200));
            textArea.setOpaque(true);

            debugTooltip.getContentPane().removeAll();
            debugTooltip.getContentPane().add(textArea, BorderLayout.CENTER);
            debugTooltip.pack();
        }

        // These lines must run every time the method is called:
        debugTooltip.setLocation(location.x + xOffset, location.y + yOffset);

        // Ensure the window is visible
        if (!debugTooltip.isVisible()) {
            debugTooltip.setVisible(true);
        }
    }

    private static void hideTooltip() {
        if (debugTooltip != null && debugTooltip.isVisible()) {
            debugTooltip.setVisible(false);
        }
    }
}


